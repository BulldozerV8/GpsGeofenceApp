package com.example.gpsgeofenceapp.domain

/**
 * Czysty model domain
 * Reprezentuje geofence w warstwie logiki biznesowej i UI.
 * Jest niezależny od bibliotek systemowych (Room) i adnotacji bazodanowych,
 * co zapewnia separację warstw
 */
data class GeofenceModel(
    val id: Long,
    val name: String,
    val lat: Double,
    val lng: Double,
    val radius: Float,
    val isActive: Boolean,
    val startTime: String,
    val endTime: String,
    val repeatDays: String,
    val specificDate: String,
    val shouldMute: Boolean
)