package com.example.weather.di

import android.app.Application
import android.content.Context
import androidx.room.Room
import com.example.weather.db.WeatherDatabase
import com.example.weather.service.WeatherService
import dagger.Module
import dagger.Provides
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
abstract class WeatherModule {
    companion object {
        // TODO: Improvement: We should add an config class to get host url depending on the environment like QA, STG and PROD along with any interceptors
        private const val HOST_URL = "https://api.openweathermap.org"

        @Provides
        @Singleton
        fun provideWeatherService(): WeatherService {
            return Retrofit.Builder()
                .baseUrl(HOST_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build().create(WeatherService::class.java)
        }

        @Provides
        @Singleton
        fun provideContext(application: Application): Context {
            return application
        }

        @Provides
        @Singleton
        fun providesWeatherDatabase(context: Context): WeatherDatabase {
            return Room.databaseBuilder(context, WeatherDatabase::class.java, "weather_database").fallbackToDestructiveMigration().build()
        }

    }



}