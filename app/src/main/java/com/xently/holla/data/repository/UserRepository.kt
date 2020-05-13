package com.xently.holla.data.repository

import androidx.lifecycle.LiveData
import com.xently.holla.data.Result
import com.xently.holla.data.model.Contact
import com.xently.holla.data.repository.schema.IUserRepository
import com.xently.holla.data.source.schema.IUserDataSource
import com.xently.holla.utils.Type

class UserRepository internal constructor(
    private val remoteDataSource: IUserDataSource,
    private val localDataSource: IUserDataSource
) : IUserRepository {
    override fun getObservableException() = localDataSource.getObservableException()

    override fun getLocalContact(contact: Contact) = localDataSource.getLocalContact(contact)

    override val observableContact: LiveData<Contact>
        get() = remoteDataSource.observableContact

    override suspend fun saveContact(type: Type): Result<Unit> {
        val result = remoteDataSource.saveContact(type)
        localDataSource.saveContact(type)
        return result
    }

    override suspend fun signOut(): Result<Unit> {
        val result = remoteDataSource.signOut()
        localDataSource.signOut()
        return result
    }

    override suspend fun updateFCMToken(token: String?) {
        val result = remoteDataSource.updateFCMToken(token)
        localDataSource.updateFCMToken(token)
        return result
    }
}