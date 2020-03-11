package com.xently.holla.data.repository.schema

import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.LiveData
import com.xently.holla.data.model.Contact

/**
 * Contains helper methods to help find list of contacts which are already registered with the app
 * (already members of Holla) and saved as part of currently signed in user's phone contacts(saved
 * in phone)
 */
interface IContactRepository {
    fun getContactList(activity: FragmentActivity): List<Contact>

    fun getObservableContactList(): LiveData<List<Contact>>
}