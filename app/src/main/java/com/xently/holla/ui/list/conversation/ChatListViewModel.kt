package com.xently.holla.ui.list.conversation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.xently.holla.data.repository.schema.IMessageRepository
import com.xently.holla.viewmodels.ChatViewModel

class ChatListViewModel(repository: IMessageRepository) : ChatViewModel(repository)

class ChatListViewModelFactory(private val repository: IMessageRepository) :
    ViewModelProvider.NewInstanceFactory() {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T =
        ChatListViewModel(repository) as T
}
