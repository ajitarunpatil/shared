package com.example.weather.db

import io.mockk.clearAllMocks
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class WeatherRepositoryTest {

    private val weatherDatabase = mockk<WeatherDatabase>()
    private val weatherDao = mockk<WeatherDao>()
    private val lastSearchLocationEntity = mockk<LastSearchLocationEntity>()
    private lateinit var subject: WeatherRepository

    @Before
    fun setup() {
        every { weatherDatabase.weatherDao()} returns weatherDao
        subject = WeatherRepository(weatherDatabase)
    }

    @After
    fun tearDown() {
        clearAllMocks()
    }

    @Test
    fun getLastSearchedLocation_success_returnsLastSearchLocationEntity() {
        coEvery { weatherDao.getLastSearchLocation() } returns listOf(lastSearchLocationEntity)

        val result = runBlocking { subject.getLastSearchedLocation() }

        assertEquals(listOf(lastSearchLocationEntity), result)
    }

    @Test
    fun updateLastSearchLocation_updateRecordGetsCalled() {
        subject.updateLastSearchLocation(lastSearchLocationEntity)

        coVerify { weatherDao.updateRecord(lastSearchLocationEntity) }
    }
}