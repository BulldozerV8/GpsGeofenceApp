package com.example.gpsgeofenceapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.lifecycle.lifecycleScope
import androidx.room.Room
import com.example.gpsgeofenceapp.data.local.AppDatabase
import com.example.gpsgeofenceapp.data.repository.GeofenceRepository
import com.example.gpsgeofenceapp.service.GeofenceService
import com.example.gpsgeofenceapp.ui.navigation.AppNav
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

/**
 * Główny punkt wejścia do aplikacji (Entry Point).
 * Odpowiada za:
 * - inicjalizację singletonów (Baza danych, Repozytorium, Serwisy)
 * - konfigurację cyklu życia aplikacji (Lifecycle)
 * - uruchomienie głównego widoku (Compose Content).
 */
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val database = Room.databaseBuilder(
            applicationContext, AppDatabase::class.java, "app_db"
        ).build()
        val repository = GeofenceRepository(database.geofenceDao(), database.logDao())
        val geofenceService = GeofenceService(applicationContext)

        lifecycleScope.launch {
            val list = repository.getGeofences().first()
            if (list.isNotEmpty()) {
                list.forEach { geo ->
                    if (geo.isActive) {
                        geofenceService.registerGeofence(
                            geo.name, geo.lat, geo.lng, geo.radius
                        )
                    }
                }
            }
        }

        setContent {
            RequestPermissionsEffect()
            AppNav(repository, geofenceService)
        }
    }
}