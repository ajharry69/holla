package com.xently.holla.data.source.schema

import androidx.lifecycle.LiveData
import com.xently.holla.data.Result
import com.xently.holla.data.model.Contact
import com.xently.holla.utils.Type

/**
 * Contains helper methods to help find list of contacts which are already registered with the app
 * (already members of Holla) and saved as part of currently signed in user's phone contacts(saved
 * in phone)
 */
interface IUserDataSource : IBaseDataSource {
    val observableContact: LiveData<Contact>

    suspend fun saveContact(type: Type = Type.CREATE): Result<Unit>

    suspend fun signOut(): Result<Unit>

    suspend fun updateFCMToken(token: String?)
}