package com.example.weather.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class LastSearchLocationEntity(
    @PrimaryKey(autoGenerate = false)
    val id: Int = 0,
    val latitude: Double,
    val longitude: Double
)
