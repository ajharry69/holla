package com.xently.holla.ui.list.conversation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.xently.holla.data.repository.schema.IConversationRepository
import com.xently.holla.data.repository.schema.IMessageRepository
import com.xently.holla.viewmodels.ConversationViewModel

class ConversationListViewModel(
    repository: IConversationRepository,
    messageRepository: IMessageRepository
) : ConversationViewModel(repository, messageRepository)

class ConversationListViewModelFactory(
    private val repository: IConversationRepository,
    private val messageRepository: IMessageRepository
) : ViewModelProvider.NewInstanceFactory() {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T =
        ConversationListViewModel(repository, messageRepository) as T
}
