package com.aviatrac.softoclub.trgjo.presentation.notificiation

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.os.bundleOf
import com.aviatrac.softoclub.AviaTrackActivity
import com.aviatrac.softoclub.R
import com.aviatrac.softoclub.trgjo.presentation.app.AviaTrackApplication
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

private const val AVIA_TRACK_CHANNEL_ID = "avia_track_notifications"
private const val AVIA_TRACK_CHANNEL_NAME = "AviaTrack Notifications"
private const val AVIA_TRACK_NOT_TAG = "AviaTrack"

class AviaTrackPushService : FirebaseMessagingService(){
    override fun onNewToken(token: String) {
        super.onNewToken(token)
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)

        // Обработка notification payload
        remoteMessage.notification?.let {
            if (remoteMessage.data.contains("url")) {
                aviaTrackShowNotification(it.title ?: AVIA_TRACK_NOT_TAG, it.body ?: "", data = remoteMessage.data["url"])
            } else {
                aviaTrackShowNotification(it.title ?: AVIA_TRACK_NOT_TAG, it.body ?: "", data = null)
            }
        }

        // Обработка data payload
        if (remoteMessage.data.isNotEmpty()) {
            aviaTrackHandleDataPayload(remoteMessage.data)
        }
    }

    private fun aviaTrackShowNotification(title: String, message: String, data: String?) {
        val aviaTrackNotificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // Создаем канал уведомлений для Android 8+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                AVIA_TRACK_CHANNEL_ID,
                AVIA_TRACK_CHANNEL_NAME,
                NotificationManager.IMPORTANCE_HIGH
            )
            aviaTrackNotificationManager.createNotificationChannel(channel)
        }

        val aviaTrackIntent = Intent(this, AviaTrackActivity::class.java).apply {
            putExtras(bundleOf(
                "url" to data
            ))
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
        val aviaTrackPendingIntent = PendingIntent.getActivity(
            this,
            0,
            aviaTrackIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val aviaTrackNotification = NotificationCompat.Builder(this, AVIA_TRACK_CHANNEL_ID)
            .setContentTitle(title)
            .setContentText(message)
            .setSmallIcon(R.drawable.avia_track_noti_icon)
            .setAutoCancel(true)
            .setContentIntent(aviaTrackPendingIntent)
            .build()

        aviaTrackNotificationManager.notify(System.currentTimeMillis().toInt(), aviaTrackNotification)
    }

    private fun aviaTrackHandleDataPayload(data: Map<String, String>) {
        data.forEach { (key, value) ->
            Log.d(AviaTrackApplication.AVIA_TRACK_MAIN_TAG, "Data key=$key value=$value")
        }
    }
}