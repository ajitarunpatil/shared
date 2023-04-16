package com.example.weatherapp.views

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.example.weatherapp.viewModels.WeatherAppHomeViewModel
import dagger.android.AndroidInjection
import javax.inject.Inject

class WeatherAppHomeActivity : ComponentActivity() {

    @Inject
    lateinit var viewModel: WeatherAppHomeViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)

        super.onCreate(savedInstanceState)
        setContent {
            WeatherAppHomeLayout(viewModel = viewModel)
        }
        lifecycle.addObserver(viewModel)
    }
}
