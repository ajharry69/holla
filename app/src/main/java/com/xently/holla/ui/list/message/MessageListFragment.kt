package com.xently.holla.ui.list.message

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentFactory
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.xently.holla.App
import com.xently.holla.R
import com.xently.holla.adapters.list.MessageListAdapter
import com.xently.holla.data.model.Message
import com.xently.holla.ui.CoreListFragment
import com.xently.holla.viewmodels.MessageViewModel

class MessageListFragment : CoreListFragment<Message>() {
    private val args: MessageListFragmentArgs? by lazy {
        arguments?.let { MessageListFragmentArgs.fromBundle(it) }
    }

    private val listAdapter: MessageListAdapter by lazy {
        MessageListAdapter().apply {
            listItemClickListener = this@MessageListFragment
        }
    }

    override val noDataText: CharSequence?
        get() = getString(R.string.no_messages)

    override val viewModel: MessageViewModel by viewModels {
        MessageListViewModelFactory((requireContext().applicationContext as App).chatRepository)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        val contactId: String = args?.argsContact?.id ?: args?.contactId ?: return
        viewModel.getObservableMessages(contactId).observe(viewLifecycleOwner, Observer {
            onObservableListChanged(it)

            listAdapter.submitList(it)
        })
    }

    override fun onRefreshRequested(forced: Boolean) {
        val contactId: String = args?.argsContact?.id ?: args?.contactId ?: return
        viewModel.getMessages(contactId)
    }

    override fun onListItemClick(model: Message, view: View) = Unit

    override fun onCreateRecyclerView(rv: RecyclerView): RecyclerView {
        return super.onCreateRecyclerView(rv).apply {
            adapter = listAdapter
            layoutManager = LinearLayoutManager(requireContext()).apply {
                reverseLayout = true
//                stackFromEnd = true
            }
            addOnLayoutChangeListener { _, _, _, _, bottom, _, _, _, oldBottom ->
                if (bottom < oldBottom) {
                    rv.postDelayed({ rv.smoothScrollToPosition(0) }, 100)
                }
            }
        }
    }

    companion object {
        fun newInstance(bundle: Bundle) = MessageListFragment().apply {
            arguments = bundle
        }
    }
}

class MessageListFragmentFactory(private val bundle: Bundle) : FragmentFactory() {
    override fun instantiate(classLoader: ClassLoader, className: String): Fragment =
        MessageListFragment.newInstance(bundle)
}



