// Author : Raj Patel
package com.example.globewander

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.globewander.Adapters.ItineraryAdapter
import com.example.globewander.Adapters.RatingAdapter
import com.example.globewander.models.Rating
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class AllRatingsActivity: AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_all_ratings)

        val recyclerView = findViewById<RecyclerView>(R.id.ratingsRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)

        // Fetch blogs from Firebase
        val databaseReference = FirebaseDatabase.getInstance().getReference("Ratings")
        databaseReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val ratings = mutableListOf<Rating>()
                for (postSnapshot in dataSnapshot.children) {
                    val rating = postSnapshot.getValue(Rating::class.java)
                    rating?.let { ratings.add(it) }
                }

                // Set up the adapter
                recyclerView.adapter = RatingAdapter(ratings) { rating ->
                    // Handle itinerary click
                    val intent = Intent(this@AllRatingsActivity, AllRatingsActivity::class.java)
                    intent.putExtra("ITINERARY_ID", rating.id) // Pass itinerary ID or entire object
                    startActivity(intent)
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Handle possible errors
            }
        })

        val fab = findViewById<FloatingActionButton>(R.id.addRatingFab)
        fab.setOnClickListener {
            // Navigate to the CreateBlogActivity
            startActivity(Intent(this, AddUserRatingActivity::class.java))
        }
    }
}
