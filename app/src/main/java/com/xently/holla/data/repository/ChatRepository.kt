package com.xently.holla.data.repository

import android.content.Context
import android.provider.ContactsContract.CommonDataKinds.Phone
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

class ChatRepository internal constructor(private val context: Context) : BaseRepository(),
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

    override suspend fun sendMessage(message: Chat): Task<Void> {
        val messageId = messagesCollection.document().id
        return messagesCollection.document(messageId)
            .set(message.copy(id = messageId, senderId = firebaseAuth.currentUser?.uid.toString()))
            .addOnCompleteListener {
                if (it.exception != null) setException(it.exception)
            }
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
            setException(ex)
            observable.refreshList(emptyList())
            return emptyList()
        }
    }

    private val Contact.local: Contact
        get() {
            var contact = this
            context.contentResolver.query(
                Phone.CONTENT_URI,
                null,
                "${Phone.NORMALIZED_NUMBER} LIKE ?",
                arrayOf(contact.mobileNumber),
                null
            )?.use {
                while (it.moveToNext()) {
                    val name: String = it.getString(it.getColumnIndex(Phone.DISPLAY_NAME))
                    contact = contact.copy(name = name)
                }
            }

            return contact
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
                    val sender = chat.sender.copy(
                        id = userId,
                        mobileNumber = user.phoneNumber
                    ).local
                    val receiver =
                        usersCollection.whereEqualTo(Contact.CREATOR.Fields.ID, chat.receiverId)
                            .limit(1).get().await()
                    chatList += chat.copy(
                        sender = sender,
                        receiver = receiver.getObject(chat.receiver).local
                    )
                }
                chat.receiverId == userId -> {
                    val receiver = chat.receiver.copy(
                        id = userId,
                        mobileNumber = user.phoneNumber
                    ).local
                    val sender =
                        usersCollection.whereEqualTo(Contact.CREATOR.Fields.ID, chat.senderId)
                            .limit(1).get().await()
                    chatList += chat.copy(
                        sender = sender.getObject(chat.sender).local,
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