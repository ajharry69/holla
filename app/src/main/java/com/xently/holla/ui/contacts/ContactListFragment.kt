package com.xently.holla.ui.contacts

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.xently.holla.App
import com.xently.holla.Log
import com.xently.holla.R
import com.xently.holla.adapters.list.ContactsListAdapter
import com.xently.holla.data.model.Contact
import com.xently.holla.ui.contacts.ContactListFragmentDirections.Companion.actionMessage
import com.xently.xui.ListFragment

class ContactListFragment : ListFragment<Contact>() {

    private val onReadContactsPermissionGranted = {
        onRefreshRequested(false)
    }

    private val contactsListAdapter: ContactsListAdapter by lazy {
        ContactsListAdapter().apply {
            listItemClickListener = this@ContactListFragment
        }
    }

    private val viewModel: ContactListViewModel by viewModels {
        ContactListViewModelFactory((requireContext().applicationContext as App).contactRepository)
    }

    override val noDataText: CharSequence?
        get() = getString(R.string.no_contacts, getString(R.string.app_name))

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        requestFeaturePermission(
            Manifest.permission.READ_CONTACTS,
            PRC_READ_CONTACTS,
            onReadContactsPermissionGranted
        )
        // Sets the adapter for the ListView
        viewModel.getObservableContactList().observe(viewLifecycleOwner, Observer {
            onObservableListChanged(it)
            contactsListAdapter.submitList(it)
        })
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            PRC_READ_CONTACTS -> {
                // If request is cancelled, the result arrays are empty.
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    // permission was granted, yay! Do the contacts-related task you need to do
                    onReadContactsPermissionGranted.invoke()
                    return
                }
                // permission denied! Disable the functionality that depends on this permission.
                return
            }
            // Add other 'when' lines to check for other permissions this app might request.
            else -> super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        }
    }

    override fun onCreateRecyclerView(recyclerView: RecyclerView): RecyclerView {
        return super.onCreateRecyclerView(recyclerView).apply {
            adapter = contactsListAdapter
        }
    }

    override fun onRefreshRequested(forced: Boolean) {
        viewModel.getContactList(requireActivity())
    }

    override fun onListItemClick(model: Contact, view: View) {
        view.findNavController().navigate(actionMessage(model))
    }

    override fun onListItemLongClick(model: Contact, view: View): Boolean {
        Log.show("FCMService", "Contact Long Click: $model")
        return true
    }

    companion object {
        private const val PRC_READ_CONTACTS = 4321
    }
}
