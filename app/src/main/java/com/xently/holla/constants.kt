package com.xently.holla

import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.xently.holla.Log.Type.*

object FBCollection {
    const val USERS = "users"
    const val CHATS = "chats"
    const val MESSAGES = "messages"
    const val CONVERSATIONS = "conversations"
    const val UNREAD_COUNT = "unreadCount"
}

object Log {
    enum class Type {
        ASSERT,
        DEBUG,
        INFO,
        WARNING,
        ERROR,
        VERBOSE
    }

    /**
     * Shows debug (Log.d(...)) if build type is DEBUG
     * @param tag: Log TAG
     * @param message: Log message
     * @param throwable: Exception to accompany the log
     */
    fun show(
        tag: String,
        message: String?,
        throwable: Throwable? = null,
        type: Type = DEBUG
    ) {
        if (BuildConfig.DEBUG && message != null) {
            when (type) {
                DEBUG -> {
                    if (throwable == null) {
                        Log.d(tag, message)
                        return
                    }
                    Log.d(tag, message, throwable)
                }
                INFO -> {
                    if (throwable == null) {
                        Log.i(tag, message)
                        return
                    }
                    Log.i(tag, message, throwable)
                }
                WARNING -> {
                    if (throwable == null) {
                        Log.w(tag, message)
                        return
                    }
                    Log.w(tag, message, throwable)
                }
                ERROR -> {
                    if (throwable == null) {
                        Log.e(tag, message)
                        return
                    }
                    Log.e(tag, message, throwable)
                }
                VERBOSE -> {
                    if (throwable == null) {
                        Log.v(tag, message)
                        return
                    }
                    Log.v(tag, message, throwable)
                }
                ASSERT -> {
                    if (throwable == null) {
                        Log.wtf(tag, message)
                        return
                    }
                    Log.wtf(tag, message, throwable)
                }
            }
        }
    }

    /**
     * @see show
     */
    fun show(tag: String, message: Any?, throwable: Throwable? = null, type: Type = DEBUG) {
        show(tag, "$message", throwable, type)
    }
}

/**
 * @param permission permission requested for. Input should be from Manifest permission constants
 * **`Manifest.permission.CAMERA`** or **`Manifest.permission_group.STORAGE`**
 * @param requestCode it helps in properly responding to specific permissions as dictated by
 * [onRequestPermissionsResult]
 * @param onPermissionGranted (Execute on Permission [permission] Granted/Available) - what to
 * do when the [permission] is granted by the user
 * @param onRationaleNeeded what to do when the [permission] request justification is needed for
 * the user to understand reason for permission request. Default behaviour is to show an alert
 * dialog explaining why the permission is required
 */
@Suppress("KDocUnresolvedReference")
fun <T> Fragment.requestFeaturePermission(
    permission: String,
    requestCode: Int,
    onPermissionGranted: (() -> T)? = null,
    onRationaleNeeded: (() -> Unit)? = null
) {
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) return

    // Check if Camera permission is already granted or available
    if (ContextCompat.checkSelfPermission(
            this.requireContext(),
            permission
        ) == PackageManager.PERMISSION_GRANTED
    ) {
        // Camera permission is already available. Show the preview
        onPermissionGranted?.invoke()
    } else {
        // Permission not available
        // Provide additional rationale to the user if the permission was not granted and the
        // user will benefit from the additional activity of the use of the permission

        if (shouldShowRequestPermissionRationale(permission)) {
            // Inform user of why you need the permission
            onRationaleNeeded?.invoke()
            // Proceed to request permission again
        }

        // Request permission
        requestPermissions(arrayOf(permission), requestCode)
    }
}