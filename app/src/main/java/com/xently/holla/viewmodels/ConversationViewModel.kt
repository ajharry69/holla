package com.xently.holla.viewmodels

import androidx.lifecycle.liveData
import androidx.lifecycle.viewModelScope
import com.xently.holla.data.Source
import com.xently.holla.data.repository.schema.IConversationRepository
import com.xently.holla.data.repository.schema.IMessageRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

abstract class ConversationViewModel(
    private val repository: IConversationRepository,
    messageRepository: IMessageRepository
) : MessageViewModel(messageRepository) {

    fun getObservableConversations() = liveData(viewModelScope.coroutineContext) {
        emitSource(repository.getObservableConversations())
    }

    suspend fun deleteConversation(id: String, source: Source? = null) =
        withContext(viewModelScope.coroutineContext + Dispatchers.IO) {
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