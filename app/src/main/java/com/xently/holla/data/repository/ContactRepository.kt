package com.xently.holla.data.repository

import com.xently.holla.data.model.Contact
import com.xently.holla.data.repository.schema.IContactRepository
import com.xently.holla.data.source.schema.IContactDataSource

class ContactRepository internal constructor(
    private val localDataSource: IContactDataSource,
    private val remoteDataSource: IContactDataSource
) : IContactRepository {
    override fun getObservableException() = localDataSource.getObservableException()

    override fun getLocalContact(contact: Contact) = localDataSource.getLocalContact(contact)

    override suspend fun getContactList() = remoteDataSource.getContactList().apply {
        localDataSource.saveContacts(this) // Cache contacts
    }

    override suspend fun getContact(id: String) = localDataSource.getContact(id)

    override suspend fun getObservableContactList() = localDataSource.getObservableContactList()

    override suspend fun getObservableContact(id: String) = localDataSource.getObservableContact(id)

    override suspend fun saveContact(contact: Contact) = localDataSource.saveContact(contact)

    override suspend fun saveContacts(contacts: List<Contact>) =
        localDataSource.saveContacts(contacts)
}