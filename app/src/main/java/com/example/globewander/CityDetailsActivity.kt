package com.example.globewander

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.globewander.models.Destination
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase


// Activity displaying details of a city, including weather and map options
class CityDetailsActivity : AppCompatActivity() {

    // Reference to the Firebase Database
    private lateinit var database: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_city_details)

        // Initialize Firebase Database reference
        database = FirebaseDatabase.getInstance().reference.child("Destinations")

        // Retrieve city name and image resource ID from the intent
        val cityName = intent.getStringExtra("cityName")
        val imageResId = intent.getIntExtra("imageResId", 0)

        // Fetch and display city details
        cityName?.let { fetchCityDetails(it) }

        // Set OnClickListener for the "Check Weather" button
        val btnWeather: Button = findViewById(R.id.btnWeather)
        btnWeather.setOnClickListener {
            openWeatherActivity(cityName ?: "", imageResId)
        }

        // Set OnClickListener for the "Show Map" button
        val buttonShowMap = findViewById<Button>(R.id.buttonShowMap)
        buttonShowMap.setOnClickListener {
            // Check if the city name is available before starting the map activity
            if (cityName != null) {
                val mapIntent = Intent(this, MapsActivity::class.java).apply {
                    putExtra("cityName", cityName)
                }
                startActivity(mapIntent)
            } else {
                // Display a toast message if the city name is not available
                Toast.makeText(this, "City name not available", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // Fetch city details from Firebase Database
    private fun fetchCityDetails(cityName: String) {
        DestinationUploader(database).getDestinationByCity(cityName, object : DestinationUploader.DestinationCallback {
            override fun onDestinationLoaded(destination: Destination?) {
                // Update UI with destination details
                updateUI(destination)
            }
        })
    }

    // Update UI elements with destination details
    private fun updateUI(destination: Destination?) {
        destination?.let {
            // Update UI elements with destination details
            findViewById<TextView>(R.id.textViewCityName).text = it.city
            findViewById<TextView>(R.id.textViewAdvice).text = it.advice
            findViewById<TextView>(R.id.textViewLanguage).text = "Language: ${it.language}"
            findViewById<TextView>(R.id.textViewBudget).text = "Budget: $${it.budget}"
            findViewById<TextView>(R.id.textViewPopulation).text = "Population: ${it.population}"

            // Update attractions list
            val attractionsList = findViewById<TextView>(R.id.textViewAttractionsList)
            attractionsList.text = buildString {
                append("Attractions:\n")
                it.attractions.forEachIndexed { index, attraction ->
                    append("${index + 1}. ${attraction.name} - ${attraction.description}\n")
                }
            }

            // Update food list
            val foodList = findViewById<TextView>(R.id.textViewFoodList)
            foodList.text = buildString {
                append("Food:\n")
                it.food.forEachIndexed { index, food ->
                    append("${index + 1}. ${food.name} - ${food.description}\n")
                }
            }

            // Load image from drawable based on the city
            val imageResId = intent.getIntExtra("imageResId", 0)
            val imageViewCity = findViewById<ImageView>(R.id.imageViewCity)
            imageViewCity.setImageResource(imageResId)
        }
    }

    // Open WeatherActivity with city name and image resource ID as extras
    private fun openWeatherActivity(cityName: String, imageResId: Int) {
        val intent = Intent(this, WeatherActivity::class.java).apply {
            putExtra("CITY_NAME", cityName)
            putExtra("imageResId", imageResId)
        }
        startActivity(intent)
    }
}
