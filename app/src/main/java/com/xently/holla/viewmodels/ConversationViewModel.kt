package com.xently.holla.viewmodels

import androidx.lifecycle.liveData
import androidx.lifecycle.viewModelScope
import com.xently.holla.data.Source
import com.xently.holla.data.repository.schema.IConversationRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

abstract class ConversationViewModel(private val repository: IConversationRepository) :
    BaseViewModel(repository) {

    fun getObservableConversations() = liveData(viewModelScope.coroutineContext) {
        emitSource(repository.getObservableConversations())
    }

    fun deleteConversation(id: String, source: Source? = null) =
        runBlocking(viewModelScope.coroutineContext) {
            repository.deleteConversation(id, source)
        }

    fun getConversations() {
        viewModelScope.launch(Dispatchers.IO) {
            repository.getConversations()
        }
    }

    fun getConversation(contactId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.getConversation(contactId)
        }
    }
}