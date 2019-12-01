package com.example.a1440.ui

import android.app.IntentService
import android.content.Intent
import com.example.a1440.ui.service.NotificationImpl
import com.example.a1440.ui.service.NotificationType
import kotlinx.coroutines.runBlocking

class RemindService : IntentService("") {
    override fun onHandleIntent(intent: Intent?) {
        val notification = NotificationImpl(this)
        runBlocking {
            notification.send(
                NotificationType.SIMPLE,
                "Title",
                "Its me",
                "id"
            )
        }
    }
}