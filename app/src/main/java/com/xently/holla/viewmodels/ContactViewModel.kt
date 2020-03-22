package com.xently.holla.viewmodels

import android.app.Activity
import androidx.lifecycle.liveData
import androidx.lifecycle.viewModelScope
import com.xently.holla.data.repository.schema.IContactRepository
import kotlinx.coroutines.runBlocking

abstract class ContactViewModel(private val repository: IContactRepository) :
    BaseViewModel(repository) {
    fun getContactList(activity: Activity) = runBlocking(viewModelScope.coroutineContext) {
        repository.getContactList(activity)
    }

    fun getObservableContactList() = liveData(viewModelScope.coroutineContext) {
        emitSource(repository.getObservableContactList())
    }
}