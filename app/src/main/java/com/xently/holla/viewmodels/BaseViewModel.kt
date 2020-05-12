package com.xently.holla.viewmodels

import androidx.lifecycle.ViewModel
import com.xently.holla.data.model.Contact
import com.xently.holla.data.repository.schema.IBaseRepository

abstract class BaseViewModel(private val repository: IBaseRepository) : ViewModel() {
    fun getLocalContact(contact: Contact) = repository.getLocalContact(contact)

    fun getObservableException() = repository.getObservableException()
}