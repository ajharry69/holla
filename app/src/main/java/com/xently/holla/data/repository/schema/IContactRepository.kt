package com.xently.holla.data.repository.schema

import android.app.Activity
import androidx.lifecycle.LiveData
import com.xently.holla.data.model.Contact

/**
 * Contains helper methods to help find list of contacts which are already registered with the app
 * (already members of Holla) and saved as part of currently signed in user's phone contacts(saved
 * in phone)
 */
interface IContactRepository {
    suspend fun getContactList(activity: Activity): List<Contact>

    suspend fun getObservableContactList(): LiveData<List<Contact>>
}