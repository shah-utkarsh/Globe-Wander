package com.example.globewander

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.globewander.adapters.DestinationAdapter
import com.example.globewander.models.DestinationCard

// Activity displaying a list of destinations using a RecyclerView
class DestinationActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_destination)

        // Get reference to the RecyclerView in the layout
        val recyclerView: RecyclerView = findViewById(R.id.recyclerViewDestinations)

        // Set a grid layout manager with two columns for the RecyclerView
        recyclerView.layoutManager = GridLayoutManager(this, 2)

        // Create a list of DestinationCard objects with city names and image resource IDs
        val destinations = listOf(
            DestinationCard("Paris", R.drawable.paris),
            DestinationCard("Tokyo", R.drawable.tokyo),
            DestinationCard("Singapore", R.drawable.singapore),
            DestinationCard("London", R.drawable.london),
            DestinationCard("Dubai", R.drawable.dubai),
            DestinationCard("Italy", R.drawable.italy)
        )

        // Create an adapter for the RecyclerView, passing the list of destinations and an item click callback
        val adapter = DestinationAdapter(this, destinations) { clickedDestination ->
            Log.d("DestinationActivity", "Clicked on: ${clickedDestination}")
            // Show details of the clicked destination
            showCityDetails(clickedDestination)
        }

        // Set the adapter for the RecyclerView
        recyclerView.adapter = adapter
    }

    // Open CityDetailsActivity for the selected destination
    private fun showCityDetails(destinationCard: DestinationCard) {
        Log.d("DestinationActivity", "Opening CityDetailsActivity for: ${destinationCard.title}")

        // Redirect to CityDetailsActivity and pass the city name and image resource id as extras
        val intent = Intent(this, CityDetailsActivity::class.java)
        intent.putExtra("cityName", destinationCard.title)
        intent.putExtra("imageResId", destinationCard.imageResId)
        startActivity(intent)
    }
}
