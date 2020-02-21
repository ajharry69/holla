package com.xently.holla

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
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
import com.xently.holla.databinding.MainActivityBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: MainActivityBinding
    private lateinit var configuration: AppBarConfiguration
    private lateinit var controller: NavController

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

        if (FirebaseAuth.getInstance().currentUser == null) {
            requestSignIn()
        } else onSignInSuccess()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RC_SIGN_IN) {
            if (resultCode == Activity.RESULT_OK) {
                onSignInSuccess()
            } else {
                val response = IdpResponse.fromResultIntent(data)

                // Sign in failed
                if (response == null) {
                    // User pressed back button
//                    showSnackBar(this, binding.root, R.string.sign_in_cancelled)
                    finish()
                    return
                }

                if (response.error?.errorCode == ErrorCodes.NO_NETWORK) {
                    showSnackBar(this, binding.root, R.string.no_internet_connection)
                    return
                }

                showSnackBar(this, binding.root, R.string.unknown_error)
                Log.show(
                    LOG_TAG,
                    "Sign-in error: ${response.error?.message}",
                    response.error,
                    Log.Type.ERROR
                )
            }
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
            R.id.sign_out -> {
                AuthUI.getInstance().signOut(this).addOnCompleteListener(this) {
                    if (it.isSuccessful) requestSignIn()
                }
                true
            }
            else -> item.onNavDestinationSelected(controller) || super.onOptionsItemSelected(item)
        }
    }

    override fun onSupportNavigateUp(): Boolean =
        controller.navigateUp(configuration) || super.onSupportNavigateUp()

    private fun onSignInSuccess() {

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
        private val LOG_TAG = MainActivity::class.java.simpleName
    }
}
