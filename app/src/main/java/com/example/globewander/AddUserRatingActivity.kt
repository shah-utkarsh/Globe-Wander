// Author : Raj Patel
package com.example.globewander

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.globewander.models.Rating
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase


// Activity for adding a user rating for a destination. It includes input fields for user name, rating, and review.
class AddUserRatingActivity : AppCompatActivity() {
    private lateinit var databaseReference: DatabaseReference

    // Initializes the activity, setting up the layout and Firebase database reference.
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_user_rating)

        // Initialize Firebase Database reference
        databaseReference = FirebaseDatabase.getInstance().getReference("Ratings")


        // UI components: EditTexts for user name and review, Spinner for rating, and a submit button.
        val userNameEditText = findViewById<EditText>(R.id.userNameEditText)
        val reviewEditText = findViewById<EditText>(R.id.reviewEditText)
        val submitButton = findViewById<Button>(R.id.submitRatingButton)

        val ratingSpinner = findViewById<Spinner>(R.id.ratingSpinner)
        //  Paris, Tokyo, Singapore, Italy, Dubai and London
        val ratings = arrayOf("Select Rating", "5", "4", "3", "2", "1")
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, ratings)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        ratingSpinner.adapter = adapter


        // OnClickListener for the submit button to handle user rating submission.
        submitButton.setOnClickListener {
            Log.d("AddRatingActivity", "Submit button clicked")
            val userName = userNameEditText.text.toString().trim()
            val review = reviewEditText.text.toString().trim()
            val rating = ratingSpinner.selectedItem.toString()

            // Validation for user inputs and creating a Rating object if valid.
            if (userName.isNotEmpty() && review.isNotEmpty() && rating.isNotEmpty() && rating != "Select Rating") {
                val ratingId = databaseReference.push().key
                val rating =
                    ratingId?.let { it1 -> Rating(it1, userName, rating, review) }

                // Submit to Firebase
                ratingId?.let {
                    databaseReference.child(it).setValue(rating).addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            Toast.makeText(this, "Rating added successfully", Toast.LENGTH_SHORT).show()
                            val intent = Intent(this, AllRatingsActivity::class.java)
                            startActivity(intent)
                        } else {
                            Toast.makeText(this, "Failed to add rating", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            } else {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
