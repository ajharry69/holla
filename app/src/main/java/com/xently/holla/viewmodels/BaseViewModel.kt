package com.xently.holla.viewmodels

import androidx.lifecycle.ViewModel
import com.xently.holla.data.model.Contact
import com.xently.holla.data.repository.schema.IBaseRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

abstract class BaseViewModel(private val repository: IBaseRepository) : ViewModel() {
    suspend fun getLocalContact(contact: Contact) = withContext(Dispatchers.IO) {
        repository.getLocalContact(contact)
    }

    fun getObservableException() = repository.getObservableException()
}