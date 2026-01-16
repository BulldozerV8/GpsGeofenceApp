package com.example.gpsgeofenceapp.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import kotlin.random.Random
/**
 * Prosta warstwa odpowiedzialna za wyświetlanie powiadomień.
 * Tworzy kanał notyfikacji
 * i wyświetla komunikaty o zdarzeniach geofence.
 */

class NotificationService(private val context: Context) {

    private val channelId = "geofence_channel"

    fun show(text: String) {
        val manager =
            context.getSystemService(Context.NOTIFICATION_SERVICE)
                    as NotificationManager

        if (Build.VERSION.SDK_INT >= 26) {
            val channel = NotificationChannel(
                channelId,
                "Geofence alerts",
                NotificationManager.IMPORTANCE_HIGH
            )
            manager.createNotificationChannel(channel)
        }

        val notification = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(android.R.drawable.ic_dialog_map)
            .setContentTitle("Geofence")
            .setContentText(text)
            .setAutoCancel(true)
            .build()

        manager.notify(Random.nextInt(), notification)
    }
}
