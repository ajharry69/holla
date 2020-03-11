package com.xently.holla.ui.message.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.xently.holla.data.repository.schema.IChatRepository

class MessageListViewModel(private val repository: IChatRepository) : ViewModel() {
    // TODO: Implement the ViewModel
}

class MessageListViewModelFactory(private val repository: IChatRepository) :
    ViewModelProvider.NewInstanceFactory() {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T = MessageListViewModel(
        repository
    ) as T
}
