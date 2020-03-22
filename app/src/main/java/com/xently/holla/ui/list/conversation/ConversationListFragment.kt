package com.xently.holla.ui.list.conversation

import android.content.Context
import android.view.View
import androidx.fragment.app.viewModels
import androidx.navigation.Navigation
import androidx.navigation.findNavController
import com.xently.holla.App
import com.xently.holla.R
import com.xently.holla.adapters.list.ConversationListAdapter
import com.xently.holla.data.model.Chat
import com.xently.holla.ui.list.ChatListFragment
import com.xently.holla.ui.list.conversation.ConversationListFragmentDirections.Companion.actionContactList
import com.xently.holla.ui.list.conversation.ConversationListFragmentDirections.Companion.actionMessage
import com.xently.xui.adapters.list.ListAdapter

class ConversationListFragment : ChatListFragment() {

    override val viewModel: ChatListViewModel by viewModels {
        ChatListViewModelFactory((requireContext().applicationContext as App).chatRepository)
    }

    override val listAdapter: ListAdapter<Chat, *> by lazy {
        ConversationListAdapter().apply {
            listItemClickListener = this@ConversationListFragment
        }
    }

    override val noDataText: CharSequence?
        get() = getString(R.string.no_conversations)

    override fun onFabClickListener(context: Context): View.OnClickListener? {
        binding.fab.setImageResource(R.drawable.ic_chat)
        return Navigation.createNavigateOnClickListener(actionContactList())
    }

    override fun onListItemClick(model: Chat, view: View) {
        view.findNavController().navigate(actionMessage(model.conversationContact))
    }
}
