package com.xently.holla.data.source.remote

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import com.google.firebase.firestore.Query.Direction
import com.xently.holla.data.Result
import com.xently.holla.data.Source
import com.xently.holla.data.data
import com.xently.holla.data.getObjects
import com.xently.holla.data.model.ChatCreator.Fields
import com.xently.holla.data.model.Contact
import com.xently.holla.data.model.Message
import com.xently.holla.data.source.schema.IMessageDataSource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class MessageRemoteDataSource internal constructor(context: Context) :
    BaseRemoteDataSource(context), IMessageDataSource {

    private val observableMessageList = MutableLiveData<List<Message>>(null)

    override suspend fun getObservableMessages(contact: Contact) = getObservableMessages(contact.id)

    override suspend fun getObservableMessages(contactId: String): LiveData<List<Message>> {
        return Transformations.map(observableMessageList) { chatList ->
            if (chatList == null) return@map null
            chatList.sortedByDescending { it.timeSent }
        }
    }

    override suspend fun sendMessage(message: Message): Result<Message> {
        val collection = getMyMessagesCollection(message.receiverId)
        val messageId = collection.document().id
        val msg = message.copy(id = messageId, senderId = firebaseAuth.currentUser?.uid.toString())
        val result = collection.document(messageId).set(msg).execute()
        return if (result is Result.Success) {
            Result.Success(msg)
        } else result as Result.Error
    }

    override suspend fun sendMessages(messages: List<Message>) = withContext(Dispatchers.IO) {
        val msgs = arrayListOf<Message>()
        messages.forEach {
            launch {
                sendMessage(it).data?.let {
                    msgs += it
                }
            }
        }
        Result.Success(msgs)
    }

    override suspend fun deleteMessage(message: Message, source: Source?): Result<Unit> {
        return withContext(Dispatchers.IO) {
            val result =
                getMyMessagesCollection(message.receiverId).document(message.id).delete().execute()
            if (result is Result.Success) {
                launch { observableMessageList.deleteChatIfPresent(message) }
                Result.Success(Unit)
            } else result as Result.Error
        }
    }

    override suspend fun deleteMessage(id: String, source: Source?): Result<Unit> {
        // Unimplemented
        return Result.Success(Unit)
    }

    override suspend fun deleteMessages(contactId: String, source: Source?): Result<Unit> {
        // Unimplemented
        return Result.Success(Unit)
    }

    override suspend fun getMessages(contact: Contact) = getMessages(contact.id)

    override suspend fun getMessages(contactId: String): List<Message> {
        return try {
            getMyMessagesCollection(contactId).orderBy(Fields.TIME_SENT, Direction.DESCENDING).get()
                .await().getObjects<Message>().apply {
                    observableMessageList.refreshList(this)
                }
        } catch (ex: Exception) {
            setException(ex)
            observableMessageList.refreshList(emptyList())
            emptyList()
        }
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