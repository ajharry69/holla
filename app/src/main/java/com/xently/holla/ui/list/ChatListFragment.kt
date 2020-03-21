package com.xently.holla.ui.list

import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.xently.holla.data.model.Chat
import com.xently.holla.viewmodels.ChatViewModel
import com.xently.xui.ListFragment
import com.xently.xui.adapters.list.ListAdapter

abstract class ChatListFragment : ListFragment<Chat>(), FirebaseAuth.AuthStateListener {

    protected abstract val viewModel: ChatViewModel

    protected abstract val listAdapter: ListAdapter<Chat, *>

    override fun onCreateRecyclerView(recyclerView: RecyclerView): RecyclerView {
        return super.onCreateRecyclerView(recyclerView).apply {
            adapter = listAdapter
        }
    }

    override fun onAuthStateChanged(p0: FirebaseAuth) {

    }
}