package com.example.a1440.broadcast

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.example.a1440.notification.NotificationImpl
import com.example.a1440.notification.NotificationType
import kotlinx.coroutines.runBlocking

class RemindBroadcastReceiver: BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        val receiveContext = context ?: return
        if (intent?.action != ACTION_TAG) return
        val minutes = intent.getIntExtra("minutes", 0)
        val notification = NotificationImpl(receiveContext)
        runBlocking {
            notification.send(
                NotificationType.SIMPLE,
                "1440App",
                "残り${minutes}分です 生産性高く働きましょう！",
                "id"
            )
        }
    }

    companion object {
        const val ACTION_TAG = "BROADCAST_FROM_1440_APP"
    }
}