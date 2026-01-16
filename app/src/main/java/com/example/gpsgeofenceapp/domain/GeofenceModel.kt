package com.example.gpsgeofenceapp.domain

data class GeofenceModel(
    val id: Long,
    val name: String,
    val lat: Double,
    val lng: Double,
    val radius: Float,
    val isActive: Boolean
)
