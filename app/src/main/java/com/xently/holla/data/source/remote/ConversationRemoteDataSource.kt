package com.xently.holla.data.source.remote

import android.content.Context
import androidx.lifecycle.MutableLiveData
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.CollectionReference
import com.xently.holla.data.Result
import com.xently.holla.data.Source
import com.xently.holla.data.getObject
import com.xently.holla.data.getObjects
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
    override suspend fun saveConversation(conversation: Conversation): Result<Unit> {
        val conversationId = conversationsCollection.document().id
        val msg = conversation.copy(
            id = conversationId,
            senderId = firebaseAuth.currentUser?.uid.toString()
        )
        val result = conversationsCollection.document(conversationId).set(msg).execute()
        return if (result is Result.Success) {
            Result.Success(Unit)
        } else result as Result.Error
    }

    // Implementation is unnecessary. It's taken care of by firebase-functions
    override suspend fun saveConversations(conversations: List<Conversation>): Result<Unit> {
        withContext(Dispatchers.IO) {
            conversations.forEach {
                launch {
                    saveConversation(it)
                }
            }
        }
        return Result.Success(Unit)
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
    ): Task<Void>? {
        return conversationsCollection.document(conversation.id).delete().addOnCompleteListener {
            if (it.isSuccessful) runBlocking {
                launch(Dispatchers.IO) {
                    observableConversationList.deleteConversationIfPresent(conversation)
                }
            }
        }
    }

    override suspend fun getConversation(mateId: String): Conversation? {
        return conversationsCollection.document(mateId).get().await().getObject()
    }

    override suspend fun getConversations(): List<Conversation> {
        val conversations = conversationsCollection.get().await().getObjects<Conversation>()
        observableConversationList.refreshList(conversations)
        return conversations
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