package com.xently.holla.data.repository

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

class ChatRepository internal constructor() : BaseRepository(), IChatRepository {

    private val observableException = MutableLiveData<Exception>()
    private val observableMessageList = MutableLiveData<List<Chat>>(emptyList())
    private val observableConversationList = MutableLiveData<List<Chat>>(emptyList())

    override fun getObservableException(): LiveData<Exception> = observableException

    override suspend fun getObservableConversations(contact: Contact?): LiveData<List<Chat>> {
        return if (contact == null) observableConversationList else {
            Transformations.map(observableMessageList) { chatList ->
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

    override suspend fun getConversations(contact: Contact?): ListenerRegistration {
        val query = messagesCollection.whereEqualTo(Fields.SENDER, firebaseAuth.currentUser?.uid)
        return if (contact == null) query else {
            query.whereEqualTo(Fields.RECEIVER, contact.id)
        }.orderBy(Fields.TIME_SENT, Query.Direction.DESCENDING)
            .addSnapshotListener { querySnapshot, exception ->
                if (exception != null) {
                    setException(exception)
                    return@addSnapshotListener
                }

                if (querySnapshot == null || querySnapshot.isEmpty) return@addSnapshotListener

                val chatList = querySnapshot.toObjects(Chat::class.java)

                val (chats, observable) = if (contact == null) {
                    Pair(chatList.getConversations(), observableConversationList)
                } else Pair(chatList, observableMessageList)

                observable.refreshList(chats)
            }
    }

    override suspend fun getContactFromChat(chat: Chat): Contact {
        // TODO: Fetch from firebase directly
        return Contact(id = chat.receiverId)
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