package com.example.weather.db

import androidx.room.Dao
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import androidx.room.Upsert

@Dao
interface WeatherDao {

    @Query("Select * from LastSearchLocationEntity")
    fun getLastSearchLocation(): List<LastSearchLocationEntity>

    @Upsert
    fun updateRecord(location: LastSearchLocationEntity)
}