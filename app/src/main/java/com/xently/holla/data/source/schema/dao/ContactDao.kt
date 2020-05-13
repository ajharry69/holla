package com.xently.holla.data.source.schema.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.xently.holla.data.model.Contact

@Dao
interface ContactDao {

    @Query("SELECT * FROM contact ORDER BY name, mobileNumber")
    fun getObservableContacts(): LiveData<List<Contact>>

    @Query("SELECT * FROM contact WHERE id = :id")
    fun getObservableContact(id: String): LiveData<Contact>

    @Query("SELECT * FROM contact ORDER BY name, mobileNumber")
    fun getContacts(): List<Contact>

    @Query("SELECT * FROM contact WHERE id = :id")
    suspend fun getContact(id: String): Contact

    @Insert(onConflict = OnConflictStrategy.REPLACE, entity = Contact::class)
    suspend fun saveContact(contact: Contact)

    @Insert(onConflict = OnConflictStrategy.REPLACE, entity = Contact::class)
    suspend fun saveContacts(contacts: List<Contact>)

    @Delete(entity = Contact::class)
    suspend fun deleteContact(contact: Contact): Int

    @Query("DELETE from contact")
    suspend fun deleteContacts()
}