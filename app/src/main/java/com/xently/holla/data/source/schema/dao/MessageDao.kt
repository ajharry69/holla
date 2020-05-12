package com.xently.holla.data.source.schema.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.xently.holla.data.model.Message

@Dao
interface MessageDao {

    /**
     * @param srId Sender/Receiver ID
     */
    @Query("SELECT * FROM Message WHERE senderId = :srId OR receiverId = :srId ORDER BY timeSent DESC")
    fun getObservableMessages(srId: String): LiveData<List<Message>>

    @Query("SELECT * FROM Message WHERE id = :id")
    fun getObservableMessage(id: String): LiveData<Message>

    /**
     * @param srId Sender/Receiver ID
     */
    @Query("SELECT * FROM Message WHERE senderId = :srId OR receiverId = :srId ORDER BY timeSent DESC")
    fun getMessages(srId: String): List<Message>

    @Query("SELECT * FROM Message WHERE id = :id")
    suspend fun getMessage(id: String): Message

    @Insert(onConflict = OnConflictStrategy.REPLACE, entity = Message::class)
    suspend fun saveMessage(message: Message)

    @Insert(onConflict = OnConflictStrategy.REPLACE, entity = Message::class)
    suspend fun saveMessages(messages: List<Message>)

    @Delete(entity = Message::class)
    suspend fun deleteMessage(message: Message): Int

    @Query("DELETE FROM message WHERE id = :id")
    suspend fun deleteMessage(id: String): Int

    @Query("DELETE from Message")
    suspend fun deleteMessages()

    @Query("DELETE from Message WHERE senderId = :contactId OR receiverId = :contactId")
    suspend fun deleteMessages(contactId: String)
}