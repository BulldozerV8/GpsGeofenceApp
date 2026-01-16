package com.example.gpsgeofenceapp.data.repository

import com.example.gpsgeofenceapp.data.local.GeofenceDao
import com.example.gpsgeofenceapp.data.local.GeofenceEntity
import com.example.gpsgeofenceapp.domain.GeofenceModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class GeofenceRepository(private val dao: GeofenceDao) {

    fun getGeofences(): Flow<List<GeofenceModel>> =
        dao.getAll().map { list ->
            list.map {
                GeofenceModel(
                    it.id, it.name, it.latitude, it.longitude, it.radius, it.isActive
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
                isActive = model.isActive
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
                isActive = model.isActive
            )
        )
    }

}