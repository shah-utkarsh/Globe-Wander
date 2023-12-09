// Author : Shah Utkarsh
package com.example.globewander

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.globewander.models.Blog
import com.google.firebase.database.*

// Activity to display each blog
class BlogDetailActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_blog_detail)

        val blogId = intent.getStringExtra("BLOG_ID")

        val titleTextView = findViewById<TextView>(R.id.blogTitleDetailTextView)
        val authorTextView = findViewById<TextView>(R.id.authorDetailTextView)
        val cityTextView = findViewById<TextView>(R.id.cityDetailTextView)
        val contentTextView = findViewById<TextView>(R.id.blogContentDetailTextView)

        if (blogId != null) {
            val databaseReference = FirebaseDatabase.getInstance().getReference("Blogs")
            databaseReference.child(blogId).addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    val blog = dataSnapshot.getValue(Blog::class.java)
                    titleTextView.text = blog?.title
                    authorTextView.text = blog?.authorName
                    cityTextView.text = blog?.city
                    contentTextView.text = blog?.content
                }

                override fun onCancelled(databaseError: DatabaseError) {
                }
            })
        } else {
        }

        // Find the "Back to All Blogs" button by its ID
        val backButton = findViewById<Button>(R.id.backToAllBlogsButton)

        // Set an OnClickListener for the button
        backButton.setOnClickListener {
            // Create an Intent to navigate back to the AllBlogsActivity
            val intent = Intent(this, AllBlogsActivity::class.java)
            startActivity(intent)
        }

    }
}
