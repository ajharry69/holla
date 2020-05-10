package com.xently.holla.data.source.schema.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.xently.holla.data.model.Conversation

@Dao
interface ConversationDao {

    @Query("SELECT * FROM Conversation")
    fun getObservableConversations(): LiveData<List<Conversation>>

    @Query("SELECT * FROM Conversation WHERE id = :id")
    fun getObservableConversation(id: String): LiveData<Conversation>

    @Query("SELECT * FROM Conversation")
    fun getConversations(): List<Conversation>

    @Query("SELECT * FROM Conversation WHERE id = :id")
    suspend fun getConversation(id: String): Conversation

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveConversation(conversation: Conversation): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveConversations(conversations: List<Conversation>): Int

    @Delete
    suspend fun deleteConversation(conversation: Conversation): Int

    @Query("DELETE from Conversation")
    suspend fun deleteConversations()
}