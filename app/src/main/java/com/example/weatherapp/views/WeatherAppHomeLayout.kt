package com.example.weatherapp.views

import LocationPermission
import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Button
import androidx.compose.material.Icon
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.example.weather.models.LocationData
import com.example.weather.models.WeatherResponse
import com.example.weatherapp.ui.theme.Blue200
import com.example.weatherapp.ui.theme.Purple700
import com.example.weatherapp.ui.theme.WeatherAppTheme
import com.example.weatherapp.viewModels.WeatherAppHomeViewModel

@SuppressLint("MissingPermission", "UnusedMaterialScaffoldPaddingParameter")
@Composable
fun WeatherAppHomeLayout(viewModel: WeatherAppHomeViewModel) {
    var textValue = remember { mutableStateOf(TextFieldValue("")) }
    val context = LocalContext.current
    LocationPermission { isPermissionGranted ->
        //TODO: Note: Location permission granted by user, search weather data for user's current location
        viewModel.loadWeatherDataForUserLocation(context, isPermissionGranted)
    }
    WeatherAppTheme {
        Scaffold(backgroundColor = Blue200, modifier = Modifier.fillMaxSize(), contentColor = Color.White,
            topBar = { TopAppBar(title = { Text("WeatherApp") }, backgroundColor = Purple700) },
            content = {
                Column(modifier = Modifier.fillMaxSize()) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp), verticalAlignment = Alignment.Bottom
                    ) {
                        TextField(
                            value = textValue.value,
                            onValueChange = {textValue.value = it},
                            modifier = Modifier.weight(0.7f),
                            singleLine = true,
                            placeholder = { Text(text = "Search here") },
                            keyboardOptions = KeyboardOptions.Default,
                            leadingIcon = { Icon( imageVector = Icons.Default.Search, contentDescription = "search icon" )}

                        )
                        Button(
                            onClick = { viewModel.updateWeatherDataForSearchLocation(textValue.value.text) }, modifier = Modifier
                                .weight(0.3f)
                                .padding(horizontal = 8.dp)
                        ) {
                            Text(text = "Get Weather")
                        }
                    }
                    val uiState = viewModel.appUiState.collectAsState()
                    when(uiState.value.state) {
                        is WeatherAppHomeViewModel.AppUiState.Success -> uiState.value.data?.let { ShowSuccessState(viewModel, data = it)} ?: ShowErrorState("Invalid Data")
                        is WeatherAppHomeViewModel.AppUiState.Loading -> ShowLoadingState()
                        is WeatherAppHomeViewModel.AppUiState.Error -> ShowErrorState(uiState.value.exception?.message ?: "Generic Error")
                    }
                }
            })
    }
}

@Composable
fun ShowErrorState(errorMsg: String) {
    Column {
        Text(text = "Sorry! Location not found, search for new location.")
        Text(text = "Error:: $errorMsg")
    }
}

@Composable
fun ShowLoadingState() {
    Column {
        Text(text = "Fetching weather data...")
    }
}

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun ShowSuccessState(viewModel: WeatherAppHomeViewModel, data: Pair<LocationData, WeatherResponse>) {
    val weatherResponse = data.second
    val weather = data.second.weather[0]
    val url = "https://openweathermap.org/img/wn/${weather.icon}@2x.png"
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {
        Text ( style = TextStyle(fontSize = 24.sp), text = "${data.first.name}, ${data.first.state}, ${data.first.country}")
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp), verticalAlignment = Alignment.Bottom
    ) {
        Column(modifier = Modifier.weight(0.5f), horizontalAlignment = Alignment.CenterHorizontally) {
            Text("Min ${viewModel.getFahrenheitValue(weatherResponse.main.temp_min)}   Max ${viewModel.getFahrenheitValue(weatherResponse.main.temp_max)}")
            Text ( style = TextStyle(fontSize = 48.sp), text = viewModel.getFahrenheitValue(weatherResponse.main.temp))
            Text ( text = "Feels like ${viewModel.getFahrenheitValue(weatherResponse.main.feels_like)}")
        }
        Column(modifier = Modifier.weight(0.5f), horizontalAlignment = Alignment.CenterHorizontally) {
            GlideImage(
                model = url,
                modifier = Modifier
                    .width(114.dp)
                    .height(114.dp),
                contentDescription = "weather icon"
            )
            Text ( text = weather.main )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    WeatherAppTheme {
        ShowLoadingState()
    }
}

