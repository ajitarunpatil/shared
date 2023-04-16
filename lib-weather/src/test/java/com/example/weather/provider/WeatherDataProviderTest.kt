package com.example.weather.provider

import com.example.weather.db.LastSearchLocationEntity
import com.example.weather.db.WeatherRepository
import com.example.weather.models.LocationData
import com.example.weather.models.WeatherResponse
import com.example.weather.service.WeatherService
import io.mockk.clearAllMocks
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertFalse
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Test

class WeatherDataProviderTest {

    private val weatherService = mockk<WeatherService>(relaxed = true)
    private val weatherRepository = mockk<WeatherRepository>(relaxed = true)
    private lateinit var subject: WeatherDataProvider
    private val testLat = 123.12
    private val testLong = 789.29
    private val locationName = "locationName"
    private val api_key = "b19fdfb43b2a47d0eabe5ea9a042e447"
    private val weatherResponse = mockk<WeatherResponse>(relaxed = true)
    private val locationData = mockk<LocationData>(relaxed = true)
    private val lastSearchLocationEntity = mockk<LastSearchLocationEntity>(relaxed = true)

    @Before
    fun setup() {
        coEvery { weatherService.getWeatherData(testLat, testLong, api_key) } returns weatherResponse
        coEvery { weatherService.getLocationFromCoordinates(testLat, testLong, api_key) } returns listOf(locationData)
        coEvery { weatherService.getCoordinatesFromLocationName(locationName, api_key) } returns listOf(locationData)

        subject = WeatherDataProvider(weatherService, weatherRepository)
    }

    @After
    fun tearDown() {
        clearAllMocks()
    }

    @Test
    fun getWeatherDataForUserCurrentLocation_success_returnsPairOfLocationDataAndWeatherData() {
        val result = runBlocking { subject.getWeatherDataForUserCurrentLocation(testLat, testLong) }

        assertEquals(Pair(locationData, weatherResponse), result)
        coVerify { weatherRepository.updateLastSearchLocation(LastSearchLocationEntity(latitude = testLat, longitude = testLong)) }
    }

    @Test
    fun getWeatherDataForUserCurrentLocation_fail_throwsException() {
        coEvery { weatherService.getWeatherData(testLat, testLong, api_key) } throws Throwable()

        Assert.assertThrows(Throwable::class.java) {
            runBlocking { subject.getWeatherDataForUserCurrentLocation(testLat, testLong) }
        }
        coVerify (inverse = true) { weatherRepository.updateLastSearchLocation(LastSearchLocationEntity(latitude = testLat, longitude = testLong)) }
    }

    @Test
    fun getWeatherDataByLocation_success_returnsPairOfLocationDataAndWeatherData() {
        every { locationData.lat } returns testLat
        every { locationData.lon } returns testLong

        val result = runBlocking { subject.getWeatherDataByLocation(locationName) }

        assertEquals(Pair(locationData, weatherResponse), result)
        coVerify { weatherRepository.updateLastSearchLocation(LastSearchLocationEntity(latitude = testLat, longitude = testLong)) }
    }

    @Test
    fun getWeatherDataByLocation_fail_throwsException() {
        coEvery { weatherService.getCoordinatesFromLocationName(locationName, api_key) } throws Throwable()

        Assert.assertThrows(Throwable::class.java) {
            runBlocking { subject.getWeatherDataByLocation(locationName) }
        }
        coVerify (inverse = true) { weatherRepository.updateLastSearchLocation(LastSearchLocationEntity(latitude = testLat, longitude = testLong)) }
    }

    @Test
    fun getWeatherDataForLastSearchedLocation_success_returnsPairOfLocationDataAndWeatherData() {
        coEvery { weatherRepository.getLastSearchedLocation() } returns listOf(lastSearchLocationEntity)
        every { locationData.lat } returns testLat
        every { locationData.lon } returns testLong

        val result = runBlocking { subject.getWeatherDataByLocation(locationName) }

        assertEquals(Pair(locationData, weatherResponse), result)
    }

    @Test
    fun getWeatherDataForLastSearchedLocation_noLastSearchedLocation_throwsException() {
        coEvery { weatherRepository.getLastSearchedLocation() } returns emptyList()

        Assert.assertThrows(Throwable::class.java) {
            runBlocking { subject.getWeatherDataForLastSearchedLocation() }
        }

        coVerify (inverse = true) { weatherService.getWeatherData(testLat, testLong, api_key) }
        coVerify (inverse = true) { weatherService.getLocationFromCoordinates(testLat, testLong, api_key) }
    }

    @Test
    fun isLastSavedLocationAvailable_noLastSearchLocation_returnsFalse() {
        coEvery { weatherRepository.getLastSearchedLocation() } returns emptyList()

        val result = runBlocking { subject.isLastSavedLocationAvailable()}

        assertFalse(result)
    }

    @Test
    fun isLastSavedLocationAvailable_lastSearchLocationAvailable_returnsTrue() {
        coEvery { weatherRepository.getLastSearchedLocation() } returns listOf(lastSearchLocationEntity)

        val result = runBlocking { subject.isLastSavedLocationAvailable()}

        assertTrue(result)
    }

}