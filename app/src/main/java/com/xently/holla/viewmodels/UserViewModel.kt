package com.xently.holla.viewmodels

import com.xently.holla.data.repository.schema.IUserRepository
import com.xently.holla.utils.Type

abstract class UserViewModel(private val repository: IUserRepository) : BaseViewModel(repository) {
    val observableContact
        get() = repository.observableContact

    suspend fun saveContact(type: Type = Type.CREATE) = repository.saveContact(type)

    suspend fun signOut() = repository.signOut()
}