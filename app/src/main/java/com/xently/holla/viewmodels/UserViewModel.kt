package com.xently.holla.viewmodels

import androidx.lifecycle.viewModelScope
import com.xently.holla.data.repository.schema.IUserRepository
import com.xently.holla.utils.Type
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

abstract class UserViewModel(private val repository: IUserRepository) : BaseViewModel(repository) {
    val observableContact
        get() = repository.observableContact

    suspend fun saveContact(type: Type = Type.CREATE) =
        withContext(viewModelScope.coroutineContext + Dispatchers.IO) {
            repository.saveContact(type)
        }

    suspend fun signOut() = withContext(viewModelScope.coroutineContext + Dispatchers.IO) {
        repository.signOut()
    }
}