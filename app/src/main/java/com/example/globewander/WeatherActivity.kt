package com.example.globewander

import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.example.globewander.models.WeatherData

// Activity displaying weather details for a city
class WeatherActivity : AppCompatActivity() {

    // TextViews for displaying weather information
    private lateinit var textViewLastUpdated: TextView
    private lateinit var textViewTemperature: TextView
    private lateinit var textViewCondition: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_weather)

        // Initialize TextViews
        textViewLastUpdated = findViewById(R.id.textViewLastUpdated)
        textViewTemperature = findViewById(R.id.textViewTemperature)
        textViewCondition = findViewById(R.id.textViewCondition)

        // Get the image resource ID from the intent and set it to the city's ImageView
        val imageResId = intent.getIntExtra("imageResId", 0)
        val imageViewCity = findViewById<ImageView>(R.id.imageViewCity)
        imageViewCity.setImageResource(imageResId)

        // Initialize WeatherViewModel using viewModels delegate
        val weatherViewModel: WeatherViewModel by viewModels()

        // Get the city name and construct the API URL
        val cityName = intent.getStringExtra("CITY_NAME")
        val apiKey = "97c506c9106b4c6e96f201759232611"
        val apiUrl = "http://api.weatherapi.com/v1/current.json?key=$apiKey&q=$cityName&aqi=no"

        // Observe changes in the weather data and update UI accordingly
        weatherViewModel.weatherData.observe(this, Observer { weatherData ->
            displayWeather(weatherData)
        })

        // Fetch weather data from the API
        weatherViewModel.fetchWeatherData(apiUrl)
    }

    // Display weather information in the TextViews
    private fun displayWeather(weatherData: WeatherData) {
        // Update TextViews with weather data
        textViewLastUpdated.text = "Last Updated: ${weatherData.lastUpdated}"
        textViewTemperature.text = "Temperature: ${weatherData.tempC}°C / ${weatherData.tempF}°F"
        textViewCondition.text = "Condition: ${weatherData.condition}"
    }
}
