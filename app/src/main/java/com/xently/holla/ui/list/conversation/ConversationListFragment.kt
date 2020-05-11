package com.xently.holla.ui.list.conversation

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.Navigation
import androidx.navigation.findNavController
import com.xently.holla.App
import com.xently.holla.R
import com.xently.holla.adapters.list.ConversationListAdapter
import com.xently.holla.data.model.Conversation
import com.xently.holla.ui.CoreListFragment
import com.xently.holla.ui.list.conversation.ConversationListFragmentDirections.Companion.actionContactList
import com.xently.holla.ui.list.conversation.ConversationListFragmentDirections.Companion.actionMessage

class ConversationListFragment : CoreListFragment<Conversation>() {

    private val listAdapter: ConversationListAdapter by lazy {
        ConversationListAdapter().apply {
            listItemClickListener = this@ConversationListFragment
        }
    }

    override val viewModel: ConversationListViewModel by viewModels {
        ConversationListViewModelFactory((requireContext().applicationContext as App).conversationRepository)
    }

    override val noDataText: CharSequence?
        get() = getString(R.string.no_conversations)

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel.getObservableConversations().observe(viewLifecycleOwner, Observer {
            onObservableListChanged(it)

            listAdapter.submitList(it)
        })
    }

    override fun onRefreshRequested(forced: Boolean) {
        viewModel.getConversations()
    }

    override fun onFabClickListener(context: Context): View.OnClickListener? {
        binding.fab.setImageResource(R.drawable.ic_chat)
        return Navigation.createNavigateOnClickListener(actionContactList())
    }

    override fun onListItemClick(model: Conversation, view: View) {
        view.findNavController().navigate(actionMessage(model.mate, model.mateId))
    }
}
