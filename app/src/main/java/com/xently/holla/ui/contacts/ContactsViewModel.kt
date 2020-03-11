package com.xently.holla.ui.contacts

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.xently.holla.data.repository.schema.IContactRepository
import com.xently.holla.viewmodels.ContactViewModel

class ContactsViewModel(repository: IContactRepository) : ContactViewModel(repository)

class ContactsViewModelFactory(private val repository: IContactRepository) :
    ViewModelProvider.NewInstanceFactory() {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T =
        ContactsViewModel(repository) as T
}