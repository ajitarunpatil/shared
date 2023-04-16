package com.example.weatherapp.di

import com.example.weatherapp.views.WeatherAppHomeActivity
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class ActivitiesModule {

    @ContributesAndroidInjector
    abstract fun bindWeatherAppHomeActivity(): WeatherAppHomeActivity
}