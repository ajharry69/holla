package com.xently.holla.data.source.remote

import android.content.Context
import androidx.lifecycle.MutableLiveData
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.Query.Direction
import com.xently.holla.data.*
import com.xently.holla.data.model.ChatCreator.Fields
import com.xently.holla.data.model.Conversation
import com.xently.holla.data.source.schema.IConversationDataSource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class ConversationRemoteDataSource internal constructor(context: Context) :
    BaseRemoteDataSource(context), IConversationDataSource {
    private val conversationsCollection: CollectionReference
        get() = getMyConversationsCollection()

    private val observableConversationList = MutableLiveData<List<Conversation>>(null)

    override suspend fun getObservableConversations() = observableConversationList

    // Implementation is unnecessary. It's taken care of by firebase-functions
    override suspend fun saveConversation(conversation: Conversation, destination: Source?) =
        withContext(Dispatchers.IO) {
            try {
                val conversationId = conversationsCollection.document().id
                val msg = conversation.copy(
                    id = conversationId,
                    senderId = firebaseAuth.currentUser?.uid.toString()
                )
                val result = conversationsCollection.document(conversationId).set(msg).execute()
                if (result is Result.Success) {
                    getConversation(conversation.mateId)?.let {
                        Result.Success(it)
                    } ?: throw Exception("Error retrieving conversation")
                } else result as Result.Error
            } catch (ex: Exception) {
                setException(ex)
                Result.Error(ex)
            }
        }

    // Implementation is unnecessary. It's taken care of by firebase-functions
    override suspend fun saveConversations(
        conversations: List<Conversation>,
        destination: Source?
    ) = withContext(Dispatchers.IO) {
        val conv = arrayListOf<Conversation>()
        conversations.forEach {
            launch {
                saveConversation(it).data?.also {
                    conv += it
                }
            }
        }
        Result.Success(conv)
    }

    override suspend fun deleteConversation(id: String, source: Source?) =
        withContext(Dispatchers.IO) {
            try {
                conversationsCollection.document(id).delete().execute().run {
                    if (isSuccessful) {
                        deleteIfPresent(id)
                        Result.Success(Unit)
                    } else this as Result.Error
                }
            } catch (ex: Exception) {
                setException(ex)
                Result.Error(ex)
            }
        }

    override suspend fun deleteConversation(conversation: Conversation, source: Source?) =
        deleteConversation(conversation.mateId, source)

    override suspend fun getConversation(mateId: String) = withContext(Dispatchers.IO) {
        try {
            conversationsCollection.document(mateId).get().await().getObject<Conversation>()
        } catch (ex: Exception) {
            setException(ex)
            null
        }
    }

    override suspend fun getConversations() = try {
        conversationsCollection.orderBy(Fields.TIME_SENT, Direction.DESCENDING).get().await()
            .getObjects<Conversation>().apply { refresh() }
    } catch (ex: Exception) {
        setException(ex)
        emptyList<Conversation>().apply { refresh() }
    }

    private suspend fun deleteIfPresent(conversationId: String) {
        withContext(Dispatchers.Default) {
            observableConversationList.value?.filter { it.mateId == conversationId }
                ?.refresh()
        }
    }

    private fun List<Conversation>?.refresh() {
        observableConversationList.postValue(this)
    }
}