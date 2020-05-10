package com.xently.holla.ui.list.message

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentFactory
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.xently.holla.App
import com.xently.holla.R
import com.xently.holla.adapters.list.MessageListAdapter
import com.xently.holla.data.model.Message
import com.xently.holla.data.model.Contact
import com.xently.holla.ui.list.ChatListFragment
import com.xently.holla.viewmodels.ChatViewModel

class MessageListFragment : ChatListFragment() {
    private val args: MessageListFragmentArgs by navArgs()

    override val contact: Contact? by lazy {
        arguments?.getParcelable(ARG_KEY_CONTACT) ?: args.argsContact
    }

    override val noDataText: CharSequence?
        get() = getString(R.string.no_messages)

    override val viewModel: ChatViewModel by viewModels {
        MessageListViewModelFactory((requireContext().applicationContext as App).chatRepository)
    }

    override val listAdapter: MessageListAdapter by lazy {
        MessageListAdapter().apply {
            listItemClickListener = this@MessageListFragment
        }
    }

    override fun onListItemClick(model: Message, view: View) = Unit

    override fun onCreateRecyclerView(recyclerView: RecyclerView): RecyclerView {
        return super.onCreateRecyclerView(recyclerView).apply {
            layoutManager = LinearLayoutManager(requireContext()).apply {
                reverseLayout = true
//                stackFromEnd = true
            }
            addOnLayoutChangeListener { _, _, _, _, bottom, _, _, _, oldBottom ->
                if (bottom < oldBottom) {
                    recyclerView.postDelayed({ recyclerView.smoothScrollToPosition(0) }, 100)
                }
            }
        }
    }

    companion object {
        private const val ARG_KEY_CONTACT = "ARG_KEY_CONTACT"
        fun newInstance(contact: Contact): MessageListFragment = MessageListFragment().apply {
            arguments = Bundle().apply {
                putParcelable(ARG_KEY_CONTACT, contact)
            }
        }
    }
}

class MessageListFragmentFactory(private val contact: Contact) : FragmentFactory() {
    override fun instantiate(classLoader: ClassLoader, className: String): Fragment =
        MessageListFragment.newInstance(contact)
}



