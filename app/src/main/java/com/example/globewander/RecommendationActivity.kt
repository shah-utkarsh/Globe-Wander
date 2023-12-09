package com.example.globewander

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.view.View
import android.widget.Button
import android.widget.TextView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class RecommendationActivity : AppCompatActivity() {

    private lateinit var friendsRecyclerView: RecyclerView
    private lateinit var database: DatabaseReference
    private lateinit var adapter: FriendRequestAdapter
    private var friendRequests: MutableList<FriendRequest> = mutableListOf()
    private val processedRequests = mutableSetOf<String>()
    private lateinit var noMoreRecommendationsTextView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_friends)
        val viewAcceptedRequestsButton: Button = findViewById(R.id.viewAcceptedRequestsButton)
        viewAcceptedRequestsButton.setOnClickListener {
            val intent = Intent(this, AcceptedRequestsActivity::class.java)
            startActivity(intent)
        }

        friendsRecyclerView = findViewById(R.id.friendsRecyclerView)
        adapter = FriendRequestAdapter(friendRequests)
        friendsRecyclerView.layoutManager = LinearLayoutManager(this)
        friendsRecyclerView.adapter = adapter

        noMoreRecommendationsTextView = findViewById(R.id.noMoreRecommendationsTextView)

        val databaseUrl = "https://globewander-6d4ec-default-rtdb.firebaseio.com/"
        database = FirebaseDatabase.getInstance(databaseUrl).getReference()

        fetchProcessedRequests {
            fetchCurrentUserPreferences()
        }
    }

    private fun fetchProcessedRequests(onComplete: () -> Unit) {
        val currentUserId = FirebaseAuth.getInstance().currentUser?.uid
        currentUserId?.let { userId ->
            database.child("UserActions").child(userId)
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        snapshot.children.forEach { actionSnapshot ->
                            actionSnapshot.children.forEach { requestSnapshot ->
                                val processedUserId = requestSnapshot.value.toString()
                                Log.d("RecommendationActivity", "Processed User ID: $processedUserId")
                                processedRequests.add(processedUserId)
                            }
                        }
                        onComplete()
                    }

                    override fun onCancelled(error: DatabaseError) {
                        Log.e("RecommendationActivity", "Error fetching processed requests", error.toException())
                        onComplete()
                    }
                })
        } ?: onComplete()
    }

    private fun fetchCurrentUserPreferences() {
        val currentUserId = FirebaseAuth.getInstance().currentUser?.uid
        currentUserId?.let { userId ->
            database.child("UserPreferences").child(userId)
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        val currentUserPreferences = snapshot.getValue(UserPreferences::class.java)
                        currentUserPreferences?.let {
                            fetchAllUserPreferences(it)
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                        // Handle possible errors
                    }
                })
        }
    }

    private fun fetchAllUserPreferences(currentUserPreferences: UserPreferences) {
        database.child("UserPreferences")
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val matchedUserIds = mutableListOf<String>()
                    for (userSnapshot in snapshot.children) {
                        val userPreferences = userSnapshot.getValue(UserPreferences::class.java)
                        if (userPreferences != null && isPotentialFriend(currentUserPreferences, userPreferences)) {
                            matchedUserIds.add(userSnapshot.key!!)
                        }
                    }
                    matchedUserIds.forEach { userId ->
                        if (!processedRequests.contains(userId)) {
                            convertToFriendRequest(userId) { friendRequest ->
                                friendRequest?.let {
                                    friendRequests.add(it)
                                    adapter.notifyDataSetChanged()

                                    // Call the function to show/hide the message
                                    showNoMoreRecommendationsMessage()
                                }
                            }
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    // Handle possible errors
                }
            })
    }

    private fun convertToFriendRequest(userId: String, completion: (FriendRequest?) -> Unit) {
        if (processedRequests.contains(userId)) {
            Log.d("RecommendationActivity", "Skipping processed request: $userId")
            completion(null)
            return
        }

        database.child("Users").child(userId)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val user = snapshot.getValue(User::class.java)
                    user?.let {
                        // Create FriendRequest object using user details
                        val friendRequest = FriendRequest(userId, it.firstName + " " + it.lastName, "Details", "5") // Example
                        completion(friendRequest)
                    } ?: run {
                        completion(null)
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    completion(null)
                }
            })
    }

    private fun isPotentialFriend(currentUserPreferences: UserPreferences, otherUserPreferences: UserPreferences): Boolean {
        val countriesIntersect = currentUserPreferences.selectedCountries.any {
            otherUserPreferences.selectedCountries.contains(it)
        }
        val interestsIntersect = currentUserPreferences.interests.any {
            otherUserPreferences.interests.contains(it)
        }
        return countriesIntersect && interestsIntersect
    }

    private fun showNoMoreRecommendationsMessage() {
        // Check if there are no friend requests to show
        if (friendRequests.isEmpty()) {
            // Display the "No more recommendations now" message
            noMoreRecommendationsTextView.visibility = View.VISIBLE
        } else {
            // Hide the message if there are friend requests
            noMoreRecommendationsTextView.visibility = View.GONE
        }
    }
}
