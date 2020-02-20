package com.xently.holla.ui.chat.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.xently.holla.data.repository.schema.IChatRepository

class ChatListViewModel(private val repository: IChatRepository) : ViewModel() {
    // TODO: Implement the ViewModel
}

class ChatListViewModelFactory(private val repository: IChatRepository) :
    ViewModelProvider.NewInstanceFactory() {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T =
        ChatListViewModel(repository) as T
}
