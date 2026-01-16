package com.example.gpsgeofenceapp.data.repository

import com.example.gpsgeofenceapp.data.local.GeofenceDao
import com.example.gpsgeofenceapp.data.local.GeofenceEntity
import com.example.gpsgeofenceapp.data.local.GeofenceLogDao // <-- Import
import com.example.gpsgeofenceapp.domain.GeofenceModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/**
 * Repozytorium danych
 * Pośredniczy między bazą danych (DAO) a warstwą UI/ViewModel,
 * separując logikę biznesową od źródeł danych.
 */
class GeofenceRepository(
    private val dao: GeofenceDao,
    private val logDao: GeofenceLogDao
) {

    fun getLogs() = logDao.getAll()

    suspend fun clearLogs() {
        logDao.deleteAll()
    }

    fun getGeofences(): Flow<List<GeofenceModel>> =
        dao.getAll().map { list ->
            list.map {
                GeofenceModel(
                    it.id, it.name, it.latitude, it.longitude, it.radius, it.isActive,
                    it.startTime, it.endTime, it.repeatDays, it.specificDate, it.shouldMute
                )
            }
        }

    suspend fun addGeofence(model: GeofenceModel){
        dao.insert(
            GeofenceEntity(
                name = model.name,
                latitude = model.lat,
                longitude = model.lng,
                radius = model.radius,
                isActive = model.isActive,
                startTime = model.startTime,
                endTime = model.endTime,
                repeatDays = model.repeatDays,
                specificDate = model.specificDate,
                shouldMute = model.shouldMute
            )
        )
    }

    suspend fun deleteGeofence(model: GeofenceModel) {
        dao.delete(
            GeofenceEntity(
                id = model.id,
                name = model.name,
                latitude = model.lat,
                longitude = model.lng,
                radius = model.radius,
                isActive = model.isActive,
                startTime = model.startTime,
                endTime = model.endTime,
                repeatDays = model.repeatDays,
                specificDate = model.specificDate,
                shouldMute = model.shouldMute
            )
        )
    }

    suspend fun getGeofenceById(id: Long): GeofenceModel? {
        val entity = dao.getById(id) ?: return null
        return GeofenceModel(
            entity.id, entity.name, entity.latitude, entity.longitude, entity.radius, entity.isActive,
            entity.startTime, entity.endTime, entity.repeatDays, entity.specificDate, entity.shouldMute
        )
    }

    suspend fun updateGeofence(model: GeofenceModel) {
        dao.update(
            GeofenceEntity(
                id = model.id,
                name = model.name,
                latitude = model.lat,
                longitude = model.lng,
                radius = model.radius,
                isActive = model.isActive,
                startTime = model.startTime,
                endTime = model.endTime,
                repeatDays = model.repeatDays,
                specificDate = model.specificDate,
                shouldMute = model.shouldMute
            )
        )
    }
}