package com.xently.holla.data.source.remote

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import com.google.firebase.firestore.Query.Direction
import com.xently.holla.data.*
import com.xently.holla.data.model.ChatCreator.Fields
import com.xently.holla.data.model.Contact
import com.xently.holla.data.model.Message
import com.xently.holla.data.model.Type
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

    override suspend fun sendMessage(
        message: Message,
        destination: Source?
    ): Result<Message?> =
        withContext(Dispatchers.IO) {
            try {
                val collection = getMyMessagesCollection(message.receiverId)
                val messageId = collection.document().id
                val senderId = firebaseAuth.currentUser?.uid.toString()
                val mediaFile = message.mediaFile
                var mediaUrl: String? = null
                if (message.type != Type.Text && mediaFile != null && mediaFile.uri != null) {
                    // Media file. Upload it first to get the download url that's to be uploaded to db
                    val path = "media/messages/${senderId}/${messageId}"
                    mediaUrl = firebaseStorage.reference.child(path).apply {
                        putFile(mediaFile.uri).await()
                    }.downloadUrl.await().toString()
                }
                val msg = message.copy(
                    id = messageId,
                    senderId = senderId,
                    mediaUrl = mediaUrl,
                    mediaFile = null
                )
                val result = collection.document(messageId).set(msg).execute()
                if (result is Result.Success) {
                    Result.Success(msg)
                } else result as Result.Error
            } catch (ex: Exception) {
                setException(ex)
                Result.Error(ex)
            }
        }

    override suspend fun sendMessages(
        messages: List<Message>,
        destination: Source?
    ) = withContext(Dispatchers.IO) {
        val msgs = arrayListOf<Message>()
        messages.forEach {
            launch(Dispatchers.IO) {
                sendMessage(it, destination).data?.let {
                    msgs += it
                }
            }
        }
        Result.Success(msgs)
    }

    override suspend fun deleteMessage(message: Message, source: Source?) =
        withContext(Dispatchers.IO) {
            try {
                getMyMessagesCollection(message.receiverId).document(message.id).delete()
                    .execute().run {
                        if (isSuccessful) {
                            deleteIfPresent(message.id)
                            Result.Success(Unit)
                        } else this as Result.Error
                    }
            } catch (ex: Exception) {
                setException(ex)
                Result.Error(ex)
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

    override suspend fun getMessages(contactId: String) = withContext(Dispatchers.IO) {
        try {
            getMyMessagesCollection(contactId).orderBy(Fields.TIME_SENT, Direction.DESCENDING).get()
                .await().getObjects<Message>().apply { refresh() }
        } catch (ex: Exception) {
            setException(ex)
            emptyList<Message>().apply { refresh() }
        }
    }

    override suspend fun getMessage(senderId: String, id: String) = withContext(Dispatchers.IO) {
        try {
            getMyMessagesCollection(senderId).document(id).get().executeTask()
                ?.getObject<Message>()
        } catch (ex: Exception) {
            setException(ex)
            null
        }
    }

    private suspend fun deleteIfPresent(messageId: String) {
        withContext(Dispatchers.Default) {
            observableMessageList.value?.filter { it.id == messageId }?.refresh()
        }
    }

    private fun List<Message>?.refresh() {
        observableMessageList.postValue(this)
    }
}