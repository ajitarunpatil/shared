package com.example.weather.models

data class WeatherResponse(
    val coord: LatLong,
    val weather: List<Weather>,
    val main: Main
)

data class LatLong(val lon: Double, val lat: Double)

data class Weather(val id: Int, val main: String, val description: String, val icon: String)

data class Main(val temp: Double, val feels_like: Double, val temp_min: Double, val temp_max: Double, val humidity: Double)
