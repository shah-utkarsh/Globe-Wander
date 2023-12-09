// Author : Shah Utkarsh
package com.example.globewander

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.globewander.Adapters.BlogAdapter
import com.example.globewander.models.Blog
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

// Activity to display a list of all blogs. Includes a filter feature based on city.
class AllBlogsActivity: AppCompatActivity() {
    private val allBlogs = mutableListOf<Blog>() // Define the list at the class level
    private lateinit var blogAdapter: BlogAdapter

    // Initializes the activity, sets up RecyclerView and its adapter, and handles Firebase data fetching.
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_all_blogs)

        val recyclerView = findViewById<RecyclerView>(R.id.blogsRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)

        // Initialize the blog adapter
        blogAdapter = BlogAdapter(listOf()) { blog ->
            val intent = Intent(this, BlogDetailActivity::class.java)
            intent.putExtra("BLOG_ID", blog.id)
            startActivity(intent)
        }
        recyclerView.adapter = blogAdapter

        // Spinner Setup for city-based blog filtering
        val cityFilterSpinner = findViewById<Spinner>(R.id.cityFilterSpinner)
        val cities = arrayOf("All", "Paris", "Tokyo", "Singapore", "Rome", "Dubai", "London")
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, cities)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        cityFilterSpinner.adapter = adapter

        // Firebase database listener to fetch and update blog data.
        val databaseReference = FirebaseDatabase.getInstance().getReference("Blogs")
        databaseReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                allBlogs.clear() // Clear existing data
                for (postSnapshot in dataSnapshot.children) {
                    val blog = postSnapshot.getValue(Blog::class.java)
                    blog?.let { allBlogs.add(it) }
                }

                filterBlogs(cityFilterSpinner.selectedItem.toString())
            }


            override fun onCancelled(databaseError: DatabaseError) {
            }
        })

        // Filter blogs based on selected city
        cityFilterSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                val selectedCity = parent.getItemAtPosition(position).toString()
                filterBlogs(selectedCity)
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                filterBlogs("All")
            }
        }

        val fab = findViewById<FloatingActionButton>(R.id.addBlogFab)
        fab.setOnClickListener {
            startActivity(Intent(this, CreateBlogActivity::class.java))
        }
    }

    // Method to filter blogs based on the selected city and update the RecyclerView.
    private fun filterBlogs(selectedCity: String) {
        Log.d("Selected City", selectedCity)
        val filteredBlogs = if (selectedCity == "All") allBlogs else allBlogs.filter { it.city == selectedCity }
        Log.d("Filtered Blogs", filteredBlogs.toString())
        blogAdapter.updateData(filteredBlogs)
    }
}
