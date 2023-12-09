package com.example.globewander

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ListView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.globewander.adapters.FAQAdapter
import com.example.globewander.models.FAQItem
import com.example.globewander.models.FAQFetch
import com.google.firebase.database.*

// FaqActivity class which extends AppCompatActivity
class FaqActivity : AppCompatActivity() {

    // Declare variables for the database reference, ListView, adapter, FAQ list, and notification helper
    private lateinit var database: DatabaseReference
    private lateinit var faqListView: ListView
    private lateinit var faqAdapter: FAQAdapter
    private val faqFetch = mutableListOf<FAQFetch>()
    private lateinit var notificationHelper: NotificationHelper

    // onCreate method initializes the activity
    @Override
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_faq)

        // Initialize the Firebase database reference to the "FAQs" node
        database = FirebaseDatabase.getInstance().getReference("FAQs")
        // Initialize the notification helper
        notificationHelper = NotificationHelper(this)

        // Get references to the EditText and Button from the layout
        val questionEditText = findViewById<EditText>(R.id.questionEditText)
        val submitButton = findViewById<Button>(R.id.submitButton)

        // Set up the ListView to display FAQs
        setupListView()

        // Set an onClickListener for the submit button to handle question submissions
        submitButton.setOnClickListener {
            val question = questionEditText.text.toString().trim()

            // Check if the question field is empty and show a toast message if it is
            if (question.isEmpty()) {
                Toast.makeText(this, "Please enter a question", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Create a new FAQItem and add it to the Firebase database
            val faqItem = FAQItem(question)
            database.push().setValue(faqItem).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // Show success message and clear the EditText if submission is successful
                    Toast.makeText(this, "Question added successfully", Toast.LENGTH_SHORT).show()
                    questionEditText.text.clear()
                    // Display a notification for the new question
                    notificationHelper.showNotification("New Question Posted", question, R.drawable.ic_notification)
                } else {
                    // Show error message if submission fails
                    Toast.makeText(this, "Failed to add question: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
        // Fetch FAQs from the Firebase database
        fetchFAQs()
    }

    private fun setupListView() {
        faqListView = findViewById(R.id.faqListView)
        faqAdapter = FAQAdapter(this, faqFetch)
        faqListView.adapter = faqAdapter
    }

    // Method to set up the ListView with an adapter
    private fun fetchFAQs() {
        database.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                faqFetch.clear()
                for (faqSnapshot in snapshot.children) {
                    val faqItem = faqSnapshot.getValue(FAQFetch::class.java)
                    faqItem?.let { faqFetch.add(it) }
                }
                faqAdapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                // Show error message if there is a problem fetching FAQs
                Toast.makeText(this@FaqActivity, "Failed to load FAQs: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }
}
