package com.xently.holla.ui.list.message

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.xently.holla.data.repository.schema.IMessageRepository
import com.xently.holla.viewmodels.MessageViewModel

class MessageListViewModel(repository: IMessageRepository) : MessageViewModel(repository)

class MessageListViewModelFactory(private val repository: IMessageRepository) :
    ViewModelProvider.NewInstanceFactory() {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T = MessageListViewModel(
        repository
    ) as T
}
