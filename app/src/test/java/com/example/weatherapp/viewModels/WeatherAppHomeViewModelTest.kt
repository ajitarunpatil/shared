package com.example.weatherapp.viewModels

import android.content.Context
import com.example.weather.models.LocationData
import com.example.weather.models.WeatherResponse
import com.example.weather.provider.WeatherDataProvider
import io.mockk.clearAllMocks
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Ignore
import org.junit.Test

class WeatherAppHomeViewModelTest {

    private val weatherDataProvider = mockk<WeatherDataProvider>()
    private val context = mockk<Context>()
    private val locationData = mockk<LocationData>()
    private val weatherResponse = mockk<WeatherResponse>()
    private val testLat = 123.12
    private val testLong = 123.12
    private lateinit var subject: WeatherAppHomeViewModel

    @Before
    fun setup() {
        Dispatchers.setMain(UnconfinedTestDispatcher())
        subject = WeatherAppHomeViewModel(weatherDataProvider)
    }

    @After
    fun tearDown() {
        clearAllMocks()
    }

    @Ignore
    @Test
    fun loadWeatherDataForUserLocation_permissionGrantedAndHasLastUserLocation_showsWeatherDataForUserCurrentLocation(){
        //TODO: Need to move Get Location logic to another class something like LocationProvider so that we can mock it easily
    }

    @Test
    fun loadWeatherDataForUserLocation_permissionNotGrantedAndHasLastUserLocation_showsWeatherDataForLastSearchedLocation() {
        coEvery { weatherDataProvider.getWeatherDataForLastSearchedLocation() } returns Pair(locationData, weatherResponse)

        subject.loadWeatherDataForUserLocation(context, false)

        assertTrue(subject.appUiState.value.state is WeatherAppHomeViewModel.AppUiState.Success)
        assertEquals(Pair(locationData, weatherResponse), subject.appUiState.value.data)
    }

    @Test
    fun handleLocationDisabledScenario_hasLastSavedLocation_showsWeatherDataForLastSearchedLocation() {
        coEvery { weatherDataProvider.getWeatherDataForLastSearchedLocation() } returns Pair(locationData, weatherResponse)
        coEvery { weatherDataProvider.isLastSavedLocationAvailable() } returns true

        subject.handleLocationDisabledScenario()

        assertTrue(subject.appUiState.value.state is WeatherAppHomeViewModel.AppUiState.Success)
        assertEquals(Pair(locationData, weatherResponse), subject.appUiState.value.data)
    }

    @Test
    fun handleLocationDisabledScenario_doNotHaveLastSavedLocation_setsErrorState() {
        coEvery { weatherDataProvider.isLastSavedLocationAvailable() } returns false

        subject.handleLocationDisabledScenario()

        assertTrue(subject.appUiState.value.state is WeatherAppHomeViewModel.AppUiState.Error)
    }

    @Test
    fun updateWeatherDataForUserLocation_success_setsSuccessState() {
        coEvery { weatherDataProvider.getWeatherDataForUserCurrentLocation(testLat, testLong) } returns Pair(locationData, weatherResponse)

        subject.updateWeatherDataForUserLocation(testLat, testLong)

        assertTrue(subject.appUiState.value.state is WeatherAppHomeViewModel.AppUiState.Success)
        assertEquals(Pair(locationData, weatherResponse), subject.appUiState.value.data)
    }

    @Test
    fun updateWeatherDataForUserLocation_error_setErrorState() {
        coEvery { weatherDataProvider.getWeatherDataForUserCurrentLocation(testLat, testLong) } throws Throwable()

        subject.updateWeatherDataForUserLocation(testLat, testLong)

        assertTrue(subject.appUiState.value.state is WeatherAppHomeViewModel.AppUiState.Error)
    }

    @Test
    fun updateWeatherDataForSearchLocation_success_setsSuccessState() {
        coEvery { weatherDataProvider.getWeatherDataByLocation("locationName") } returns Pair(locationData, weatherResponse)

        subject.updateWeatherDataForSearchLocation("locationName")

        assertTrue(subject.appUiState.value.state is WeatherAppHomeViewModel.AppUiState.Success)
        assertEquals(Pair(locationData, weatherResponse), subject.appUiState.value.data)
    }

    @Test
    fun updateWeatherDataForSearchLocation_error_setsErrorState() {
        coEvery { weatherDataProvider.getWeatherDataByLocation("locationName") } throws Throwable()

        subject.updateWeatherDataForSearchLocation("locationName")

        assertTrue(subject.appUiState.value.state is WeatherAppHomeViewModel.AppUiState.Error)
    }

    @Test
    fun getFahrenheitValue_returnsConvertedValueInString() {
        assertEquals("65.73 °F", subject.getFahrenheitValue(291.89))
        assertEquals("63.78 °F", subject.getFahrenheitValue(290.81))
        assertEquals("22.38 °F", subject.getFahrenheitValue(267.81))
    }

}