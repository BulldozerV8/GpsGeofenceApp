package com.example.gpsgeofenceapp.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Model danych reprezentujący pojedynczy wpis w historii zdarzeń.
 * Mapowany na tabelę "geofence_logs".
 * Służy do archiwizacji informacji o czasie i typie zdarzenia (Wejście/Wyjście).
 */
@Entity(tableName = "geofence_logs")
data class GeofenceLogEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val date: String,
    val message: String
)