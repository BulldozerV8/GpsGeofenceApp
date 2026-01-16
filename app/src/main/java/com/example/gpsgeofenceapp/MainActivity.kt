package com.example.gpsgeofenceapp

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.core.app.ActivityCompat
import androidx.lifecycle.lifecycleScope
import androidx.room.Room
import com.example.gpsgeofenceapp.data.local.AppDatabase
import com.example.gpsgeofenceapp.data.repository.GeofenceRepository
import com.example.gpsgeofenceapp.service.GeofenceService
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

/**
 * Główna aktywność aplikacji.
 *
 * Odpowiada wyłącznie za:
 * - runtime permissions (lokalizacja + powiadomienia)
 * - inicjalizację bazy danych
 * - ponowną rejestrację zapisanych geofence
 * - uruchomienie UI
 */
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        requestLocationPermissions()
        requestNotificationPermission()

        val database = Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java,
            "app_db"
        ).build()

        val repository = GeofenceRepository(database.geofenceDao())
        val geofenceService = GeofenceService(applicationContext)

        lifecycleScope.launch {
            repository.getGeofences().first().forEach { geo ->
                geofenceService.registerGeofence(
                    id = geo.name,
                    lat = geo.lat,
                    lng = geo.lng,
                    radius = geo.radius
                )
            }
        }

        setContent {
            AppNav(
                repository = repository,
                geofenceService = geofenceService
            )
        }
    }

    private fun requestLocationPermissions() {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                1000
            )
            return
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q &&
            ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_BACKGROUND_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_BACKGROUND_LOCATION),
                1001
            )
        }
    }

    private fun requestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
            ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                2000
            )
        }
    }
}
