package com.xently.holla.data.repository.schema

import androidx.lifecycle.LiveData
import com.xently.holla.data.model.Contact
import com.xently.holla.data.repository.Result
import com.xently.holla.utils.Type

interface IUserRepository : IBaseRepository {
    val observableContact: LiveData<Contact>

    suspend fun saveContact(type: Type = Type.CREATE): Result<Void>

    suspend fun signOut(): Result<Void>

    suspend fun updateFCMToken(token: String?)
}