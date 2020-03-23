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
import com.xently.holla.data.model.Contact
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class FCMService : FirebaseMessagingService() {
    private var viewModel: FCMViewModel? = null

    override fun onCreate() {
        super.onCreate()
        val app = application as App
        viewModel = FCMViewModelFactory(
            app.chatRepository,
            app.userRepository
        ).create(FCMViewModel::class.java)
    }

    override fun onDeletedMessages() {
        super.onDeletedMessages()
        Log.show(LOG_TAG, "Notification messages clean up...")
    }

    override fun onMessageReceived(p0: RemoteMessage) {
        // ViewModel is required to process received message(s). Prevent further actions if null
        val vm: FCMViewModel = viewModel ?: return

        val data = p0.data

        val notification = p0.notification
        val (title, body) = Pair(notification?.title, notification?.body)

        vm.run {
            viewModelScope.launch(Dispatchers.Main) {

                val senderKey = "sender"
                val isNewMessageSentToMe = data.containsKey(senderKey)
                val senderJson = data[senderKey]
                val contact = Contact.fromJson(senderJson ?: title) ?: return@launch

                if (isNewMessageSentToMe) {
                    val localContact = vm.getLocalContact(contact)
                    buildAndSendNotification(localContact.name ?: title, body ?: data["body"])
                } else {
                    val conversationContact = Contact.fromJson(data["conversationContact"])
                    vm.getConversations(conversationContact)
                }
            }
        }
    }

    override fun onMessageSent(p0: String) = Log.show(LOG_TAG, "FCM Message Sent: $p0")

    override fun onSendError(p0: String, p1: Exception) =
        Log.show(LOG_TAG, "FCM Message Send Error: $p0", p1, Log.Type.ERROR)

    override fun onNewToken(p0: String) {
        Log.show(LOG_TAG, "New token $p0")
        viewModel?.updateFCMToken(p0)
    }

    private fun buildAndSendNotification(title: String?, body: String?) {
        val mBuilder = buildNotification(title, body)

        sendNotification(mBuilder)
    }

    private fun buildNotification(title: String?, body: String?): NotificationCompat.Builder {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // Create the NotificationChannel, but only on API 26+ because
            // the NotificationChannel class is new and not in the support library
            val name = getString(R.string.channel_name)// user visible name
            // description that user sees in the system settings
            val desc = getString(R.string.channel_description)
            val channel = NotificationChannel(
                CHANNEL_ID,
                name,
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply { description = desc }
            // Register the channel with the system
            val notificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }

        return NotificationCompat.Builder(this, CHANNEL_ID)
            .apply {
                setContentTitle(title)
                setContentText(body)
//                setLargeIcon()
                setStyle(NotificationCompat.BigTextStyle().bigText(body))
                priority = NotificationCompat.PRIORITY_DEFAULT
                setAutoCancel(true)//remove notification when user taps on it
            }
    }

    private fun sendNotification(mBuilder: NotificationCompat.Builder) {
        NotificationManagerCompat.from(this)
            .apply { notify(System.currentTimeMillis().toInt(), mBuilder.build()) }
    }

    companion object {
        private val clazz = FCMService::class.java
        private val LOG_TAG = clazz.simpleName
        private val CHANNEL_ID = "${clazz.name}_CHANNEL_ID"
    }
}