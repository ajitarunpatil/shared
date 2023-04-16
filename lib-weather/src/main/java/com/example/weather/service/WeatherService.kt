package com.example.weather.service

import com.example.weather.models.LocationData
import com.example.weather.models.WeatherResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherService {

    @GET("/geo/1.0/direct?")
    suspend fun getCoordinatesFromLocationName(@Query("q") location: String, @Query("appid") apiKey: String): List<LocationData>

    @GET("/geo/1.0/reverse?")
    suspend fun getLocationFromCoordinates(@Query("lat") latitude: Double, @Query("lon") longitude: Double, @Query("appid") apiKey: String): List<LocationData>

    @GET("/data/2.5/weather?")
    suspend fun getWeatherData(@Query("lat") latitude: Double, @Query("lon") longitude: Double,  @Query("appid") apiKey: String): WeatherResponse
}