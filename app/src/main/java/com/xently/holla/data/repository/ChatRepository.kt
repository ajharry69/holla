package com.xently.holla.data.repository

import android.content.Context
import android.provider.ContactsContract.CommonDataKinds.Phone
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.Query
import com.xently.holla.data.model.Chat
import com.xently.holla.data.model.Chat.CREATOR.Fields
import com.xently.holla.data.model.Contact
import com.xently.holla.data.repository.schema.IChatRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext

class ChatRepository internal constructor(private val context: Context) : BaseRepository(),
    IChatRepository {

    private val observableException = MutableLiveData<Exception>()
    private val observableMessageList = MutableLiveData<List<Chat>>(emptyList())

    override fun getObservableException(): LiveData<Exception> = observableException

    override suspend fun getObservableConversations(contact: Contact?): LiveData<List<Chat>> {
        return Transformations.map(observableMessageList) { chatList ->
            if (contact == null) chatList else {
                chatList.filter { it.receiverId == contact.id }
                    .sortedByDescending { it.timeSent }
            }
        }
    }

    override suspend fun sendMessage(message: Chat): Task<Void> {
        val messageId = messagesCollection.document().id
        return messagesCollection.document(messageId)
            .set(message.copy(id = messageId, senderId = firebaseAuth.currentUser?.uid.toString()))
    }

    override suspend fun deleteMessage(message: Chat): Task<Void> {
        return messagesCollection.document(message.id).delete().addOnCompleteListener {
            if (it.isSuccessful) runBlocking { observableMessageList.deleteChatIfPresent(message) }
        }
    }

    override suspend fun getConversations(contact: Contact?): ListenerRegistration? {
        val currentUser = firebaseAuth.currentUser ?: return null
        val currentUserId = currentUser.uid
//        val query = messagesCollection.whereEqualTo(Fields.SENDER, currentUserId)
        return if (contact == null) {
            messagesCollection.whereEqualTo(Fields.SENDER, currentUserId)
        } else {
            messagesCollection
        }.orderBy(Fields.TIME_SENT, Query.Direction.DESCENDING)
            .addSnapshotListener { querySnapshot, exception ->
                if (exception != null) {
                    setException(exception)
                    return@addSnapshotListener
                }

                if (querySnapshot == null || querySnapshot.isEmpty) return@addSnapshotListener

                val chatList = querySnapshot.toObjects(Chat::class.java)

                val (chats, observable) = if (contact == null) {
                    Pair(chatList.getConversations(), observableMessageList)
                } else Pair(chatList, observableMessageList)

                val nc = arrayListOf<Chat>()
                for (chat in chats) {
                    when {
                        chat.senderId == currentUserId -> {
                            val sender = chat.sender.copy(mobileNumber = currentUser.phoneNumber)
                            getContactFromSenderOrReceiverID(chat.receiverId) {
                                nc.add(chat.copy(sender = sender, receiver = it))
                                observable.refreshList(nc)
                            }
                        }
                        chat.receiverId == currentUserId -> {
                            val receiver =
                                chat.receiver.copy(mobileNumber = currentUser.phoneNumber)
                            getContactFromSenderOrReceiverID(chat.senderId) {
                                nc.add(chat.copy(receiver = receiver, sender = it))
                                observable.refreshList(nc)
                            }
                        }
                        else -> {
                            var currentChatPos: Int
                            getContactFromSenderOrReceiverID(chat.sender.id) { contact1 ->
                                val currentChat = nc.firstOrNull { it.id == chat.id }
                                    ?: return@getContactFromSenderOrReceiverID nc.add(
                                        chat.copy(
                                            sender = contact1
                                        )
                                    )
                                val c = currentChat.copy(sender = contact1)
                                currentChatPos = nc.indexOf(currentChat)
                                // Chat already added
                                if (currentChatPos > 0) {
                                    nc[currentChatPos] = c
                                } else nc.add(c)
                                observable.refreshList(nc)
                            }
                            getContactFromSenderOrReceiverID(chat.receiver.id) { contact1 ->
                                val currentChat = nc.firstOrNull { it.id == chat.id }
                                    ?: return@getContactFromSenderOrReceiverID nc.add(
                                        chat.copy(
                                            receiver = contact1
                                        )
                                    )
                                val c = currentChat.copy(receiver = contact1)
                                currentChatPos = nc.indexOf(currentChat)
                                if (currentChatPos > 0) {
                                    nc[currentChatPos] = c
                                } else nc.add(c)
                                observable.refreshList(nc)
                            }
                        }
                    }
                }
            }
    }

    private fun getContactFromSenderOrReceiverID(id: String, withContact: ((Contact) -> Any?)?) {
        usersCollection.whereEqualTo(Contact.CREATOR.Fields.ID, id).limit(1).get()
            .addOnCompleteListener {
                if (it.isSuccessful && it.result != null) {
                    for (snapshot in it.result!!) {
                        if (snapshot.exists()) {
                            withContact?.invoke(snapshot.toObject(Contact::class.java).local)
                        }
                    }
                }
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

    private suspend fun MutableLiveData<List<Chat>>.deleteChatIfPresent(message: Chat) {
        withContext(Dispatchers.Default) {
            value?.filter { it == message }?.let { refreshList(it) }
        }
    }

    private fun MutableLiveData<List<Chat>>.refreshList(list: List<Chat>) = postValue(list)

    private fun List<Chat>.getConversations(): List<Chat> {
        val conversations = arrayListOf<Chat>()
        // Group chats by recipients(contact) id then scan through each of them to get the latest
        for (group in this@getConversations.groupBy { it.receiverId }) {
            // Add the latest message to the conversation list
            conversations.add(group.value.sortedByDescending { it.timeSent }[0])
        }
        return conversations
    }

    private fun setException(ex: Exception?) {
        observableException.value = ex
    }

    companion object {
        private val LOG_TAG = ChatRepository::class.java.simpleName
    }
}