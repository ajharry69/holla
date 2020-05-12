package com.xently.holla.ui.message

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.xently.holla.data.repository.schema.IMessageRepository

class MessageViewModel(repository: IMessageRepository) :
    com.xently.holla.viewmodels.MessageViewModel(repository)

class MessageViewModelFactory(private val repository: IMessageRepository) :
    ViewModelProvider.NewInstanceFactory() {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T =
        MessageViewModel(repository) as T
}
