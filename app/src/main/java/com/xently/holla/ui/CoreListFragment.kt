package com.xently.holla.ui

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.provider.Settings
import androidx.lifecycle.Observer
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.xently.holla.Log
import com.xently.holla.R
import com.xently.holla.viewmodels.BaseViewModel
import com.xently.xui.ListFragment
import com.xently.xui.utils.ui.fragment.requestFeaturePermission
import com.xently.xui.utils.ui.fragment.showSnackBar

abstract class CoreListFragment<T> : ListFragment<T>(), FirebaseAuth.AuthStateListener {
    private var snackbar: Snackbar? = null
    protected open val onReadContactsPermissionGranted: () -> Unit = {
        snackbar?.dismiss()
        onRefreshRequested(false)
    }

    protected open val onReadContactsPermissionDenied: () -> Unit = {
        snackbar = showSnackBar(
            R.string.read_contacts_permission_required,
            Snackbar.LENGTH_INDEFINITE,
            getString(R.string.allow)
        ) {
            val intent = Intent(Settings.ACTION_APPLICATION_SETTINGS)
            if (intent.resolveActivity(requireActivity().packageManager) != null) {
                startActivity(intent)
            }
        }
    }

    protected abstract val viewModel: BaseViewModel

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        requestFeaturePermission(
            Manifest.permission.READ_CONTACTS,
            PRC_READ_CONTACTS,
            onReadContactsPermissionGranted,
            onReadContactsPermissionDenied
        )
        viewModel.getObservableException().observe(viewLifecycleOwner, Observer {
            if (it == null) return@Observer
            it.message?.let { it1 -> showSnackBar(it1, Snackbar.LENGTH_LONG) }
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
                onReadContactsPermissionDenied.invoke()
                return
            }
            // Add other 'when' lines to check for other permissions this app might request.
            else -> super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        }
    }

    override fun onAuthStateChanged(p0: FirebaseAuth) {
        if (p0.currentUser != null) onRefreshRequested(false) else {
            Log.show(
                CoreListFragment::class.java.simpleName,
                "Auth status changed! User null? true...",
                type = Log.Type.ERROR
            )
        }
    }

    companion object {
        private const val PRC_READ_CONTACTS = 2356
    }
}