package com.xently.holla.data.repository

import android.Manifest
import android.content.Context
import android.provider.ContactsContract.CommonDataKinds.Phone
import androidx.annotation.RequiresPermission
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.xently.holla.Log
import com.xently.holla.data.model.Contact
import com.xently.holla.data.repository.schema.IContactRepository
import com.xently.holla.utils.CountryISOInitialsToCode

class ContactRepository internal constructor(private val context: Context) : BaseRepository(),
    IContactRepository {

    private val observableContactList: MutableLiveData<List<Contact>> = MutableLiveData()

    private fun setContactList(list: Iterable<Contact>) {
        observableContactList.value = list.toList()
        Log.show("FCMService", "Set List Size: ${observableContactList.value}") // TODO: Delete
    }

    private fun setContactList(vararg contacts: Contact) {
        setContactList(contacts.asIterable())
    }

    @RequiresPermission(Manifest.permission.READ_CONTACTS)
    override fun getContactList(activity: FragmentActivity): List<Contact> {
        val contactList = arrayListOf<Contact>()
        val countryCode = CountryISOInitialsToCode.getCountryCode(context)

        context.contentResolver.query(
            Phone.CONTENT_URI,
            null,
            null,
            null,
            null
        )?.use {
            while (it.moveToNext()) {
                val name: String = it.getString(it.getColumnIndex(Phone.DISPLAY_NAME))
                var mobileNumber: String = it.getString(it.getColumnIndex(Phone.NUMBER))
                    .replace(Regex("\\s|-|\\(|\\)"), "")

                // Add country code if it's not already present
                if (mobileNumber[0].toString() != "+") mobileNumber =
                    countryCode + mobileNumber.removePrefix("0")
                // Skip current user's phone number

                if (mobileNumber == firebaseAuth.currentUser?.phoneNumber) continue
                val contact = Contact("", name, mobileNumber)
                contactList.addIfRegistered(contact, activity)
            }
        }

//        setContactList(contactList)

        return contactList
    }

    override fun getObservableContactList(): LiveData<List<Contact>> = observableContactList

    private fun ArrayList<Contact>.addIfRegistered(contact: Contact, activity: FragmentActivity) {
        // Get only one [contact] with mobile number = contact.mobileNumber
        usersCollection.whereEqualTo("mobileNumber", contact.mobileNumber).limit(1).get()
            .addOnCompleteListener(activity) {
                val result = it.result
                if (it.isSuccessful && result != null) {
                    for (snapshot in result) if (snapshot.exists()) this.add(
                        snapshot.toObject(
                            Contact::class.java
                        ).copy(name = contact.name) // Use save name
                    )

                    setContactList(this)
                }
            }
    }
}