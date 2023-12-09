// Author : Raj Patel
package com.example.globewander

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.globewander.Adapters.ItineraryAdapter
import com.example.globewander.models.Itinerary
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.InputStream

// Activity to display all Itineraries
class AllItinerariesActivity : AppCompatActivity() {
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_all_itineraries)

        val recyclerView = findViewById<RecyclerView>(R.id.itinerariesRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)

        // Read data from the raw resource file
        val inputStream: InputStream = resources.openRawResource(R.raw.itineraries)
        val json = inputStream.bufferedReader().use { it.readText() }

        // Convert JSON to a list of Itinerary objects using Gson
        val itinerariesType = object : TypeToken<List<Itinerary>>() {}.type
        val itineraries: List<Itinerary> = Gson().fromJson(json, itinerariesType)

        // Set up the adapter
        recyclerView.adapter = ItineraryAdapter(itineraries) { itinerary ->
            // Handle itinerary click
            val intent = Intent(this@AllItinerariesActivity, ItineraryDetailActivity::class.java)
            intent.putExtra("ITINERARY_ID", itinerary.id) // Pass itinerary ID or entire object
            startActivity(intent)
        }

        val allItinerariesButton = findViewById<Button>(R.id.allItinerariesButton)

        // Set an OnClickListener
        allItinerariesButton.setOnClickListener {
            // Create an Intent to start AllBlogsActivity
            val intent = Intent(this, ItineraryBuilder::class.java)
            startActivity(intent)
        }
    }
}
