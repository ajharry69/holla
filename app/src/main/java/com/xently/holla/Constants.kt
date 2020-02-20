package com.xently.holla

import android.content.Context
import android.util.Log
import android.view.View
import androidx.annotation.StringRes
import androidx.core.content.ContextCompat
import com.google.android.material.snackbar.Snackbar
import com.xently.holla.Log.Type.*

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
 * Shows [Snackbar] for given `duration`
 * @param context
 * @param view
 * @param duration: @see Snackbar.getDuration
 * @param message: [Snackbar] message
 * @param actionButtonClick: Callback for responding to [Snackbar] action button click
 * @param actionButtonText: Label text shown on [Snackbar]s action button
 */
fun showSnackBar(
    view: View,
    message: String,
    duration: Int = Snackbar.LENGTH_SHORT, actionButtonText: String? = null,
    actionButtonClick: ((snackBar: Snackbar) -> Unit)? = null
) {
    val snackbar = Snackbar.make(view, message, duration)
    with(snackbar) {
        setActionTextColor(ContextCompat.getColor(context, R.color.colorAccent))
        if (actionButtonText != null) setAction(actionButtonText) {
            actionButtonClick?.invoke(this)
        }
        if (!this.isShownOrQueued) show()
    }
}

fun showSnackBar(
    context: Context,
    view: View,
    @StringRes message: Int,
    duration: Int = Snackbar.LENGTH_SHORT, actionButtonText: String? = null,
    actionButtonClick: ((snackBar: Snackbar) -> Unit)? = null
) {
    showSnackBar(view, context.getString(message), duration, actionButtonText, actionButtonClick)
}