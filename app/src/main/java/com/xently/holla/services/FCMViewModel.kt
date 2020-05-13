package com.xently.holla.services

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.xently.holla.data.repository.schema.IConversationRepository
import com.xently.holla.data.repository.schema.IMessageRepository
import com.xently.holla.data.repository.schema.IUserRepository
import com.xently.holla.viewmodels.ConversationViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class FCMViewModel(
    conversationRepository: IConversationRepository,
    repository: IMessageRepository,
    private val userRepository: IUserRepository
) : ConversationViewModel(conversationRepository, repository) {
    fun updateFCMToken(token: String) {
        viewModelScope.launch(Dispatchers.IO) {
            userRepository.updateFCMToken(token)
        }
    }
}

@Suppress("UNCHECKED_CAST")
class FCMViewModelFactory(
    private val conversationRepository: IConversationRepository,
    private val repository: IMessageRepository,
    private val userRepository: IUserRepository
) : ViewModelProvider.NewInstanceFactory() {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T =
        FCMViewModel(conversationRepository, repository, userRepository) as T
}