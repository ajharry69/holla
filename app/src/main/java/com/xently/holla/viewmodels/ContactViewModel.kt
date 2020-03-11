package com.xently.holla.viewmodels

import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.xently.holla.data.model.Contact
import com.xently.holla.data.repository.schema.IContactRepository

abstract class ContactViewModel(private val repository: IContactRepository) : ViewModel() {
    fun getContactList(activity: FragmentActivity): List<Contact> =
        repository.getContactList(activity)

    fun getObservableContactList(): LiveData<List<Contact>> = repository.getObservableContactList()
}