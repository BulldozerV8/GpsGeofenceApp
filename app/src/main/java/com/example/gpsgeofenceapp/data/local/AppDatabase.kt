package com.example.gpsgeofenceapp.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.gpsgeofenceapp.data.local.GeofenceDao
import com.example.gpsgeofenceapp.data.local.GeofenceEntity

@Database(
    entities = [GeofenceEntity::class],
    version = 2
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun geofenceDao(): GeofenceDao
}
