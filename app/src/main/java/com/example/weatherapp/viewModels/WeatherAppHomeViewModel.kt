package com.example.weatherapp.viewModels

import android.annotation.SuppressLint
import android.content.Context
import android.os.Looper
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weather.models.LocationData
import com.example.weather.models.WeatherResponse
import com.example.weather.provider.WeatherDataProvider
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsRequest
import com.google.android.gms.location.LocationSettingsResponse
import com.google.android.gms.location.SettingsClient
import com.google.android.gms.tasks.Task
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.math.RoundingMode
import java.text.DecimalFormat
import javax.inject.Inject
import kotlin.coroutines.CoroutineContext

class WeatherAppHomeViewModel @Inject constructor(private val weatherDataProvider: WeatherDataProvider): ViewModel(), DefaultLifecycleObserver, CoroutineScope  {

    private val _appUiState = MutableStateFlow(ViewState())
    val appUiState: StateFlow<ViewState> = _appUiState

    override val coroutineContext: CoroutineContext
        get() = Job() + Dispatchers.Main + CoroutineExceptionHandler { _, e ->
            handleError(e)
        }

    override fun onPause(owner: LifecycleOwner) {
        coroutineContext.cancel()
        super.onPause(owner)
    }

    private fun handleError(e: Throwable) {
        _appUiState.value = ViewState().setErrorState(e)
    }

    @SuppressLint("MissingPermission")
    fun loadWeatherDataForUserLocation(context: Context, isPermissionGranted: Boolean) {
        _appUiState.value = ViewState().setLoadingState(appUiState.value.data)
        if (isPermissionGranted) {
            val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
            fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                location?.let { updateWeatherDataForUserLocation(location.latitude, location.longitude) }
                if (location == null) {
                    val locationRequest = LocationRequest.create().apply {
                        priority = LocationRequest.PRIORITY_HIGH_ACCURACY
                        numUpdates = 1
                        interval = 5000
                    }
                    // Checking for Location Settings enabled or not
                    val builder = LocationSettingsRequest.Builder().addLocationRequest(locationRequest)
                    val client: SettingsClient = LocationServices.getSettingsClient(context)
                    val task: Task<LocationSettingsResponse> = client.checkLocationSettings(builder.build())
                    task.addOnFailureListener { exception ->
                        if (exception is ResolvableApiException) {
                            // TODO: Note: Location is not enabled! If there is no last searched location, show an error state to user else get data for last searched location
                            handleLocationDisabledScenario()
                        }
                    }
                    // listening for lastLocation updates
                    val locationCallback = object : LocationCallback() {
                        override fun onLocationResult(it: LocationResult) {
                            updateWeatherDataForUserLocation(
                                it.lastLocation.latitude,
                                it.lastLocation.longitude
                            )
                        }
                    }
                    fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper() )
                }
            }
        } else {
           //TODO: Note: Permission not granted use the last searched location if available or show error to user
            updateWeatherDataForLastSearchLocation()
        }

    }

    fun handleLocationDisabledScenario() {
        viewModelScope.launch(coroutineContext) {
            if (weatherDataProvider.isLastSavedLocationAvailable()) {
                updateWeatherDataForLastSearchLocation()
            } else {
                _appUiState.value = ViewState().setErrorState(Throwable("Please enable Location Settings to search for current location!!"))
            }
        }
    }

    fun updateWeatherDataForUserLocation(latitude: Double, longitude: Double) {
        viewModelScope.launch(coroutineContext) {
            val result = weatherDataProvider.getWeatherDataForUserCurrentLocation(latitude, longitude)
            _appUiState.value = ViewState().setSuccessState(result)
        }
    }

    fun updateWeatherDataForSearchLocation(locationName: String) {
        viewModelScope.launch(coroutineContext) {
            val result = weatherDataProvider.getWeatherDataByLocation(locationName)
            _appUiState.value = ViewState().setSuccessState(result)
        }
    }

    private fun updateWeatherDataForLastSearchLocation() {
        viewModelScope.launch(coroutineContext) {
            val result = weatherDataProvider.getWeatherDataForLastSearchedLocation()
            _appUiState.value = ViewState().setSuccessState(result)
        }
    }

    //TODO: Improvement - This function should be part of Util class
    fun getFahrenheitValue(value: Double): String {
        val fahrenheitValue = ((value - 273.15) * (9.0/5)) + 32.0
        val df = DecimalFormat("#.##")
        df.roundingMode = RoundingMode.DOWN
        val roundingOffValue = df.format(fahrenheitValue)
        return "$roundingOffValue °F"
    }

    data class ViewState(
        val state: AppUiState = AppUiState.Loading,
        val data: Pair<LocationData, WeatherResponse>? = null,
        val exception: Throwable? = null
    ) {
        fun setErrorState(exception: Throwable? = null) = copy(state = AppUiState.Error, exception = exception)

        fun setSuccessState(data: Pair<LocationData, WeatherResponse>?) = copy(state = AppUiState.Success, data = data)

        fun setLoadingState(data: Pair<LocationData, WeatherResponse>?) = copy(state = AppUiState.Loading, data = data)
    }

    sealed interface AppUiState {
        object Loading : AppUiState

        object Error : AppUiState

        object Success : AppUiState
    }
}