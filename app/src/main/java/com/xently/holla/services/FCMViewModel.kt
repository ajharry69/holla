package com.xently.holla.services

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.xently.holla.data.repository.schema.IMessageRepository
import com.xently.holla.data.repository.schema.IUserRepository
import com.xently.holla.viewmodels.MessageViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class FCMViewModel(
    repository: IMessageRepository,
    private val userRepository: IUserRepository
) : MessageViewModel(repository) {
    fun updateFCMToken(token: String) {
        viewModelScope.launch(Dispatchers.IO) {
            userRepository.updateFCMToken(token)
        }
    }
}

@Suppress("UNCHECKED_CAST")
class FCMViewModelFactory(
    private val repository: IMessageRepository,
    private val userRepository: IUserRepository
) : ViewModelProvider.NewInstanceFactory() {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T =
        FCMViewModel(repository, userRepository) as T
}