package com.xently.holla.ui.contacts

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.xently.holla.data.repository.schema.IContactRepository
import com.xently.holla.viewmodels.ContactViewModel

class ContactListViewModel(repository: IContactRepository) : ContactViewModel(repository)

class ContactListViewModelFactory(private val repository: IContactRepository) :
    ViewModelProvider.NewInstanceFactory() {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T =
        ContactListViewModel(repository) as T
}