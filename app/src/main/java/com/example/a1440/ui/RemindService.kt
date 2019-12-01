package com.example.a1440.ui

import android.app.IntentService
import android.content.Intent
import com.example.a1440.ui.service.NotificationImpl
import com.example.a1440.ui.service.NotificationType
import kotlinx.coroutines.runBlocking

class RemindService : IntentService("") {
    override fun onHandleIntent(intent: Intent?) {
        val minutes = intent?.getIntExtra("minutes", 0) ?: return
        val notification = NotificationImpl(this)
        runBlocking {
            notification.send(
                NotificationType.SIMPLE,
                "1440App",
                "残り${minutes}分です 生産性高く働きましょう！",
                "id"
            )
        }
    }
}