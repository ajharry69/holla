package com.xently.holla.viewmodels

import androidx.lifecycle.liveData
import androidx.lifecycle.viewModelScope
import com.xently.holla.data.repository.schema.IContactRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

abstract class ContactViewModel(private val repository: IContactRepository) :
    BaseViewModel(repository) {
    suspend fun getContactList() = withContext(viewModelScope.coroutineContext + Dispatchers.IO) {
        repository.getContactList()
    }

    fun getObservableContactList() = liveData(viewModelScope.coroutineContext) {
        emitSource(repository.getObservableContactList())
    }
}