package com.example.gpsgeofenceapp.service

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofencingEvent
/**
 * BroadcastReceiver wywoływany przez system Android
 * w momencie zdarzenia geofence (ENTER / EXIT).
 */

class GeofenceReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {

        val event = GeofencingEvent.fromIntent(intent) ?: return
        if (event.hasError()) return

        val transition = event.geofenceTransition

        if (transition == Geofence.GEOFENCE_TRANSITION_ENTER ||
            transition == Geofence.GEOFENCE_TRANSITION_EXIT
        ) {
            val notificationService = NotificationService(context)

            event.triggeringGeofences?.forEach { geo ->
                val message =
                    if (transition == Geofence.GEOFENCE_TRANSITION_ENTER)
                        "Wszedłeś do obszaru: ${geo.requestId}"
                    else
                        "Opuściłeś obszar: ${geo.requestId}"

                notificationService.show(message)
            }
        }
    }
}