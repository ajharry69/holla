package com.xently.holla.data.repository

import android.Manifest
import android.app.Activity
import android.content.Context
import android.provider.ContactsContract.CommonDataKinds.Phone
import androidx.annotation.RequiresPermission
import androidx.core.database.getStringOrNull
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.xently.holla.data.model.Contact
import com.xently.holla.data.model.Contact.CREATOR.Fields.MOBILE
import com.xently.holla.data.repository.schema.IContactRepository

class ContactRepository internal constructor(private val context: Context) : BaseRepository(),
    IContactRepository {

    private val observableContactList: MutableLiveData<List<Contact>> = MutableLiveData(emptyList())

    private fun setContactList(list: Iterable<Contact>) {
        observableContactList.value = list.toList()
    }

    private fun setContactList(vararg contacts: Contact) {
        setContactList(contacts.asIterable())
    }

    @RequiresPermission(Manifest.permission.READ_CONTACTS)
    override suspend fun getContactList(activity: Activity): List<Contact> {
        val contactList = arrayListOf<Contact>()

        context.contentResolver.query(
            Phone.CONTENT_URI,
            null,
            null,
            null,
            null
        )?.use {
            while (it.moveToNext()) {
                val name: String = it.getString(it.getColumnIndex(Phone.DISPLAY_NAME))
                val mobileNumber = it.getStringOrNull(it.getColumnIndex(Phone.NORMALIZED_NUMBER))
                    ?.replace(Regex("\\s|-|\\(|\\)"), "") ?: continue

                // Skip current user's phone number
                if (mobileNumber == firebaseAuth.currentUser?.phoneNumber) continue
                val contact = Contact("", name, mobileNumber)
                contactList.addIfRegistered(activity, contact)
            }
        }
        return contactList
    }

    override suspend fun getObservableContactList(): LiveData<List<Contact>> = observableContactList

    private fun ArrayList<Contact>.addIfRegistered(activity: Activity, contact: Contact) {
        // Get only one [contact] with mobile number = contact.mobileNumber
        usersCollection.whereEqualTo(MOBILE, contact.mobileNumber).limit(1).get()
            .addOnCompleteListener(activity) {
                if (it.isSuccessful && it.result != null) {
                    for (snapshot in it.result!!) if (snapshot.exists()) this@addIfRegistered.add(
                        snapshot.toObject(Contact::class.java)
                            .copy(name = contact.name) // Use save name
                    )

                    setContactList(this@addIfRegistered)
                }
            }
    }
}