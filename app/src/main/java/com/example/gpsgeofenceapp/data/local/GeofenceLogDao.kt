package com.example.gpsgeofenceapp.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

/**
 * Interfejs DAO dla historii zdarzeń (logów).
 * Umożliwia zapisywanie nowych zdarzeń wejścia/wyjścia
 * oraz czyszczenie całej historii.
 */
@Dao
interface GeofenceLogDao {
    @Insert
    suspend fun insert(log: GeofenceLogEntity)

    @Query("SELECT * FROM geofence_logs ORDER BY id DESC")
    fun getAll(): Flow<List<GeofenceLogEntity>>

    @Query("DELETE FROM geofence_logs")
    suspend fun deleteAll()
}