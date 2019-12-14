package com.example.a1440.service

import android.content.Intent

internal interface NotificationUsecase {
    suspend fun send(
        type: NotificationType = NotificationType.SIMPLE,
        title: String,
        content: String,
        id: String = DEFAULT_ID,
        groupId: String = DEFAULT_GROUP_ID,
        groupName: String = DEFAULT_GROUP_NAME,
        intent: Intent? = null
    )

    companion object {
        const val DEFAULT_ID = "title"
        const val DEFAULT_GROUP_ID = "group_id"
        const val DEFAULT_GROUP_NAME = "group_name"
    }
}