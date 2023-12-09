// Author : Raj Patel
package com.example.globewander

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.globewander.models.Itinerary
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.InputStream

// Activity to display each Itinerary
class ItineraryDetailActivity : AppCompatActivity() {

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_itinerary_detail)

        val itineraryId = intent.getStringExtra("ITINERARY_ID")

        if (itineraryId != null) {
            // Read data from the raw resource file
            val inputStream: InputStream = resources.openRawResource(R.raw.itineraries)
            val json = inputStream.bufferedReader().use { it.readText() }

            // Convert JSON to a list of Itinerary objects using Gson
            val itinerariesType = object : TypeToken<List<Itinerary>>() {}.type
            val itineraries = Gson().fromJson<List<Itinerary>>(json, itinerariesType)

            // Find the itinerary by ID
            val itinerary = itineraries.find { it.id == itineraryId }

            itinerary?.let {
                val image = when (it.image) {
                    "dubai1" -> R.drawable.dubai1
                    "paris1" -> R.drawable.paris1
                    "tokyo1" -> R.drawable.tokyo1
                    "paris2" -> R.drawable.paris2
                    "singapore1" -> R.drawable.singapore1
                    "singapore2" -> R.drawable.singapore2
                    else -> null
                }
                if (image != null) {
                    findViewById<ImageView>(R.id.itineraryImageView).setImageResource(image)
                }
                findViewById<TextView>(R.id.itineraryTitleDetailTextView).text = it.title
                findViewById<TextView>(R.id.itineraryCityDetailTextView).text = it.city
                findViewById<TextView>(R.id.itineraryDaysDetailTextView).text = it.days + " days"
                findViewById<TextView>(R.id.itineraryDetailsTextView).text = it.details
                // Set other fields as well if available
            }
        } else {
            // Handle case where itineraryId is null
        }

        // Find the "Back to All Itineraries" button by its ID
        val backButton = findViewById<Button>(R.id.backToAllItinerariesButton)

        // Set an OnClickListener for the button
        backButton.setOnClickListener {
            // Create an Intent to navigate back to the AllItinerariesActivity
            val intent = Intent(this, AllItinerariesActivity::class.java)
            startActivity(intent)
        }
    }
}
