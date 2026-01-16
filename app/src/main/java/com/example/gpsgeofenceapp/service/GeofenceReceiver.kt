package com.example.gpsgeofenceapp.service

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.media.AudioManager // WAŻNY IMPORT
import android.util.Log
import androidx.room.Room
import com.example.gpsgeofenceapp.data.local.AppDatabase
import com.example.gpsgeofenceapp.data.local.GeofenceEntity
import com.example.gpsgeofenceapp.data.local.GeofenceLogEntity
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofencingEvent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Odbiornik rozgłoszeniowy (BroadcastReceiver) uruchamiany przez system Android,
 * gdy użytkownik przekroczy granicę geofence.
 * Odpowiada za:
 * - interpretację zdarzenia (Wejście/Wyjście)
 * - sprawdzenie warunków biznesowych (czy jest odpowiedni dzień/godzina w bazie)
 * - wykonanie akcji: powiadomienie, log do bazy, wyciszenie telefonu.
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

            val db = Room.databaseBuilder(
                context.applicationContext,
                AppDatabase::class.java,
                "app_db"
            ).build()

            val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
            val dateStr = sdf.format(Date())

            val scope = CoroutineScope(Dispatchers.IO)

            scope.launch {
                event.triggeringGeofences?.forEach { geo ->
                    val allGeofences = db.geofenceDao().getAllList()
                    val currentGeo = allGeofences.find { it.name == geo.requestId }

                    if (currentGeo != null) {

                        val isTimeOk = isCurrentTimeInBond(currentGeo.startTime, currentGeo.endTime)
                        val isDayOk = checkDayCondition(currentGeo)

                        if (isTimeOk && isDayOk) {
                            val typeStr =
                                if (transition == Geofence.GEOFENCE_TRANSITION_ENTER) "WEJŚCIE" else "WYJŚCIE"
                            val message = "$typeStr: ${geo.requestId}"

                            if (currentGeo.shouldMute) {
                                handleMuteLogic(context, transition)
                            }

                            notificationService.show(message)
                            db.logDao().insert(GeofenceLogEntity(date = dateStr, message = message))
                        } else {
                            val reason = if (!isTimeOk) "zły czas" else "zły dzień"
                            db.logDao().insert(
                                GeofenceLogEntity(
                                    date = dateStr,
                                    message = "ZIGNOROWANO ($reason): ${geo.requestId}"
                                )
                            )
                        }
                    }
                }
            }
        }
    }

    private fun handleMuteLogic(context: Context, transition: Int) {
        val audioManager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager

        try {
            if (transition == Geofence.GEOFENCE_TRANSITION_ENTER) {
                audioManager.ringerMode = AudioManager.RINGER_MODE_VIBRATE
            } else if (transition == Geofence.GEOFENCE_TRANSITION_EXIT) {
                audioManager.ringerMode = AudioManager.RINGER_MODE_NORMAL
            }
        } catch (e: SecurityException) {
            Log.e("GeofenceReceiver", "Brak uprawnień do zmiany trybu dźwięku: ${e.message}")
        } catch (e: Exception) {
            Log.e("GeofenceReceiver", "Błąd audio: ${e.message}")
        }
    }

    private fun isCurrentTimeInBond(start: String, end: String): Boolean {
        return try {
            val sdf = SimpleDateFormat("HH:mm", Locale.getDefault())
            val now = sdf.parse(sdf.format(Date()))
            val dStart = sdf.parse(start)
            val dEnd = sdf.parse(end)
            if (now != null && dStart != null && dEnd != null) {
                !now.before(dStart) && !now.after(dEnd)
            } else {
                true
            }
        } catch (e: Exception) {
            true
        }
    }

    private fun checkDayCondition(geo: GeofenceEntity): Boolean {
        if (geo.repeatDays.isNotEmpty()) {
            val calendar = java.util.Calendar.getInstance()
            val todayDayOfWeek = calendar.get(java.util.Calendar.DAY_OF_WEEK)
            val activeDays = geo.repeatDays.split(",").mapNotNull { it.toIntOrNull() }
            return activeDays.contains(todayDayOfWeek)
        }
        else if (geo.specificDate.isNotEmpty()) {
            val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val todayStr = sdf.format(Date())
            return todayStr == geo.specificDate
        }

        return true
    }
}