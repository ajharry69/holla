package com.xently.holla.ui.contacts

import android.Manifest
import android.content.pm.PackageManager
import android.database.Cursor
import android.net.Uri
import android.os.Bundle
import android.provider.ContactsContract
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import androidx.cursoradapter.widget.CursorAdapter
import androidx.cursoradapter.widget.SimpleCursorAdapter
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.loader.app.LoaderManager
import androidx.loader.content.CursorLoader
import androidx.loader.content.Loader
import com.xently.holla.App
import com.xently.holla.R
import com.xently.holla.databinding.ContactsFragmentBinding
import com.xently.holla.requestFeaturePermission

class ContactsFragment : Fragment(), LoaderManager.LoaderCallbacks<Cursor>,
    AdapterView.OnItemClickListener {

    // Define variables for the contact the user selects
    // The contact's _ID value
    var contactId: Long = 0
    // The contact's LOOKUP_KEY
    var contactKey: String? = null
    // A content URI for the selected contact
    var contactUri: Uri? = null
    // An adapter that binds the result Cursor to the ListView
    private var cursorAdapter: SimpleCursorAdapter? = null

    // Defines a variable for the search string
    private var searchString: String = "A"
    // Defines the array to hold values that replace the ?
    private val selectionArgs = arrayOf(searchString)

    private var _binding: ContactsFragmentBinding? = null
    private val binding: ContactsFragmentBinding
        get() = _binding!!

    private val onReadContactsPermissionGranted = {
        // Initializes the loader
        LoaderManager.getInstance(this).initLoader(0, null, this)
    }

    private val viewModel: ContactsViewModel by viewModels {
        ContactsViewModelFactory((requireContext().applicationContext as App).contactRepository)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestFeaturePermission(
            Manifest.permission.READ_CONTACTS,
            PRC_READ_CONTACTS,
            onReadContactsPermissionGranted
        )
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

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        binding.list.onItemClickListener = this
        // Gets a CursorAdapter
        cursorAdapter = SimpleCursorAdapter(
            requireContext(),
            R.layout.contact_item,
            null,
            FROM_COLUMNS,
            TO_IDS,
            0
        )
        // Sets the adapter for the ListView
        binding.list.adapter = cursorAdapter
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

    override fun onCreateLoader(id: Int, args: Bundle?): Loader<Cursor> {
        /*
         * Makes search string into pattern and
         * stores it in the selection array
         */
        selectionArgs[0] = "%$searchString%"
        // Starts the query
        return activity?.let {
            return CursorLoader(
                it,
                ContactsContract.Contacts.CONTENT_URI,
                PROJECTION,
                SELECTION,
                selectionArgs,
                null
            )
        } ?: throw IllegalStateException()
    }

    override fun onLoadFinished(loader: Loader<Cursor>, data: Cursor?) {
        // Put the result Cursor in the adapter for the ListView
        cursorAdapter?.swapCursor(data)
    }

    override fun onLoaderReset(loader: Loader<Cursor>) {
        // Delete the reference to the existing Cursor
        cursorAdapter?.swapCursor(null)
    }

    override fun onItemClick(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        // Get the Cursor
        val cursor: Cursor? = (parent?.adapter as? CursorAdapter)?.cursor?.apply {
            // Move to the selected contact
            moveToPosition(position)
            // Get the _ID value
            contactId = getLong(CONTACT_ID_INDEX)
            // Get the selected LOOKUP KEY
            contactKey = getString(CONTACT_KEY_INDEX)
            // Create the contact's content Uri
            contactUri = ContactsContract.Contacts.getLookupUri(contactId, contactKey)
            /*
             * You can use contactUri as the content URI for retrieving
             * the details for a contact.
             */
        }
    }

    companion object {
        /**
         * Defines an array that contains column names to move from the Cursor to the ListView.
         */
        private val FROM_COLUMNS: Array<String> = arrayOf(
            ContactsContract.Contacts.DISPLAY_NAME_PRIMARY
        )

        /**
         * Defines an array that contains resource ids for the layout views that get the Cursor
         * column contents. The id is pre-defined in the Android framework, so it is prefaced with "android.R.id"
         */
        private val TO_IDS: IntArray = intArrayOf(R.id.name)

        private const val SELECTION: String =
            "${ContactsContract.Contacts.DISPLAY_NAME_PRIMARY} LIKE ?"

        private val PROJECTION: Array<out String> = arrayOf(
            ContactsContract.Contacts._ID,
            ContactsContract.Contacts.LOOKUP_KEY,
            ContactsContract.Contacts.DISPLAY_NAME_PRIMARY
        )

        /**
         * Indices are equal to the order of column names in [PROJECTION]
         */
        // The column index for the _ID column
        private const val CONTACT_ID_INDEX: Int = 0
        // The column index for the CONTACT_KEY column
        private const val CONTACT_KEY_INDEX: Int = 1

        private const val PRC_READ_CONTACTS = 4321
    }
}
