package com.xently.holla.data.repository

import com.xently.holla.data.Result
import com.xently.holla.data.Source
import com.xently.holla.data.Source.LOCAL
import com.xently.holla.data.Source.REMOTE
import com.xently.holla.data.data
import com.xently.holla.data.listData
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

    override suspend fun saveConversation(
        conversation: Conversation,
        destination: Source?
    ): Result<Conversation> = when (destination) {
        REMOTE -> remoteDataSource.saveConversation(conversation, destination)
        LOCAL -> localDataSource.saveConversation(conversation.run {
            copy(mate = mate?.let { getLocalContact(it) })
        }, destination)
        null -> remoteDataSource.saveConversation(conversation, destination).data.run {
            this?.let { saveConversation(it, LOCAL) }
                ?: Result.Error(Exception("Error saving conversations"))
        }
    }

    override suspend fun saveConversations(
        conversations: List<Conversation>,
        destination: Source?
    ): Result<List<Conversation>> = when (destination) {
        REMOTE -> remoteDataSource.saveConversations(conversations, destination)
        LOCAL -> localDataSource.saveConversations(conversations.map { conv ->
            conv.copy(mate = conv.mate?.let { getLocalContact(it) })
        }, destination)
        null -> remoteDataSource.saveConversations(conversations, destination).listData.run {
            saveConversations(this, LOCAL)
        }
    }

    override suspend fun deleteConversation(id: String, source: Source?): Result<Unit> {
        return when (source) {
            REMOTE -> remoteDataSource.deleteConversation(id, source)
            LOCAL -> localDataSource.deleteConversation(id, source)
            null -> remoteDataSource.deleteConversation(id, source).run {
                deleteConversation(id, LOCAL)
            }
        }
    }

    override suspend fun deleteConversation(conversation: Conversation, source: Source?) =
        when (source) {
            REMOTE -> remoteDataSource.deleteConversation(conversation, source)
            LOCAL -> localDataSource.deleteConversation(conversation, source)
            null -> remoteDataSource.deleteConversation(conversation, source)?.run {
                localDataSource.deleteConversation(conversation, source)
            }
        }

    override suspend fun getConversation(mateId: String) =
        remoteDataSource.getConversation(mateId)?.apply {
            saveConversation(this, LOCAL)
        }

    override suspend fun getConversations() = remoteDataSource.getConversations().apply {
        saveConversations(this, LOCAL)
    }
}