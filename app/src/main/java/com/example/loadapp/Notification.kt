package com.example.loadapp

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat

private const val NOTIFICATION_ID = 0

fun NotificationManager.sendNotification(data: Map<String, Any>, applicationContext: Context) {
    val contentIntent = Intent(applicationContext, MainActivity::class.java)
    val contentPendingIntent = PendingIntent.getActivity(
        applicationContext,
        NOTIFICATION_ID,
        contentIntent,
        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
    )

    val detailIntent = Intent(applicationContext, DetailActivity::class.java)
    for ((key, value) in data) {
        when(value) {
            is String -> detailIntent.putExtra(key, value)
            is Boolean -> detailIntent.putExtra(key, value)
            else -> throw IllegalArgumentException("Unsupported type in the data")
        }
    }
    val detailPendingIntent = PendingIntent.getActivity(
        applicationContext,
        0,
        detailIntent,
        PendingIntent.FLAG_ONE_SHOT or PendingIntent.FLAG_IMMUTABLE
    )

    val builder = NotificationCompat.Builder(
        applicationContext,
        applicationContext.getString(R.string.notification_channel_id)
    )
        .setContentTitle(applicationContext.getString(R.string.app_name))
        .setContentText(data["name"].toString())
        .setContentIntent(contentPendingIntent)
        .setSmallIcon(R.drawable.ic_launcher_foreground)
        .setAutoCancel(true)
        .addAction(
            R.drawable.ic_launcher_foreground,
            applicationContext.getString(R.string.check_status),
            detailPendingIntent
        )
        .setPriority(NotificationCompat.PRIORITY_HIGH)

    notify(NOTIFICATION_ID, builder.build())
}