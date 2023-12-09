package com.example.globewander.models

// Data class representing weather information for a specific location
data class WeatherData(
    val lastUpdated: String,    // Timestamp indicating the last update time of the weather data
    val tempC: Double,          // Temperature in Celsius
    val tempF: Double,          // Temperature in Fahrenheit
    val condition: String       // Text description of the weather condition
)
