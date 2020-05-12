package com.xently.holla.ui.contacts

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.viewModelScope
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.xently.holla.App
import com.xently.holla.Log
import com.xently.holla.R
import com.xently.holla.adapters.list.ContactsListAdapter
import com.xently.holla.data.model.Contact
import com.xently.holla.ui.CoreListFragment
import com.xently.holla.ui.contacts.ContactListFragmentDirections.Companion.actionMessage
import kotlinx.coroutines.launch

class ContactListFragment : CoreListFragment<Contact>() {

    private val contactsListAdapter: ContactsListAdapter by lazy {
        ContactsListAdapter().apply {
            listItemClickListener = this@ContactListFragment
        }
    }

    override val viewModel: ContactListViewModel by viewModels {
        ContactListViewModelFactory((requireContext().applicationContext as App).contactRepository)
    }

    override val noDataText: CharSequence?
        get() = getString(R.string.no_contacts, getString(R.string.app_name))

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel.getObservableContactList().observe(viewLifecycleOwner, Observer {
            onObservableListChanged(it)
            contactsListAdapter.submitList(it)
        })
    }

    override fun onCreateRecyclerView(rv: RecyclerView): RecyclerView {
        return super.onCreateRecyclerView(rv).apply {
            adapter = contactsListAdapter
        }
    }

    override fun onRefreshRequested(forced: Boolean) {
        viewModel.run {
            viewModelScope.launch {
                getContactList()
            }
        }
    }

    override fun onListItemClick(model: Contact, view: View) {
        view.findNavController().navigate(actionMessage(model, model.id))
    }

    override fun onListItemLongClick(model: Contact, view: View): Boolean {
        Log.show("FCMService", "Contact Long Click: $model")
        return true
    }
}
