package com.example.weather.db

import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.coroutines.CoroutineContext

class WeatherRepository @Inject constructor(private val weatherDatabase: WeatherDatabase): CoroutineScope{

    override val coroutineContext: CoroutineContext
        get() = Job() + Dispatchers.IO + CoroutineExceptionHandler { _, e -> e.printStackTrace() }

    suspend fun getLastSearchedLocation(): List<LastSearchLocationEntity> {
        return async(coroutineContext){ weatherDatabase.weatherDao().getLastSearchLocation() }.await()
    }

    fun updateLastSearchLocation(lastSearchLocationEntity: LastSearchLocationEntity) {
        launch(coroutineContext) {
            weatherDatabase.weatherDao().updateRecord(lastSearchLocationEntity)
        }
    }
}