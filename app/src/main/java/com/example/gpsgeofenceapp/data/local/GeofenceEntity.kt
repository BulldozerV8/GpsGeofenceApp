package com.example.gpsgeofenceapp.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Model danych reprezentujący pojedynczy obszar monitorowany (Geofence).
 * Mapowany bezpośrednio na tabelę "geofences" w bazie danych.
 * Przechowuje koordynaty, promień oraz ustawienia harmonogramu i wyciszania.
 */
@Entity(tableName = "geofences")
data class GeofenceEntity (
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val latitude: Double,
    val longitude: Double,
    val radius: Float,
    val isActive: Boolean = true,
    val startTime: String = "00:00",
    val endTime: String = "23:59",
    val repeatDays: String = "",
    val specificDate: String = "",
    val shouldMute: Boolean = false
)