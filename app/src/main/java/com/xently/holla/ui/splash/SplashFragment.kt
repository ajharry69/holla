package com.xently.holla.ui.splash

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.viewModelScope
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.ErrorCodes
import com.firebase.ui.auth.IdpResponse
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUserMetadata
import com.xently.holla.App
import com.xently.holla.BuildConfig
import com.xently.holla.Log
import com.xently.holla.R
import com.xently.holla.data.Result
import com.xently.holla.databinding.SplashFragmentBinding
import com.xently.holla.utils.Type
import com.xently.xui.Fragment
import com.xently.xui.utils.ui.fragment.showSnackBar
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class SplashFragment : Fragment() {
    private val hideHandler = Handler()

    private val hidePart2Runnable = Runnable {
        // Delayed removal of status and navigation bar

        // Note that some of these constants are new as of API 16 (Jelly Bean)
        // and API 19 (KitKat). It is safe to use them, as they are inlined
        // at compile-time and do nothing on earlier devices.
        val flags = View.SYSTEM_UI_FLAG_LOW_PROFILE or
                View.SYSTEM_UI_FLAG_FULLSCREEN or
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE or
                View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY or
                View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION or
                View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
        activity?.window?.decorView?.systemUiVisibility = flags
        (activity as? AppCompatActivity)?.supportActionBar?.hide()
    }
    private val hideRunnable = Runnable { hide() }

    private var _binding: SplashFragmentBinding? = null
    private val binding: SplashFragmentBinding
        get() = _binding!!

    private val viewModel: SplashViewModel by viewModels {
        val app = requireContext().applicationContext as App
        SplashViewModelFactory(app.userRepository)
    }

    override val showToolbar: Boolean = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = SplashFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        viewModel.observableContact.observe(viewLifecycleOwner, Observer {
            if (it == null) requestSignIn()
        })
    }

    override fun onResume() {
        super.onResume()
        activity?.window?.addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)

        // Trigger the initial hide() shortly after the activity has been
        // created, to briefly hint to the user that UI controls
        // are available.
        delayedHide()
    }

    override fun onPause() {
        super.onPause()
        activity?.window?.clearFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)

        // Clear the systemUiVisibility flag
        activity?.window?.decorView?.systemUiVisibility = 0
        show()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == RC_SIGN_IN) {
            if (resultCode == Activity.RESULT_OK) onSignInSuccess()
            else onSignInFailed(data)
        }
    }

    private fun requestSignIn() {
        startActivityForResult(
            AuthUI.getInstance().createSignInIntentBuilder()
                .setAvailableProviders(arrayListOf(AuthUI.IdpConfig.PhoneBuilder().build()))
                .setIsSmartLockEnabled(!BuildConfig.DEBUG, true).build(),
            RC_SIGN_IN
        )
    }

    private fun onSignInSuccess() {
        val currentUser = FirebaseAuth.getInstance().currentUser ?: return
        val metadata: FirebaseUserMetadata = currentUser.metadata ?: return
        viewModel.viewModelScope.launch(Dispatchers.Main) {
            if (metadata.creationTimestamp == metadata.lastSignInTimestamp) {
                // The user is new, show them a fancy intro screen!
                viewModel.saveContact().also {
                    when (it) {
                        is Result.Success -> show() // TODO: Navigate to home screen...
                        is Result.Error ->
                            showSnackBar(R.string.sign_in_failed)
                        Result.Loading -> Unit
                    }
                }
            } else viewModel.saveContact(Type.UPDATE)
        }
    }

    private fun onSignInFailed(data: Intent?) {
        val response = IdpResponse.fromResultIntent(data)

        when {
            response == null -> onBackPressed() // User pressed back button
            response.error?.errorCode == ErrorCodes.NO_NETWORK -> {
                showSnackBar(R.string.no_internet_connection)
            }
            else -> {
                showSnackBar(R.string.unknown_error)
                Log.show(
                    LOG_TAG,
                    "Sign-in error: ${response.error?.message}",
                    response.error,
                    Log.Type.ERROR
                )
            }
        }
    }

    private fun hide() {
        // Schedule a runnable to remove the status and navigation bar after a delay
        hideHandler.postDelayed(hidePart2Runnable, UI_ANIMATION_DELAY.toLong())
    }

    private fun show() {
        // Show the system bar
        binding.root.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or
                View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION

        // Schedule a runnable to display UI elements after a delay
        hideHandler.removeCallbacks(hidePart2Runnable)
        // Always hide toolbar
//        (activity as? AppCompatActivity)?.supportActionBar?.show()
    }

    /**
     * Schedules a call to hide() in [delayMillis], canceling any
     * previously scheduled calls.
     */
    private fun delayedHide(delayMillis: Int = 50) {
        hideHandler.removeCallbacks(hideRunnable)
        hideHandler.postDelayed(hideRunnable, delayMillis.toLong())
    }

    companion object {
        /**
         * Whether or not the system UI should be auto-hidden after
         * [AUTO_HIDE_DELAY_MILLIS] milliseconds.
         */
        private const val AUTO_HIDE = true

        /**
         * If [AUTO_HIDE] is set, the number of milliseconds to wait after
         * user interaction before hiding the system UI.
         */
        private const val AUTO_HIDE_DELAY_MILLIS = 3000

        /**
         * Some older devices needs a small delay between UI widget updates
         * and a change of the status and navigation bar.
         */
        private const val UI_ANIMATION_DELAY = 300

        private const val RC_SIGN_IN = 1234
        private val LOG_TAG = SplashFragment::class.java.simpleName
    }
}
