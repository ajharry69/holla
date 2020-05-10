package com.xently.holla.data.repository

import android.app.Activity
import com.xently.holla.data.model.Contact
import com.xently.holla.data.repository.schema.IContactRepository
import com.xently.holla.data.source.schema.IContactDataSource

class ContactRepository internal constructor(
    private val localDataSource: IContactDataSource,
    private val remoteDataSource: IContactDataSource
) : IContactRepository {
    override fun getObservableException() = localDataSource.getObservableException()

    override fun getLocalContact(contact: Contact) = localDataSource.getLocalContact(contact)

    override suspend fun getContactList(activity: Activity): List<Contact> {
        val result = remoteDataSource.getContactList(activity)
        localDataSource.saveContacts(result) // Cache contacts
        return result
    }

    override suspend fun getContact(id: String) = localDataSource.getContact(id)

    override suspend fun getObservableContactList() = localDataSource.getObservableContactList()

    override suspend fun getObservableContact(id: String) = localDataSource.getObservableContact(id)

    override suspend fun saveContact(contact: Contact) {
        localDataSource.saveContact(contact)
    }

    override suspend fun saveContacts(contacts: List<Contact>) {
        localDataSource.saveContacts(contacts)
    }
}