package com.example.globewander

import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.google.firebase.database.FirebaseDatabase

class PersonDetailActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_person_detail)

        val userId = intent.getStringExtra("USER_ID")

        // Initialize UI components
        val textViewName: TextView = findViewById(R.id.textViewName)
        val textViewEmail: TextView = findViewById(R.id.textViewEmail)
        val textViewAddress: TextView = findViewById(R.id.textViewAddress) // Add this to your layout
        val textViewContact: TextView = findViewById(R.id.textViewContact) // Add this to your layout
        val imageViewProfile: ImageView = findViewById(R.id.imageViewProfile)

        // Fetch user details from Firebase
        userId?.let {
            val databaseReference = FirebaseDatabase.getInstance().getReference("Users").child(it)
            databaseReference.get().addOnSuccessListener { dataSnapshot ->
                val user = dataSnapshot.getValue(User::class.java)
                user?.let {
                    textViewName.text = user.firstName + " " + user.lastName
                    textViewEmail.text = user.email
                    textViewAddress.text = user.address
                    textViewContact.text = user.contact
                    Glide.with(this)
                        .load(user.profileImageUrl)
                        .placeholder(R.drawable.ic_launcher_foreground)
                        .into(imageViewProfile)
                }
            }.addOnFailureListener {
            }
        }
    }
}