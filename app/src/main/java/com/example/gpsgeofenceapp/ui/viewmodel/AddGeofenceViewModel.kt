package com.example.gpsgeofenceapp.ui.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.gpsgeofenceapp.data.repository.GeofenceRepository
import com.example.gpsgeofenceapp.domain.GeofenceModel
import com.example.gpsgeofenceapp.service.GeofenceService
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * ViewModel zarządzający stanem formularza dodawania/edycji geofence.
 * Przechowuje dane wprowadzane przez użytkownika (nazwa, współrzędne, czas).
 * Odpowiada za logikę zapisu:
 * - aktualizacja bazy danych (poprzez Repository)
 * - rejestracja geofence w usługach systemowych (poprzez GeofenceService).
 */

class AddGeofenceViewModel(
    private val repository: GeofenceRepository,
    private val geofenceService: GeofenceService
) : ViewModel() {

    private var editingGeofenceId: Long = 0

    var selectedLatLng by mutableStateOf<LatLng?>(null)
    var radius by mutableStateOf(100f)
    var name by mutableStateOf("")

    // CZAS
    var isAllDay by mutableStateOf(true)
    var startTime by mutableStateOf("08:00")
    var endTime by mutableStateOf("16:00")

    // DATA
    var isAlwaysActive by mutableStateOf(true)
    var isRepeating by mutableStateOf(false)
    var specificDate by mutableStateOf(getCurrentDate())
    val selectedDays = mutableStateListOf<Int>()

    var shouldMute by mutableStateOf(false)

    fun loadGeofence(id: Long) {
        viewModelScope.launch {
            val geo = repository.getGeofenceById(id)
            if (geo != null) {
                editingGeofenceId = geo.id

                name = geo.name
                radius = geo.radius
                selectedLatLng = LatLng(geo.lat, geo.lng)

                if (geo.startTime == "00:00" && geo.endTime == "23:59") {
                    isAllDay = true
                    startTime = "08:00"
                    endTime = "16:00"
                } else {
                    isAllDay = false
                    startTime = geo.startTime
                    endTime = geo.endTime
                }

                selectedDays.clear()
                if (geo.repeatDays.isEmpty() && geo.specificDate.isEmpty()) {
                    isAlwaysActive = true
                    isRepeating = false
                } else {
                    isAlwaysActive = false
                    if (geo.repeatDays.isNotEmpty()) {
                        isRepeating = true
                        val days = geo.repeatDays.split(",").mapNotNull { it.toIntOrNull() }
                        selectedDays.addAll(days)
                    } else {
                        isRepeating = false
                        specificDate = geo.specificDate
                    }
                }

                shouldMute = geo.shouldMute
            }
        }
    }

    fun toggleDay(day: Int) {
        if (selectedDays.contains(day)) {
            selectedDays.remove(day)
        } else {
            selectedDays.add(day)
        }
    }

    fun save() {
        val point = selectedLatLng ?: return

        val finalRepeatString = if (isAlwaysActive) "" else {
            if (isRepeating) selectedDays.joinToString(",") else ""
        }
        val finalDateString = if (isAlwaysActive) "" else {
            if (isRepeating) "" else specificDate
        }

        val finalStartTime = if (isAllDay) "00:00" else startTime
        val finalEndTime = if (isAllDay) "23:59" else endTime

        val model = GeofenceModel(
            id = editingGeofenceId,
            name = name,
            lat = point.latitude,
            lng = point.longitude,
            radius = radius,
            isActive = true,
            startTime = finalStartTime,
            endTime = finalEndTime,
            repeatDays = finalRepeatString,
            specificDate = finalDateString,
            shouldMute = shouldMute
        )

        viewModelScope.launch {
            if (editingGeofenceId == 0L) {
                repository.addGeofence(model)
            } else {
                repository.updateGeofence(model)
            }

            geofenceService.registerGeofence(
                id = name,
                lat = point.latitude,
                lng = point.longitude,
                radius = radius
            )
        }
    }

    fun clearForm() {
        editingGeofenceId = 0
        name = ""
        selectedLatLng = null
        radius = 100f
        isAllDay = true
        startTime = "08:00"
        endTime = "16:00"
        isAlwaysActive = true
        isRepeating = false
        specificDate = getCurrentDate()
        selectedDays.clear()
        shouldMute = false
    }

    private fun getCurrentDate(): String {
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        return sdf.format(Date())
    }
}