package com.xently.holla.ui.message

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.xently.holla.data.repository.schema.IChatRepository
import com.xently.holla.viewmodels.ChatViewModel

class MessageViewModel(repository: IChatRepository) : ChatViewModel(repository)

class MessageViewModelFactory(private val repository: IChatRepository) :
    ViewModelProvider.NewInstanceFactory() {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T = MessageViewModel(
        repository
    ) as T
}
