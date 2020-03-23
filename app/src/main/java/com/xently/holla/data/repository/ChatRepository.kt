package com.xently.holla.data.repository

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseUser
import com.xently.holla.Log
import com.xently.holla.data.model.Chat
import com.xently.holla.data.model.Chat.CREATOR.Fields
import com.xently.holla.data.model.Contact
import com.xently.holla.data.repository.schema.IChatRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class ChatRepository internal constructor(context: Context) : BaseRepository(context),
    IChatRepository {

    private val observableConversationList = MutableLiveData<List<Chat>>(null)
    private val observableMessageList = MutableLiveData<List<Chat>>(null)

    override suspend fun getObservableConversations(contact: Contact?): LiveData<List<Chat>> {
        return if (contact == null) observableConversationList else Transformations.map(
            observableMessageList
        ) { chatList ->
            if (chatList == null) return@map null
            chatList.sortedByDescending { it.timeSent }
        }
    }

    override suspend fun sendMessage(message: Chat): Result<Void> {
        val messageId = messagesCollection.document().id
        return messagesCollection.document(messageId)
            .set(message.copy(id = messageId, senderId = firebaseAuth.currentUser?.uid.toString()))
            .execute()
    }

    override suspend fun deleteMessage(message: Chat): Task<Void> {
        return messagesCollection.document(message.id).delete().addOnCompleteListener {
            if (it.isSuccessful) runBlocking { observableMessageList.deleteChatIfPresent(message) }
        }
    }

    override suspend fun getConversations(contact: Contact?): List<Chat> {
        val observable = if (contact == null) observableConversationList else observableMessageList
        try {
            val currentUser =
                firebaseAuth.currentUser ?: throw Exception("Authentication is required")
            val currentUserId = currentUser.uid
            val chatListAsSender = if (contact == null) {
                messagesCollection.whereEqualTo(Fields.SENDER, currentUserId)
            } else {
                messagesCollection.whereEqualTo(Fields.SENDER, contact.id)
                    .whereEqualTo(Fields.RECEIVER, currentUserId)
            }.get().await().toObjects(Chat::class.java)

            val chatListAsReceiver = if (contact == null) {
                messagesCollection.whereEqualTo(Fields.RECEIVER, currentUserId)
            } else {
                messagesCollection.whereEqualTo(Fields.RECEIVER, contact.id)
                    .whereEqualTo(Fields.SENDER, currentUserId)
            }.get().await().toObjects(Chat::class.java)

            val chatList = if (contact == null) {
                (chatListAsReceiver + chatListAsSender).getConversations(currentUser)
            } else {
                (chatListAsReceiver + chatListAsSender)
            }.withContacts(currentUser)

            observable.refreshList(chatList)

            return chatList
        } catch (ex: Exception) {
            Log.show("FCMService", ex.message, ex, Log.Type.ERROR) // TODO
            setException(ex)
            observable.refreshList(emptyList())
            return emptyList()
        }
    }

    private suspend fun List<Chat>.getConversations(user: FirebaseUser): List<Chat> =
        withContext(Dispatchers.Default) {
            val conversations = arrayListOf<Chat>()
            // Group chats by recipients(contact) id then scan through each of them to get the latest
            val g1 = groupBy { it.senderId }
            for (group in g1) {
                // Add the latest message to the conversation list
                conversations.add(group.value.sortedByDescending { it.timeSent }[0])
            }
            conversations.sortedByDescending { it.timeSent }
        }

    private suspend fun List<Chat>.withContacts(user: FirebaseUser): ArrayList<Chat> {
        val userId = user.uid
        val chatList = arrayListOf<Chat>()

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

    private suspend fun MutableLiveData<List<Chat>>.deleteChatIfPresent(message: Chat) {
        withContext(Dispatchers.Default) {
            value?.filter { it == message }?.let { refreshList(it) }
        }
    }

    private fun MutableLiveData<List<Chat>>.refreshList(list: List<Chat>?) {
        postValue(list)
    }
}