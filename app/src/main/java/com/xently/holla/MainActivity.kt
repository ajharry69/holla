package com.xently.holla

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.onNavDestinationSelected
import androidx.navigation.ui.setupActionBarWithNavController
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.ErrorCodes
import com.firebase.ui.auth.IdpResponse
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUserMetadata
import com.xently.holla.Log.Type.ERROR
import com.xently.holla.databinding.MainActivityBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: MainActivityBinding
    private lateinit var configuration: AppBarConfiguration
    private lateinit var controller: NavController

    private val viewModel: MainActivityViewModel by viewModels {
        MainActivityViewModelFactory((applicationContext as App).userRepository)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.main_activity)

        setSupportActionBar(binding.toolbar)

        val navHostFragment: NavHostFragment = supportFragmentManager
            .findFragmentById(R.id.fragment_container) as NavHostFragment?
            ?: return

        configuration = AppBarConfiguration(setOf(R.id.fragment_chat_list))
        controller = navHostFragment.navController
        setupActionBarWithNavController(controller, configuration)

        with(viewModel) {
            setClient(FirebaseAuth.getInstance().currentUser)
            observableClient.observe(this@MainActivity, Observer {
                if (it == null) requestSignIn()
            })
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RC_SIGN_IN) {
            if (resultCode == Activity.RESULT_OK) onSignInSuccess()
            else onSignInFailed(data)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        return if (menu == null) {
            super.onCreateOptionsMenu(menu)
        } else {
            menuInflater.inflate(R.menu.main, menu)
            true
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.sign_out -> viewModel.signOut().isSuccessful
            else -> item.onNavDestinationSelected(controller) || super.onOptionsItemSelected(item)
        }
    }

    override fun onSupportNavigateUp(): Boolean =
        controller.navigateUp(configuration) || super.onSupportNavigateUp()

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
        if (metadata.creationTimestamp == metadata.lastSignInTimestamp) {
            // The user is new, show them a fancy intro screen!
            viewModel.addClient(currentUser).addOnCompleteListener(this) {
                if (!it.isSuccessful) showSnackBar(this, binding.root, R.string.sign_in_failed)
            }
        }
    }

    private fun onSignInFailed(data: Intent?) {
        val response = IdpResponse.fromResultIntent(data)

        when {
            response == null -> finish() // User pressed back button
            response.error?.errorCode == ErrorCodes.NO_NETWORK -> {
                showSnackBar(this, binding.root, R.string.no_internet_connection)
            }
            else -> {
                showSnackBar(this, binding.root, R.string.unknown_error)
                Log.show(
                    LOG_TAG,
                    "Sign-in error: ${response.error?.message}",
                    response.error,
                    ERROR
                )
            }
        }
    }

    companion object {
        private const val RC_SIGN_IN = 1234
        private val LOG_TAG = MainActivity::class.java.simpleName
    }
}
