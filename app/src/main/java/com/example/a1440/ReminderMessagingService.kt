package com.example.a1440

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class ReminderMessagingService : FirebaseMessagingService() {

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        remoteMessage.data.also {
            val title = it["title"]
            val message = it["message"]

            val builder = if (Build.VERSION_CODES.O <= Build.VERSION.SDK_INT) {
                NotificationCompat.Builder(this, CHANNEL_ID)
            } else {
                NotificationCompat.Builder(this)
            }

            val notification = builder.setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(title)
                .setContentText(message)
                .build()

            val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            manager.notify(0, notification)
        }
    }

    override fun onNewToken(token: String) {
        Log.d("ushi", "token: $token")

        val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager?
        if (Build.VERSION_CODES.O <= Build.VERSION.SDK_INT) {
            val channel = manager?.getNotificationChannel(CHANNEL_ID) ?: NotificationChannel(
                CHANNEL_ID, "Its channel", NotificationManager.IMPORTANCE_HIGH
            )
            manager?.createNotificationChannel(channel)
        }
    }


    companion object {
        private const val CHANNEL_ID = "CHANNEL_ID"
    }
}