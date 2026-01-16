package com.example.gpsgeofenceapp.ui.viewmodel

import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import android.widget.Toast
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.gpsgeofenceapp.data.repository.GeofenceRepository
import com.example.gpsgeofenceapp.domain.GeofenceModel
import com.example.gpsgeofenceapp.service.GeofenceService
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
/**
 * ViewModel dla ekranu głównego (Dashboard).
 * Udostępnia strumień danych (Flow) z listą aktywnych obszarów.
 * Obsługuje logikę usuwania geofence, zapewniając spójność
 * między bazą danych a usługami lokalizacyjnymi Google.
 */

class DashboardViewModel(
    private val repository: GeofenceRepository,
    private val geofenceService: GeofenceService
) : ViewModel() {

    val geofences = repository.getGeofences()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), emptyList())

    fun delete(geo: GeofenceModel) {
        viewModelScope.launch {
            //Usuń z usług Google (zatrzymaj śledzenie)
            geofenceService.removeGeofence(geo.name)
            //Usuń z bazy danych
            repository.deleteGeofence(geo)
        }
    }

/**
 * FUNKCJA DEBUGUJĄCA (Wspomniana w raporcie - Sekcja 6)
 * Używana do diagnozy problemów z GPS na emulatorze.
 * Sprawdza, czy FusedLocationProvider w ogóle zwraca jakąkolwiek pozycję.
 */
    @SuppressLint("MissingPermission")
    fun testLocation(context: Context) {
        val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)

        fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
            if (location == null) {
                Toast.makeText(context, "BŁĄD: Brak lokalizacji (włącz GPS)", Toast.LENGTH_LONG).show()
                return@addOnSuccessListener
            }

            val currentLat = location.latitude
            val currentLng = location.longitude
            val currentGeoList = geofences.value

            if (currentGeoList.isEmpty()) {
                Toast.makeText(context, "Brak obszarów do sprawdzenia", Toast.LENGTH_SHORT).show()
                return@addOnSuccessListener
            }

            val sb = StringBuilder()
            sb.append("JA: $currentLat, $currentLng\n")

            var insideAny = false

            currentGeoList.forEach { geo ->
                val distance = FloatArray(1)
                Location.distanceBetween(
                    currentLat, currentLng,
                    geo.lat, geo.lng,
                    distance
                )
                val distInMeters = distance[0]

                if (distInMeters <= geo.radius) {
                    insideAny = true
                    sb.append("✅ JESTEŚ W: ${geo.name} (odl: ${distInMeters.toInt()}m)\n")
                } else {
                    sb.append("❌ POZA: ${geo.name} (odl: ${distInMeters.toInt()}m)\n")
                }
            }

            Toast.makeText(context, sb.toString(), Toast.LENGTH_LONG).show()
        }.addOnFailureListener {
            Toast.makeText(context, "Błąd pobierania lokalizacji: ${it.message}", Toast.LENGTH_SHORT).show()
        }
    }
}