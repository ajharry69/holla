package com.xently.holla.data.source.remote

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.CollectionReference
import com.xently.holla.data.Result
import com.xently.holla.data.getObject
import com.xently.holla.data.model.Chat
import com.xently.holla.data.model.ChatCreator.Fields
import com.xently.holla.data.model.Contact
import com.xently.holla.data.model.Conversation
import com.xently.holla.data.model.Message
import com.xently.holla.data.source.schema.IConversationDataSource
import com.xently.holla.data.source.schema.IMessageDataSource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class MessageRemoteDataSource internal constructor(
    context: Context,
    private val remoteDataSource: IConversationDataSource?
) : BaseRemoteDataSource(context), IMessageDataSource {

    private val messagesCollection: CollectionReference
        get() = getMyMessagesCollection()

    private val observableMessageList = MutableLiveData<List<Message>>(null)

    override suspend fun getObservableChats(contact: Contact): LiveData<List<Message>> {
        return Transformations.map(observableMessageList) { chatList ->
            if (chatList == null) return@map null
            chatList.sortedByDescending { it.timeSent }
        }
    }

    override suspend fun sendMessage(message: Message): Result<Unit> {
        val messageId = messagesCollection.document().id
        val msg = message.copy(id = messageId, senderId = firebaseAuth.currentUser?.uid.toString())
        val result = messagesCollection.document(messageId).set(msg).execute()
        return if (result is Result.Success) {
            // TODO: Replace with could function method
            remoteDataSource?.saveConversation(
                Conversation(
                    id = msg.id,
                    body = msg.body,
                    receiverId = msg.receiverId,
                    senderId = msg.senderId,
                    type = Chat.Type.valueOf(msg.type.name),
                    mediaUrl = msg.mediaUrl,
                    sent = msg.sent,
                    read = msg.read,
                    deleteFromSender = msg.deleteFromSender,
                    deleteFromReceiver = msg.deleteFromReceiver,
                    timeSent = msg.timeSent,
                    sender = msg.sender,
                    receiver = msg.receiver
                )
            )
            Result.Success(Unit)
        } else result as Result.Error
    }

    override suspend fun sendMessages(messages: List<Message>): Result<Unit> {
        // TODO: Delete conversation if all messages in a chat have been deleted
        withContext(Dispatchers.IO) {
            messages.forEach {
                launch {
                    sendMessage(it)
                }
            }
        }
        return Result.Success(Unit)
    }

    override suspend fun deleteMessage(message: Message): Task<Void>? {
        // TODO: Use the NEW last sent text as the new conversation if the deleted message was the latest
        return messagesCollection.document(message.id).delete().addOnCompleteListener {
            if (it.isSuccessful) runBlocking { observableMessageList.deleteChatIfPresent(message) }
        }
    }

    override suspend fun getChats(contact: Contact): List<Message> {
        try {
            val currentUser =
                firebaseAuth.currentUser ?: throw Exception("Authentication is required")
            val currentUserId = currentUser.uid
            val chatListAsSender = withContext(Dispatchers.IO) {
                messagesCollection.whereEqualTo(Fields.SENDER, contact.id)
                    .whereEqualTo(Fields.RECEIVER, currentUserId)
                    .get().await().toObjects(Message::class.java)
            }

            val chatListAsReceiver = withContext(Dispatchers.IO) {
                messagesCollection.whereEqualTo(Fields.RECEIVER, contact.id)
                    .whereEqualTo(Fields.SENDER, currentUserId)
                    .get().await().toObjects(Message::class.java)
            }

            val chatList = (chatListAsReceiver + chatListAsSender).withContacts(currentUser)

            observableMessageList.refreshList(chatList)

            return chatList
        } catch (ex: Exception) {
            setException(ex)
            observableMessageList.refreshList(emptyList())
            return emptyList()
        }
    }

    private suspend fun List<Message>.withContacts(user: FirebaseUser): ArrayList<Message> {
        val userId = user.uid
        val chatList = arrayListOf<Message>()

        for (chat in this) {
            when {
                chat.senderId == userId -> {
                    val sender = getLocalContact(
                        chat.sender.copy(
                            id = userId,
                            mobileNumber = user.phoneNumber
                        )
                    )
                    val receiver =
                        usersCollection.whereEqualTo(Contact.CREATOR.Fields.ID, chat.receiverId)
                            .limit(1).get().await()
                    chatList += chat.copy(
                        sender = sender,
                        receiver = getLocalContact(receiver.getObject(chat.receiver))
                    )
                }
                chat.receiverId == userId -> {
                    val receiver = getLocalContact(
                        chat.receiver.copy(
                            id = userId,
                            mobileNumber = user.phoneNumber
                        )
                    )
                    val sender =
                        usersCollection.whereEqualTo(Contact.CREATOR.Fields.ID, chat.senderId)
                            .limit(1).get().await()
                    chatList += chat.copy(
                        sender = getLocalContact(sender.getObject(chat.sender)),
                        receiver = receiver
                    )
                }
            }
        }
        return chatList
    }

    private suspend fun MutableLiveData<List<Message>>.deleteChatIfPresent(message: Message) {
        withContext(Dispatchers.Default) {
            value?.filter { it == message }?.let { refreshList(it) }
        }
    }

    private fun MutableLiveData<List<Message>>.refreshList(list: List<Message>?) {
        postValue(list)
    }
}