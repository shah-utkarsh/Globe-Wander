package com.example.globewander

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.globewander.models.WeatherData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL

// ViewModel for managing weather data
class WeatherViewModel : ViewModel() {

    // MutableLiveData for holding weather data
    private val _weatherData = MutableLiveData<WeatherData>()

    // LiveData exposed to the UI
    val weatherData: LiveData<WeatherData>
        get() = _weatherData

    // Fetch weather data from the provided API URL
    fun fetchWeatherData(apiUrl: String) {
        viewModelScope.launch(Dispatchers.IO) {
            // Fetch data from the API and parse it
            val result = fetchData(apiUrl)
            val weatherData = parseWeatherData(result)

            // Update MutableLiveData with the parsed weather data
            _weatherData.postValue(weatherData)
        }
    }

    // Function to fetch data from the API
    private fun fetchData(apiUrl: String): String {
        val url = URL(apiUrl)
        val connection = url.openConnection() as HttpURLConnection

        return try {
            val reader = BufferedReader(InputStreamReader(connection.inputStream))
            val response = StringBuilder()

            var line: String?
            while (reader.readLine().also { line = it } != null) {
                response.append(line)
            }

            response.toString()
        } finally {
            connection.disconnect()
        }
    }

    // Function to parse weather data from the JSON response
    private fun parseWeatherData(result: String): WeatherData {
        val jsonObject = JSONObject(result)
        val currentObject = jsonObject.getJSONObject("current")

        return WeatherData(
            lastUpdated = currentObject.getString("last_updated"),
            tempC = currentObject.getDouble("temp_c"),
            tempF = currentObject.getDouble("temp_f"),
            condition = currentObject.getJSONObject("condition").getString("text")
        )
    }
}
