package com.example.gpsgeofenceapp.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.gpsgeofenceapp.data.local.GeofenceEntity
import kotlinx.coroutines.flow.Flow

/**
 * Interfejs dostępu do danych (DAO) dla tabeli Geofence.
 * Zawiera mapowanie metod Kotlin na zapytania SQL:
 * - pobieranie wszystkich obszarów (Flow)
 * - dodawanie, usuwanie i aktualizacja rekordów.
 */
@Dao
interface GeofenceDao {

    @Query("SELECT * FROM geofences")
    fun getAll(): Flow<List<GeofenceEntity>>

    @Query("SELECT * FROM geofences")
    suspend fun getAllList(): List<GeofenceEntity>

    @Insert
    suspend fun insert(geofence: GeofenceEntity)

    @Delete
    suspend fun delete(geofence: GeofenceEntity)

    @Query("SELECT * FROM geofences WHERE id = :id")
    suspend fun getById(id: Long): GeofenceEntity?

    @Update
    suspend fun update(geofence: GeofenceEntity)
}