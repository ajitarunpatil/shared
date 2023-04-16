package com.example.weatherapp.di

import android.app.Application
import com.example.weather.di.WeatherModule
import com.example.weatherapp.WeatherApplication
import dagger.BindsInstance
import dagger.Component
import dagger.android.AndroidInjectionModule
import javax.inject.Singleton

@Singleton
@Component(modules = [AndroidInjectionModule::class, ActivitiesModule::class, WeatherModule::class])
interface ApplicationComponent {
    fun inject(application: WeatherApplication)

    @Component.Builder
    interface Builder {
        @BindsInstance
        fun application(application: Application): Builder

        fun build(): ApplicationComponent
    }
}