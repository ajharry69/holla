package com.xently.holla.viewmodels

import android.app.Activity
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import androidx.lifecycle.viewModelScope
import com.xently.holla.data.model.Contact
import com.xently.holla.data.repository.schema.IContactRepository
import kotlinx.coroutines.runBlocking

abstract class ContactViewModel(private val repository: IContactRepository) : ViewModel() {
    fun getContactList(activity: Activity): List<Contact> {
        return runBlocking(viewModelScope.coroutineContext) {
            repository.getContactList(activity)
        }
    }

    fun getObservableContactList(): LiveData<List<Contact>> {
        return liveData(viewModelScope.coroutineContext) {
            emitSource(repository.getObservableContactList())
        }
    }
}