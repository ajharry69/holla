package com.xently.holla.ui.list.conversation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.xently.holla.data.repository.schema.IConversationRepository
import com.xently.holla.viewmodels.ConversationViewModel

class ConversationListViewModel(repository: IConversationRepository) : ConversationViewModel(repository)

class ConversationListViewModelFactory(private val repository: IConversationRepository) :
    ViewModelProvider.NewInstanceFactory() {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T =
        ConversationListViewModel(repository) as T
}
