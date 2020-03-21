package com.xently.holla.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import androidx.lifecycle.viewModelScope
import com.xently.holla.data.model.Chat
import com.xently.holla.data.model.Contact
import com.xently.holla.data.repository.schema.IChatRepository
import kotlinx.coroutines.runBlocking

abstract class ChatViewModel(private val repository: IChatRepository) : ViewModel() {
    fun getObservableException() = repository.getObservableException()

    fun getObservableConversations(contact: Contact?) = liveData(viewModelScope.coroutineContext) {
        emitSource(repository.getObservableConversations(contact))
    }

    fun sendMessage(message: Chat) = runBlocking(viewModelScope.coroutineContext) {
        repository.sendMessage(message)
    }

    fun deleteMessage(message: Chat) = runBlocking(viewModelScope.coroutineContext) {
        repository.deleteMessage(message)
    }

    fun getConversations(contact: Contact?) = runBlocking(viewModelScope.coroutineContext) {
        repository.getConversations(contact)
    }
}