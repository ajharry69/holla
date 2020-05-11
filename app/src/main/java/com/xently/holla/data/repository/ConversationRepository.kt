package com.xently.holla.data.repository

import com.google.android.gms.tasks.Task
import com.xently.holla.data.Result
import com.xently.holla.data.Source
import com.xently.holla.data.Source.LOCAL
import com.xently.holla.data.Source.REMOTE
import com.xently.holla.data.model.Contact
import com.xently.holla.data.model.Conversation
import com.xently.holla.data.repository.schema.IConversationRepository
import com.xently.holla.data.source.schema.IConversationDataSource

class ConversationRepository internal constructor(
    private val localDataSource: IConversationDataSource,
    private val remoteDataSource: IConversationDataSource
) : IConversationRepository {
    override fun getObservableException() = localDataSource.getObservableException()

    override fun getLocalContact(contact: Contact) = localDataSource.getLocalContact(contact)

    override suspend fun getObservableConversations() = localDataSource.getObservableConversations()

    override suspend fun saveConversation(conversation: Conversation): Result<Unit> {
        val result = remoteDataSource.saveConversation(conversation)
        return if (result is Result.Success) {
            localDataSource.saveConversation(conversation)
            Result.Success(Unit)
        } else result as Result.Error
    }

    override suspend fun saveConversations(conversations: List<Conversation>): Result<Unit> {
        val result = remoteDataSource.saveConversations(conversations)
        return if (result is Result.Success) {
            localDataSource.saveConversations(conversations)
            Result.Success(Unit)
        } else result as Result.Error
    }

    override suspend fun deleteConversation(id: String, source: Source?): Result<Unit> {
        return when (source) {
            REMOTE -> remoteDataSource.deleteConversation(id, source)
            LOCAL -> localDataSource.deleteConversation(id, source)
            null -> remoteDataSource.deleteConversation(id, source).run {
                localDataSource.deleteConversation(id, source)
            }
        }
    }

    override suspend fun deleteConversation(
        conversation: Conversation,
        source: Source?
    ): Task<Void>? {
        return when (source) {
            REMOTE -> remoteDataSource.deleteConversation(conversation, source)
            LOCAL -> localDataSource.deleteConversation(conversation, source)
            null -> remoteDataSource.deleteConversation(conversation, source)?.run {
                localDataSource.deleteConversation(conversation, source)
            }
        }
    }

    override suspend fun getConversation(mateId: String): Conversation? {
        return remoteDataSource.getConversation(mateId)?.apply {
            localDataSource.saveConversation(this)
        }
    }

    override suspend fun getConversations(): List<Conversation> {
        return remoteDataSource.getConversations().apply {
            localDataSource.saveConversations(this)
        }
    }
}