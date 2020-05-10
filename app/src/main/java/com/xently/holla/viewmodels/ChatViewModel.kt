package com.xently.holla.viewmodels

import androidx.lifecycle.liveData
import androidx.lifecycle.viewModelScope
import com.xently.holla.data.model.Message
import com.xently.holla.data.model.Contact
import com.xently.holla.data.repository.schema.IMessageRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext

abstract class ChatViewModel(private val repository: IMessageRepository) : BaseViewModel(repository) {

    fun getObservableConversations(contact: Contact?) = liveData(viewModelScope.coroutineContext) {
        emitSource(repository.getObservableChats(contact))
    }

    suspend fun sendMessage(message: Message) =
        withContext(viewModelScope.coroutineContext + Dispatchers.IO) {
            repository.sendMessage(message)
        }

    fun deleteMessage(message: Message) = runBlocking(viewModelScope.coroutineContext) {
        repository.deleteMessage(message)
    }

    fun getConversations(contact: Contact?) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.getChats(contact)
        }
    }
}