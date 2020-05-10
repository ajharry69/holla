package com.xently.holla.data.source.remote

import android.Manifest
import android.app.Activity
import android.content.Context
import android.provider.ContactsContract
import androidx.annotation.RequiresPermission
import androidx.core.database.getStringOrNull
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import com.xently.holla.data.model.Contact
import com.xently.holla.data.source.schema.IContactDataSource

class ContactRemoteDataSource internal constructor(private val context: Context) :
    BaseRemoteDataSource(context), IContactDataSource {

    private val observableContactList = MutableLiveData<List<Contact>>(null)

    @RequiresPermission(Manifest.permission.READ_CONTACTS)
    override suspend fun getContactList(activity: Activity): List<Contact> {
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
                val contact = Contact("", name, mobileNumber)
                contactList.addIfRegistered(activity, contact)
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

    private fun ArrayList<Contact>.addIfRegistered(activity: Activity, contact: Contact) {
        // Get only one [contact] with mobile number = contact.mobileNumber
        usersCollection.whereEqualTo(Contact.CREATOR.Fields.MOBILE, contact.mobileNumber).limit(1)
            .get().addOnCompleteListener(activity) {
                if (it.isSuccessful && it.result != null) {
                    for (snapshot in it.result!!) if (snapshot.exists()) this@addIfRegistered.add(
                        snapshot.toObject(Contact::class.java)
                            .copy(name = contact.name) // Use save name
                    )

                    setContactList(this@addIfRegistered)
                }
            }
    }

    private fun setContactList(list: Iterable<Contact>) {
        observableContactList.value = list.toList()
    }
}