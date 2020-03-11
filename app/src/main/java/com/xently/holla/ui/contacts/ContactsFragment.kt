package com.xently.holla.ui.contacts

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.xently.holla.App
import com.xently.holla.adapters.list.ContactsListAdapter
import com.xently.holla.databinding.ContactsFragmentBinding
import com.xently.holla.requestFeaturePermission

class ContactsFragment : Fragment() {

    private val onReadContactsPermissionGranted = {
        viewModel.getContactList(requireActivity())
    }

    private var _binding: ContactsFragmentBinding? = null
    private val binding: ContactsFragmentBinding
        get() = _binding!!

    private val contactsListAdapter: ContactsListAdapter by lazy {
        ContactsListAdapter()
    }

    private val viewModel: ContactsViewModel by viewModels {
        ContactsViewModelFactory((requireContext().applicationContext as App).contactRepository)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = ContactsFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        with(binding.list) {
            adapter = contactsListAdapter
            layoutManager = LinearLayoutManager(context)
            setHasFixedSize(true)
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        // Sets the adapter for the ListView
        viewModel.getObservableContactList().observe(viewLifecycleOwner, Observer {
            contactsListAdapter.submitList(it)
        })
    }

    override fun onResume() {
        super.onResume()
        requestFeaturePermission(
            Manifest.permission.READ_CONTACTS,
            PRC_READ_CONTACTS,
            onReadContactsPermissionGranted
        )
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

    companion object {
        private const val PRC_READ_CONTACTS = 4321
    }
}
