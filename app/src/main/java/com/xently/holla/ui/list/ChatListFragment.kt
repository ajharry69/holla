package com.xently.holla.ui.list

import android.os.Bundle
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.RecyclerView
import com.xently.holla.data.model.Chat
import com.xently.holla.data.model.Contact
import com.xently.holla.ui.CoreListFragment
import com.xently.holla.viewmodels.ChatViewModel
import com.xently.xui.adapters.list.ListAdapter

abstract class ChatListFragment : CoreListFragment<Chat>() {

    abstract override val viewModel: ChatViewModel

    protected abstract val listAdapter: ListAdapter<Chat, *>

    protected open val contact: Contact?
        get() = null

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel.getObservableConversations(contact).observe(viewLifecycleOwner, Observer {
            onObservableListChanged(it)

            listAdapter.submitList(it)
        })
    }

    override fun onRefreshRequested(forced: Boolean) {
        viewModel.getConversations(contact)
    }

    override fun onCreateRecyclerView(recyclerView: RecyclerView): RecyclerView {
        return super.onCreateRecyclerView(recyclerView).apply {
            adapter = listAdapter
        }
    }
}