package com.xently.holla.ui.list.message

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.xently.holla.data.repository.schema.IChatRepository
import com.xently.holla.viewmodels.ChatViewModel

class MessageListViewModel(repository: IChatRepository) : ChatViewModel(repository)

class MessageListViewModelFactory(private val repository: IChatRepository) :
    ViewModelProvider.NewInstanceFactory() {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T = MessageListViewModel(
        repository
    ) as T
}
