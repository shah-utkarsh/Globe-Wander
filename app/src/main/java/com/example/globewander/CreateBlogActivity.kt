// Author : Shah Utkarsh
package com.example.globewander

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.globewander.models.Blog
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

// Activity to create Blog
class CreateBlogActivity : AppCompatActivity() {
    private lateinit var databaseReference: DatabaseReference
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_blog)

        // Initialize Firebase Database and Authentication references
        databaseReference = FirebaseDatabase.getInstance().getReference("Blogs")
        auth = FirebaseAuth.getInstance()

        // UI components
        val titleEditText = findViewById<EditText>(R.id.titleEditText)
        val contentEditText = findViewById<EditText>(R.id.contentEditText)
        val citySpinner = findViewById<Spinner>(R.id.citySpinner)
        val submitButton = findViewById<Button>(R.id.submitBlogButton)

        // Spinner setup
        val cities = arrayOf("Paris", "Tokyo", "Singapore", "Rome", "Dubai", "London")
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, cities)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        citySpinner.adapter = adapter

        // Submit button click listener
        submitButton.setOnClickListener {
            val title = titleEditText.text.toString().trim()
            val content = contentEditText.text.toString().trim()
            val city = citySpinner.selectedItem.toString()

            if (title.isNotEmpty() && content.isNotEmpty() && city.isNotEmpty()) {
                val currentUser = auth.currentUser
                if (currentUser != null) {
                    val authorId = currentUser.uid

                    FirebaseDatabase.getInstance().getReference("Users")
                        .child(authorId)
                        .addListenerForSingleValueEvent(object : ValueEventListener {
                            override fun onDataChange(dataSnapshot: DataSnapshot) {
                                val userProfile = dataSnapshot.getValue(UserProfile::class.java)
                                val authorName = "${userProfile?.firstName} ${userProfile?.lastName}"

                                val blogId = databaseReference.push().key
                                val blog = Blog(blogId ?: "", title, content, city, authorId, authorName)
                                Log.d("Blog reuqet",blog.toString() )

                                blogId?.let {
                                    databaseReference.child(it).setValue(blog).addOnCompleteListener { task ->
                                        if (task.isSuccessful) {
                                            Toast.makeText(this@CreateBlogActivity, "Blog added successfully", Toast.LENGTH_SHORT).show()
                                            startActivity(Intent(this@CreateBlogActivity, AllBlogsActivity::class.java))
                                            finish()
                                        } else {
                                            Toast.makeText(this@CreateBlogActivity, "Failed to add blog", Toast.LENGTH_SHORT).show()
                                        }
                                    }
                                }
                            }

                            override fun onCancelled(databaseError: DatabaseError) {
                                Toast.makeText(this@CreateBlogActivity, "Error: ${databaseError.message}", Toast.LENGTH_SHORT).show()
                            }
                        })
                } else {
                    Toast.makeText(this, "User not authenticated", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
