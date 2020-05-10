package com.xently.holla.data.source.local

import android.content.Context
import com.google.android.gms.tasks.Task
import com.xently.holla.data.Result
import com.xently.holla.data.model.Conversation
import com.xently.holla.data.source.schema.IConversationDataSource
import com.xently.holla.data.source.schema.dao.ConversationDao

class ConversationLocalDataSource internal constructor(
    private val dao: ConversationDao,
    context: Context
) : BaseLocalDataSource(context), IConversationDataSource {
    override suspend fun getObservableConversations() = dao.getObservableConversations()

    override suspend fun saveConversation(conversation: Conversation): Result<Unit> {
        dao.saveConversation(conversation)
        return Result.Success(Unit)
    }

    override suspend fun saveConversations(conversations: List<Conversation>): Result<Unit> {
        dao.saveConversations(conversations)
        return Result.Success(Unit)
    }

    override suspend fun deleteConversation(conversation: Conversation): Task<Void>? {
        dao.deleteConversation(conversation)
        return null
    }

    override suspend fun getConversations() = dao.getConversations()
}