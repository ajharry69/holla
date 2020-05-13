package com.xently.holla.data.source.local

import android.content.Context
import com.xently.holla.data.Result
import com.xently.holla.data.Source
import com.xently.holla.data.model.Conversation
import com.xently.holla.data.source.schema.IConversationDataSource
import com.xently.holla.data.source.schema.dao.ConversationDao

class ConversationLocalDataSource internal constructor(
    private val dao: ConversationDao,
    context: Context
) : BaseLocalDataSource(context), IConversationDataSource {
    override suspend fun getObservableConversations() = dao.getObservableConversations()

    override suspend fun saveConversation(
        conversation: Conversation,
        destination: Source?
    ): Result<Conversation> {
        dao.saveConversation(conversation)
        return Result.Success(dao.getConversation(conversation.mateId))
    }

    override suspend fun saveConversations(
        conversations: List<Conversation>,
        destination: Source?
    ): Result<List<Conversation>> {
        dao.saveConversations(conversations)
        return Result.Success(dao.getConversations())
    }

    override suspend fun deleteConversation(id: String, source: Source?): Result<Unit> {
        dao.deleteConversation(id)
        return Result.Success(Unit)
    }

    override suspend fun deleteConversation(
        conversation: Conversation,
        source: Source?
    ): Result<Unit> {
        dao.deleteConversation(conversation)
        return Result.Success(Unit)
    }

    override suspend fun getConversation(mateId: String) = dao.getConversation(mateId)

    override suspend fun getConversations() = dao.getConversations()
}