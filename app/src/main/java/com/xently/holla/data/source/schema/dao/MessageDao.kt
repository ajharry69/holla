package com.xently.holla.data.source.schema.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.xently.holla.data.model.Message

@Dao
interface MessageDao {

    /**
     * @param srId Sender/Receiver ID
     */
    @Query("SELECT * FROM Message WHERE senderId = :srId OR receiverId = :srId")
    fun getObservableMessages(srId: String): LiveData<List<Message>>

    @Query("SELECT * FROM Message WHERE id = :id")
    fun getObservableMessage(id: String): LiveData<Message>

    /**
     * @param srId Sender/Receiver ID
     */
    @Query("SELECT * FROM Message WHERE senderId = :srId OR receiverId = :srId")
    fun getMessages(srId: String): List<Message>

    @Query("SELECT * FROM Message WHERE id = :id")
    suspend fun getMessage(id: String): Message

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveMessage(chat: Message): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveMessages(chats: List<Message>): Int

    @Delete
    suspend fun deleteMessage(chat: Message): Int

    @Query("DELETE from Message")
    suspend fun deleteMessages()
}