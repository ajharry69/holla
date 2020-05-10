package com.xently.holla.data.source.schema

import androidx.lifecycle.LiveData
import com.google.android.gms.tasks.Task
import com.xently.holla.data.Result
import com.xently.holla.data.model.Conversation

interface IConversationDataSource : IBaseDataSource {
    suspend fun getObservableConversations(): LiveData<List<Conversation>>
    suspend fun saveConversation(conversation: Conversation): Result<Unit>
    suspend fun saveConversations(conversations: List<Conversation>): Result<Unit>
    suspend fun deleteConversation(conversation: Conversation): Task<Void>?
    suspend fun getConversations(): List<Conversation>
}