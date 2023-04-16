package com.example.weather.models

data class LocationData(
    val name: String = "",
    val lat: Double,
    val lon: Double,
    val country: String,
    val state: String
)
