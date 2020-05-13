package com.xently.holla.data.source.schema

import androidx.lifecycle.LiveData
import com.xently.holla.data.Result
import com.xently.holla.data.Source
import com.xently.holla.data.model.Conversation

interface IConversationDataSource : IBaseDataSource {
    suspend fun getObservableConversations(): LiveData<List<Conversation>>
    suspend fun saveConversation(
        conversation: Conversation,
        destination: Source? = null
    ): Result<Conversation>

    suspend fun saveConversations(
        conversations: List<Conversation>,
        destination: Source? = null
    ): Result<List<Conversation>>

    /**
     * @param source specifies from where [id] is to be deleted. `null` means delete from
     * all(remote & cache) sources
     */
    suspend fun deleteConversation(id: String, source: Source? = null): Result<Unit>

    /**
     * @param source specifies from where [conversation] is to be deleted. `null` means delete from
     * all(remote & cache) sources
     */
    suspend fun deleteConversation(conversation: Conversation, source: Source? = null): Result<Unit>
    suspend fun getConversation(mateId: String): Conversation?
    suspend fun getConversations(): List<Conversation>
}