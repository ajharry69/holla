package com.xently.holla.data.source.schema.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.xently.holla.data.model.Contact

@Dao
interface ContactDao {

    @Query("SELECT * FROM contact")
    fun getObservableContacts(): LiveData<List<Contact>>

    @Query("SELECT * FROM contact WHERE id = :id")
    fun getObservableContact(id: String): LiveData<Contact>

    @Query("SELECT * FROM contact")
    fun getContacts(): List<Contact>

    @Query("SELECT * FROM contact WHERE id = :id")
    suspend fun getContact(id: String): Contact

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveContact(contact: Contact): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveContacts(contacts: List<Contact>): Int

    @Delete
    suspend fun deleteContact(contact: Contact): Int

    @Query("DELETE from contact")
    suspend fun deleteContacts()
}