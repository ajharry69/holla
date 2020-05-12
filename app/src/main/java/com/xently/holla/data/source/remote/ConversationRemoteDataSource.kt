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
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class ConversationRemoteDataSource internal constructor(context: Context) :
    BaseRemoteDataSource(context), IConversationDataSource {
    private val conversationsCollection: CollectionReference
        get() = getMyConversationsCollection()

    private val observableConversationList = MutableLiveData<List<Conversation>>(null)

    override suspend fun getObservableConversations() = observableConversationList

    // Implementation is unnecessary. It's taken care of by firebase-functions
    override suspend fun saveConversation(
        conversation: Conversation,
        destination: Source?
    ): Result<Conversation> {
        val conversationId = conversationsCollection.document().id
        val msg = conversation.copy(
            id = conversationId,
            senderId = firebaseAuth.currentUser?.uid.toString()
        )
        val result = conversationsCollection.document(conversationId).set(msg).execute()
        return if (result is Result.Success) {
            getConversation(conversation.mateId)?.let {
                Result.Success(it)
            } ?: Result.Error(Exception("Error retrieving conversation"))
        } else result as Result.Error
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

    override suspend fun deleteConversation(id: String, source: Source?): Result<Unit> {
        val result = conversationsCollection.document(id).delete().execute()
        return if (result is Result.Success) {
            Result.Success(Unit)
        } else result as Result.Error
    }

    // Implementation is unnecessary. It's taken care of by firebase-functions
    override suspend fun deleteConversation(
        conversation: Conversation,
        source: Source?
    ) = conversationsCollection.document(conversation.id).delete().addOnCompleteListener {
        if (it.isSuccessful) runBlocking {
            launch(Dispatchers.IO) {
                observableConversationList.deleteConversationIfPresent(conversation)
            }
        }
    }

    override suspend fun getConversation(mateId: String) =
        conversationsCollection.document(mateId).get().await().getObject<Conversation>()

    override suspend fun getConversations() =
        conversationsCollection.orderBy(Fields.TIME_SENT, Direction.DESCENDING)
            .get().await().getObjects<Conversation>().apply {
                observableConversationList.refreshList(this)
            }

    private suspend fun MutableLiveData<List<Conversation>>.deleteConversationIfPresent(conversation: Conversation) {
        withContext(Dispatchers.Default) {
            value?.filter { it == conversation }?.let { refreshList(it) }
        }
    }

    private fun MutableLiveData<List<Conversation>>.refreshList(list: List<Conversation>?) {
        postValue(list)
    }
}