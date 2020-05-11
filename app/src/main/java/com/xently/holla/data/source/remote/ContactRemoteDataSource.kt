package com.xently.holla.data.source.remote

import android.Manifest
import android.content.Context
import android.provider.ContactsContract
import androidx.annotation.RequiresPermission
import androidx.core.database.getStringOrNull
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import com.xently.holla.data.model.Contact
import com.xently.holla.data.model.Contact.CREATOR.Fields
import com.xently.holla.data.source.schema.IContactDataSource
import kotlinx.coroutines.tasks.await

class ContactRemoteDataSource internal constructor(private val context: Context) :
    BaseRemoteDataSource(context), IContactDataSource {

    private val observableContactList = MutableLiveData<List<Contact>>(null)

    @RequiresPermission(Manifest.permission.READ_CONTACTS)
    override suspend fun getContactList(): List<Contact> {
        val contactList = arrayListOf<Contact>()

        context.contentResolver.query(
            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
            null,
            null,
            null,
            null
        )?.use {
            while (it.moveToNext()) {
                val name: String =
                    it.getString(it.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME))
                val mobileNumber =
                    it.getStringOrNull(it.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NORMALIZED_NUMBER))
                        ?.replace(Regex("\\s|-|\\(|\\)"), "") ?: continue

                // Skip current user's phone number
                if (mobileNumber == firebaseAuth.currentUser?.phoneNumber) continue
                contactList.addIfRegistered(Contact("", name, mobileNumber))
            }
        }
        return contactList
    }

    override suspend fun getContact(id: String): Contact? = null

    override suspend fun getObservableContactList() = observableContactList

    override suspend fun getObservableContact(id: String): LiveData<Contact> {
        return Transformations.map(observableContactList) { contacts ->
            contacts.firstOrNull { it.id == id }
        }
    }

    override suspend fun saveContact(contact: Contact) = Unit

    override suspend fun saveContacts(contacts: List<Contact>) {
        setContactList(contacts)
    }

    private suspend fun ArrayList<Contact>.addIfRegistered(contact: Contact) {
        // Get only one [contact] with mobile number = contact.mobileNumber
        val snapshots = usersCollection.whereEqualTo(Fields.MOBILE, contact.mobileNumber)
            .limit(1).get().await()

        for (snap in snapshots) {
            if (snap.exists()) {
                add(snap.toObject(Contact::class.java).copy(name = contact.name)) // Use save name
            }
            setContactList(this@addIfRegistered)
        }
    }

    private fun setContactList(list: Iterable<Contact>) {
        observableContactList.value = list.toList()
    }
}