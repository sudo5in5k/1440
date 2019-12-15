package com.example.a1440.notification

import android.app.NotificationChannel
import android.app.NotificationChannelGroup
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.SystemClock
import androidx.core.app.NotificationCompat
import com.example.a1440.R

internal class NotificationImpl(private val context: Context) : NotificationUsecase {

    override suspend fun send(
        type: NotificationType,
        title: String,
        content: String,
        id: String,
        groupId: String,
        groupName: String,
        intent: Intent?
    ) {
        val notificationManager =
            context.getSystemService(NotificationManager::class.java) ?: return

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val group = NotificationChannelGroup(groupId, groupName)
            notificationManager.createNotificationChannelGroup(group)

            val channel = notificationManager.getNotificationChannel(id) ?: NotificationChannel(
                id,
                "name",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            notificationManager.createNotificationChannel(channel)
        }

        val notification = NotificationCompat.Builder(context, id)
            .setContentText(content)
            .setContentTitle(title)
            .setSmallIcon(R.drawable.ic_access_time_black_24dp)
            .build()

        notificationManager.notify(SystemClock.uptimeMillis().toInt(), notification)
    }
}