package com.example.worker

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.content.getSystemService
import androidx.work.ForegroundInfo

const val SyncNotificationId = 0
const val SyncNotificationChannelID = "Sync Notification Channel"

fun Context.syncForegroundInfo() = ForegroundInfo(
    SyncNotificationId,
    syncWorkerNotification()
)

fun Context.syncWorkerNotification(): Notification {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        val channel = NotificationChannel(
            SyncNotificationChannelID,
            SyncNotificationChannelID,
            NotificationManager.IMPORTANCE_DEFAULT
        )

        val notificationManager: NotificationManager? = getSystemService()
        notificationManager?.createNotificationChannel(channel)
    }

    return NotificationCompat.Builder(this, SyncNotificationChannelID)
        .setContentTitle("Syncing data")
        .setSmallIcon(R.drawable.sync_foreground)
        .build()
}