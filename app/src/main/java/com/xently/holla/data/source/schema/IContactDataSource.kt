package com.xently.holla.data.source.schema

import androidx.lifecycle.LiveData
import com.xently.holla.data.model.Contact

/**
 * Contains helper methods to help find list of contacts which are already registered with the app
 * (already members of Holla) and saved as part of currently signed in user's phone contacts(saved
 * in phone)
 */
interface IContactDataSource : IBaseDataSource {
    suspend fun getContactList(): List<Contact>

    suspend fun getContact(id: String): Contact?

    suspend fun getObservableContactList(): LiveData<List<Contact>>

    suspend fun getObservableContact(id: String): LiveData<Contact>

    suspend fun saveContact(contact: Contact)

    suspend fun saveContacts(contacts: List<Contact>)
}