package de.skg_botnang.skg_app

// https://firebase.google.com/docs/cloud-messaging/android/client?hl=en

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.NotificationManager.IMPORTANCE_DEFAULT
import android.app.PendingIntent
import android.app.PendingIntent.FLAG_IMMUTABLE
import android.content.Intent
import android.content.Intent.FLAG_ACTIVITY_CLEAR_TOP
import android.util.Log
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.random.Random

class SKGMessagingService : FirebaseMessagingService() {

    private val random = Random

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        remoteMessage.notification?.let { message ->
            sendNotification(message)
        }

        val fcmMessage = FCMMessage(
            title = remoteMessage.notification?.title ?: "",
            body = remoteMessage.notification?.body ?: ""
        )
        storeMessageLocally(fcmMessage)

    }

    private fun storeMessageLocally(message: FCMMessage) {
        val database = FCMDatabase.getDatabase(applicationContext)
        val messageDao = database.messageDao()

        CoroutineScope(Dispatchers.IO).launch {
            val rowID: Long = messageDao.insertMessage(message)
            Log.d(TAG, "message.id = ${rowID}")
            notifyActivity(rowID)
        }
    }

    private fun notifyActivity(id: Long) {
        sendBroadcast(Intent("NOTIFY_FCM").apply {
            putExtra("rowID", id)
        })
    }

    private fun sendNotification(message: RemoteMessage.Notification) {
        // If you want the notifications to appear when your app is in foreground

        val intent = Intent(this, MainActivity::class.java).apply {
            addFlags(FLAG_ACTIVITY_CLEAR_TOP)
        }

        val pendingIntent = PendingIntent.getActivity(
            this, 0, intent, FLAG_IMMUTABLE
        )

        val channelId = this.getString(R.string.default_notification_channel_id)

        val notificationBuilder = NotificationCompat.Builder(this, channelId)
            .setContentTitle(message.title)
            .setContentText(message.body)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)

        val manager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager

        val channel = NotificationChannel(channelId, CHANNEL_NAME, IMPORTANCE_DEFAULT)
        manager.createNotificationChannel(channel)

        manager.notify(random.nextInt(), notificationBuilder.build())
    }

    override fun onNewToken(token: String) {
        // If you want to send messages to this application instance or
        // manage this apps subscriptions on the server side, send the
        // FCM registration token to your app server.
        Log.d(TAG,"New token: $token")
    }

    companion object {
        const val CHANNEL_NAME = "FCM notification channel"
        const val TAG = "SKG-App"
    }
}