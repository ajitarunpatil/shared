package com.example.weather.db

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [LastSearchLocationEntity::class], version = 1)
abstract class WeatherDatabase: RoomDatabase(){
    abstract fun weatherDao(): WeatherDao
}