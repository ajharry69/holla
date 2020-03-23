package com.xently.holla.services

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.xently.holla.data.repository.schema.IChatRepository
import com.xently.holla.data.repository.schema.IUserRepository
import com.xently.holla.viewmodels.ChatViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class FCMViewModel(
    repository: IChatRepository,
    private val userRepository: IUserRepository
) : ChatViewModel(repository) {
    fun updateFCMToken(token: String) {
        viewModelScope.launch(Dispatchers.IO) {
            userRepository.updateFCMToken(token)
        }
    }
}

@Suppress("UNCHECKED_CAST")
class FCMViewModelFactory(
    private val repository: IChatRepository,
    private val userRepository: IUserRepository
) : ViewModelProvider.NewInstanceFactory() {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T =
        FCMViewModel(repository, userRepository) as T
}