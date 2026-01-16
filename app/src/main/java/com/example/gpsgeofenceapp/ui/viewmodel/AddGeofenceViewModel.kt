package com.example.gpsgeofenceapp.ui.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.gpsgeofenceapp.data.repository.GeofenceRepository
import com.example.gpsgeofenceapp.domain.GeofenceModel
import com.example.gpsgeofenceapp.service.GeofenceService
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
/**
 * ViewModel formularza dodawania geofence.
 * Przechowuje stan UI (punkt, promień, nazwa)
 * oraz zapisuje nowy geofence:
 * - do bazy danych
 * - do systemu Android (aktywacja geofence)
 */

class AddGeofenceViewModel(
    private val repository: GeofenceRepository,
    private val geofenceService: GeofenceService
) : ViewModel() {

    var selectedLatLng by mutableStateOf<LatLng?>(null)
    var radius by mutableStateOf(100f)
    var name by mutableStateOf("")

    fun save() {
        val point = selectedLatLng ?: return

        viewModelScope.launch {
            repository.addGeofence(
                GeofenceModel(
                    id = 0,
                    name = name,
                    lat = point.latitude,
                    lng = point.longitude,
                    radius = radius,
                    isActive = true
                )
            )

            geofenceService.registerGeofence(
                id = name,
                lat = point.latitude,
                lng = point.longitude,
                radius = radius
            )
        }
    }
}
