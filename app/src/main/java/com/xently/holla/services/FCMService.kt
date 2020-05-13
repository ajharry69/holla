package com.xently.holla.services

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.lifecycle.viewModelScope
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.xently.holla.App
import com.xently.holla.Log
import com.xently.holla.R
import com.xently.holla.data.Source
import com.xently.holla.data.model.Contact
import com.xently.holla.data.model.Notification
import com.xently.holla.data.model.ObjectOperation
import com.xently.holla.data.model.ObjectOperation.Operation.DELETE
import com.xently.holla.data.model.ObjectOperation.Operation.UPDATE
import com.xently.holla.data.model.ObjectOperation.Type.*
import com.xently.holla.viewmodels.BaseViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class FCMService : FirebaseMessagingService() {
    private var viewModel: FCMViewModel? = null

    override fun onCreate() {
        super.onCreate()
        val app = application as App
        viewModel = FCMViewModelFactory(
            app.conversationRepository,
            app.chatRepository,
            app.userRepository
        ).create(FCMViewModel::class.java)
    }

    override fun onDeletedMessages() {
        super.onDeletedMessages()
        Log.show(LOG_TAG, "Notification messages clean up...")
    }

    override fun onMessageReceived(p0: RemoteMessage) {
        val data = p0.data

        viewModel?.run {
            try {
                // New message notification was received
                Notification.fromMap(data)?.run {
                    if (!senderId.isNullOrBlank() && !messageId.isNullOrBlank())
                        getMessage(senderId, messageId)
                    if (!title.isNullOrBlank() || !body.isNullOrBlank()) {
                        viewModelScope.launch(Dispatchers.Main) {
                            val contact = getLocalContact(Contact(mobileNumber = title))
                            buildAndSendNotification(
                                contact.name ?: title,
                                body,
                                imageUrl,
                                viewModel
                            )
                        }
                    }
                }
            } catch (ex: Exception) {
                Log.show(LOG_TAG, ex.message, ex, Log.Type.ERROR)
            }

            try {
                // Data update notification was received
                ObjectOperation.fromMap(data)?.run {
                    if (id.isNullOrBlank()) return
                    when (objectType) {
                        CONVERSATION -> {
                            when (operation) {
                                UPDATE -> getConversation(id)
                                DELETE -> {
                                    viewModelScope.launch(Dispatchers.IO) {
                                        deleteConversation(id, Source.LOCAL)
                                        deleteMessages(id, Source.LOCAL)
                                    }
                                }
                                else -> Unit
                            }
                        }
                        MESSAGE -> Unit
                        CONTACT -> Unit
                        else -> Unit
                    }
                }
            } catch (ex: Exception) {
                Log.show(LOG_TAG, ex.message, ex, Log.Type.ERROR)
            }
        }
    }

    override fun onMessageSent(p0: String) = Log.show(LOG_TAG, "FCM Chat Sent: $p0")

    override fun onSendError(p0: String, p1: Exception) =
        Log.show(LOG_TAG, "FCM Chat Send Error: $p0", p1, Log.Type.ERROR)

    override fun onNewToken(p0: String) {
        Log.show(LOG_TAG, "New token $p0")
        viewModel?.updateFCMToken(p0)
    }

    private suspend fun buildAndSendNotification(
        title: String?, body: String?,
        imageUrl: String?,
        viewModel: BaseViewModel?
    ) {
        val mBuilder = buildNotification(title, body, imageUrl, viewModel)

        mBuilder.sendNotification()
    }

    private suspend fun buildNotification(
        title: String?,
        body: String?,
        imageUrl: String?,
        viewModel: BaseViewModel?
    ): NotificationCompat.Builder {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                getString(R.string.channel_name), // user visible name
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                // description that user sees in the system settings
                description = getString(R.string.channel_description)
            }
            // Register the channel with the system
            (getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager).run {
                createNotificationChannel(channel)
            }
        }

        return NotificationCompat.Builder(this, CHANNEL_ID).apply {
            setContentTitle(title)
            setContentText(body)
            setSmallIcon(R.drawable.ic_chat_notification)
            if (!imageUrl.isNullOrBlank()) {
                viewModel?.also { vm ->
                    setLargeIcon(vm.getBitmap(this@FCMService, imageUrl))
                }
            }
            setStyle(NotificationCompat.BigTextStyle().bigText(body))
            priority = NotificationCompat.PRIORITY_DEFAULT
            setAutoCancel(true)//remove notification when user taps on it
        }
    }

    private fun NotificationCompat.Builder.sendNotification() {
        NotificationManagerCompat.from(this@FCMService).apply {
            notify(System.currentTimeMillis().toInt(), build())
        }
    }

    companion object {
        private val clazz = FCMService::class.java
        private val LOG_TAG = clazz.simpleName
        private val CHANNEL_ID = "${clazz.name}_CHANNEL_ID"
    }
}