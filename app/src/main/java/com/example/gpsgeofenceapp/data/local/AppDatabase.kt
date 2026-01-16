package com.example.gpsgeofenceapp.data.local

import androidx.room.Database
import androidx.room.RoomDatabase

/**
 * Główna baza danych aplikacji oparta na bibliotece Room.
 * Definiuje dostępne tabele (Entities) oraz wersję schematu bazy.
 * Udostępnia obiekty DAO do wykonywania operacji na danych.
 */
@Database(
    entities = [GeofenceEntity::class, GeofenceLogEntity::class],
    version = 6,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun geofenceDao(): GeofenceDao
    abstract fun logDao(): GeofenceLogDao
}