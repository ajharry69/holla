package com.xently.holla.data.source.schema.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.xently.holla.data.model.Conversation

@Dao
interface ConversationDao {

    @Query("SELECT * FROM Conversation")
    fun getObservableConversations(): LiveData<List<Conversation>>

    @Query("SELECT * FROM Conversation WHERE mateId = :mateId")
    fun getObservableConversation(mateId: String): LiveData<Conversation>

    @Query("SELECT * FROM Conversation")
    fun getConversations(): List<Conversation>

    @Query("SELECT * FROM Conversation WHERE mateId = :mateId")
    suspend fun getConversation(mateId: String): Conversation

    @Insert(onConflict = OnConflictStrategy.REPLACE, entity = Conversation::class)
    suspend fun saveConversation(conversation: Conversation)

    @Insert(onConflict = OnConflictStrategy.REPLACE, entity = Conversation::class)
    suspend fun saveConversations(conversations: List<Conversation>)

    @Delete(entity = Conversation::class)
    suspend fun deleteConversation(conversation: Conversation): Int

    @Query("DELETE FROM Conversation")
    suspend fun deleteConversations()

    @Query("DELETE FROM Conversation WHERE mateId = :id")
    suspend fun deleteConversation(id: String)
}