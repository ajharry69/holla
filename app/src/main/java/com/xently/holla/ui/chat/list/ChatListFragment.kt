package com.xently.holla.ui.chat.list

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.ErrorCodes
import com.firebase.ui.auth.IdpResponse
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.xently.holla.App
import com.xently.holla.BuildConfig
import com.xently.holla.R
import com.xently.holla.databinding.ChatListFragmentBinding
import com.xently.holla.showSnackBar

class ChatListFragment : Fragment() {

    private var _binding: ChatListFragmentBinding? = null
    private val binding: ChatListFragmentBinding
        get() = _binding!!

    private val viewModel: ChatListViewModel by viewModels {
        ChatListViewModelFactory((requireContext().applicationContext as App).chatRepository)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = ChatListFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onResume() {
        super.onResume()
        if (FirebaseAuth.getInstance().currentUser == null) {
            requestSignIn()
        } else {
            showSnackBar(binding.root, "Loading chats...", Snackbar.LENGTH_INDEFINITE)
        }
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
//        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RC_SIGN_IN) {
            val response = IdpResponse.fromResultIntent(data)

            if (response == null) {
                showSnackBar(requireContext(), binding.root, R.string.sign_in_cancelled)
            } else {
                val error = response.error
                if (error == null) {
//                findNavController().navigate(R.id.fragment_chat_list)
                    showSnackBar(binding.root, "Sign in successful")
                } else {
                    if (error.errorCode == ErrorCodes.NO_NETWORK) {
                        showSnackBar(
                            requireContext(),
                            binding.root,
                            R.string.no_internet_connection
                        )
                    } else {
                        showSnackBar(requireContext(), binding.root, R.string.unknown_error)
                    }
                }
            }
        }
    }

    private fun requestSignIn() {
        startActivityForResult(
            AuthUI.getInstance().createSignInIntentBuilder()
                .setAvailableProviders(
                    arrayListOf(
                        AuthUI.IdpConfig.GoogleBuilder().build(),
                        AuthUI.IdpConfig.EmailBuilder().build(),
                        AuthUI.IdpConfig.PhoneBuilder().build(),
                        AuthUI.IdpConfig.AnonymousBuilder().build()
                    )
                ).setIsSmartLockEnabled(!BuildConfig.DEBUG, true).build(),
            RC_SIGN_IN
        )
    }

    companion object {
        private const val RC_SIGN_IN = 1234
        private val LOG_TAG = ChatListFragment::class.java.simpleName

        fun newInstance() = ChatListFragment()
    }

}
