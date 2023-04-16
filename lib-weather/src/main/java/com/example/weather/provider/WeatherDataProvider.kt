package com.example.weather.provider

import com.example.weather.db.LastSearchLocationEntity
import com.example.weather.db.WeatherDatabase
import com.example.weather.db.WeatherRepository
import com.example.weather.models.LocationData
import com.example.weather.models.WeatherResponse
import com.example.weather.service.WeatherService
import javax.inject.Inject

class WeatherDataProvider @Inject constructor(
    private val weatherService: WeatherService,
    private val weatherRepository: WeatherRepository
) {

    suspend fun getWeatherDataForUserCurrentLocation(
        latitude: Double,
        longitude: Double
    ): Pair<LocationData, WeatherResponse> {
        //TODO: Handle the scenarios for weatherData or locationData empty or null from api. Currently it will handled at ViewModel
        val weatherData = weatherService.getWeatherData(latitude, longitude, API_KEY)
        val locationData =
            weatherService.getLocationFromCoordinates(latitude, longitude, API_KEY)[0]
        weatherRepository.updateLastSearchLocation(
            LastSearchLocationEntity(
                latitude = latitude,
                longitude = longitude
            )
        )
        return Pair(locationData, weatherData)
    }

    suspend fun getWeatherDataByLocation(location: String): Pair<LocationData, WeatherResponse> {
        //TODO: Handle the scenarios for weatherData or locationData empty or null from api. Currently it will handled at ViewModel
        val locationData = weatherService.getCoordinatesFromLocationName(location, API_KEY)[0]
        val weatherData = weatherService.getWeatherData(locationData.lat, locationData.lon, API_KEY)
        weatherRepository.updateLastSearchLocation(
            LastSearchLocationEntity(
                latitude = locationData.lat,
                longitude = locationData.lon
            )
        )
        return Pair(locationData, weatherData)
    }

    suspend fun getWeatherDataForLastSearchedLocation(): Pair<LocationData, WeatherResponse> {
        val lastSearchLocation = weatherRepository.getLastSearchedLocation()
        if (lastSearchLocation.isNotEmpty()) {
            //TODO: Handle the scenarios for weatherData or locationData empty or null from api. Currently it will handled at ViewModel
            val weatherData = weatherService.getWeatherData(
                lastSearchLocation[0].latitude,
                lastSearchLocation[0].longitude,
                API_KEY
            )
            val locationData = weatherService.getLocationFromCoordinates(
                lastSearchLocation[0].latitude,
                lastSearchLocation[0].longitude,
                API_KEY
            )[0]
            return Pair(locationData, weatherData)
        } else {
            throw Throwable("Enter the location and search to get weather data!")
        }
    }

    suspend fun isLastSavedLocationAvailable(): Boolean {
        return weatherRepository.getLastSearchedLocation().isNotEmpty()
    }

    companion object {
        //TODO: Improvement - API key should be part of api configuration file along with base url configuration
        private const val API_KEY = "b19fdfb43b2a47d0eabe5ea9a042e447"
    }
}