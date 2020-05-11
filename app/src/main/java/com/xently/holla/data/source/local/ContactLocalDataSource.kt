package com.xently.holla.data.source.local

import android.content.Context
import com.xently.holla.data.model.Contact
import com.xently.holla.data.source.schema.IContactDataSource
import com.xently.holla.data.source.schema.dao.ContactDao

class ContactLocalDataSource internal constructor(private val dao: ContactDao, context: Context) :
    BaseLocalDataSource(context), IContactDataSource {
    override suspend fun getContactList() = dao.getContacts()

    override suspend fun getContact(id: String) = dao.getContact(id)

    override suspend fun getObservableContactList() = dao.getObservableContacts()

    override suspend fun getObservableContact(id: String) = dao.getObservableContact(id)

    override suspend fun saveContact(contact: Contact) = dao.saveContact(contact)

    override suspend fun saveContacts(contacts: List<Contact>) = dao.saveContacts(contacts)
}