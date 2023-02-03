package com.github.bkmbigo.textclassification.utils

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import com.github.bkmbigo.textclassification.R

enum class MyNotificationChannel(
    val channelId: String,
    val channelName: Int,
    val channelDescription: Int
) {
    NOTIFICATION_LISTENER_CHANNEL(
        "text_classifier_notification_listener",
        R.string.notification_listener_channel_name,
        R.string.notification_listener_channel_desc
    )
}

fun Context.createNotificationChannel(myNotificationChannel: MyNotificationChannel) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        val name = getString(myNotificationChannel.channelName)
        val descriptionText = getString(myNotificationChannel.channelDescription)
        val importance = NotificationManager.IMPORTANCE_LOW
        val channel = NotificationChannel(myNotificationChannel.channelId, name, importance).apply {
            description = descriptionText
        }
        (getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager).createNotificationChannel(
            channel
        )
    }
}