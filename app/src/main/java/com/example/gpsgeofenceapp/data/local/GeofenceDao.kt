package com.example.gpsgeofenceapp.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.example.gpsgeofenceapp.data.local.GeofenceEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface GeofenceDao {

    @Query("SELECT * FROM geofences")
    fun getAll(): Flow<List<GeofenceEntity>>

    @Insert
    suspend fun insert(geofence: GeofenceEntity)

    @Delete
    suspend fun delete(geofence: GeofenceEntity)
}