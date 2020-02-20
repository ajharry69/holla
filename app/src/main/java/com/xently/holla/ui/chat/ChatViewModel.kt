package com.xently.holla.ui.chat

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.xently.holla.data.repository.schema.IChatRepository

class ChatViewModel(private val repository: IChatRepository) : ViewModel() {
    // TODO: Implement the ViewModel
}

class ChatViewModelFactory(private val repository: IChatRepository) :
    ViewModelProvider.NewInstanceFactory() {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T = ChatViewModel(repository) as T
}
