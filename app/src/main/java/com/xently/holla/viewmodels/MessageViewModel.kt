package com.xently.holla.viewmodels

import androidx.lifecycle.liveData
import androidx.lifecycle.viewModelScope
import com.xently.holla.data.Source
import com.xently.holla.data.model.Contact
import com.xently.holla.data.model.Message
import com.xently.holla.data.repository.schema.IMessageRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

abstract class MessageViewModel(private val repository: IMessageRepository) :
    BaseViewModel(repository) {

    fun getObservableMessages(contact: Contact) = liveData(viewModelScope.coroutineContext) {
        emitSource(repository.getObservableMessages(contact))
    }

    fun getObservableMessages(contactId: String) = liveData(viewModelScope.coroutineContext) {
        emitSource(repository.getObservableMessages(contactId))
    }

    suspend fun sendMessage(message: Message, destination: Source? = null) =
        withContext(viewModelScope.coroutineContext + Dispatchers.IO) {
            repository.sendMessage(message, destination)
        }

    suspend fun deleteMessage(message: Message, source: Source? = null) =
        withContext(viewModelScope.coroutineContext + Dispatchers.IO) {
            repository.deleteMessage(message, source)
        }

    fun getMessages(contact: Contact?) {
        viewModelScope.launch(Dispatchers.IO) {
            contact?.let { repository.getMessages(it) }
        }
    }

    fun getMessages(contactId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.getMessages(contactId)
        }
    }

    fun getMessage(senderId: String, id: String) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.getMessage(senderId, id)
        }
    }

    /**
     * @param source specifies from where message with [id] is to be deleted. `null` means delete
     * from all(remote & cache) sources
     */
    fun deleteMessage(id: String, source: Source? = null) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteMessage(id, source)
        }
    }

    /**
     * @param source specifies from where messages with [contactId] as either the receiver or sender
     * is to be deleted. `null` means delete from all(remote & cache) sources
     */
    fun deleteMessages(contactId: String, source: Source? = null) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteMessages(contactId, source)
        }
    }
}