package com.example.globewander

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class AcceptedRequestsActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: AcceptedRequestAdapter
    private var acceptedRequests: MutableList<FriendRequest> = mutableListOf()

    // Firebase reference
    private lateinit var database: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_accepted_requests)

        recyclerView = findViewById(R.id.acceptedRequestsRecyclerView)
        adapter = AcceptedRequestAdapter(this, acceptedRequests)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter

        val databaseUrl = "https://globewander-6d4ec-default-rtdb.firebaseio.com/"
        database = FirebaseDatabase.getInstance(databaseUrl).getReference()

        fetchAcceptedRequests()
    }

    private fun fetchAcceptedRequests() {
        val currentUserId = FirebaseAuth.getInstance().currentUser?.uid
        currentUserId?.let { userId ->
            database.child("UserActions").child(userId).child("confirmed")
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        val userIds = snapshot.children.mapNotNull { it.value as? String }
                        fetchUserDetails(userIds)
                    }

                    override fun onCancelled(error: DatabaseError) {
                        // Handle possible errors
                    }
                })
        }
    }

    private fun fetchUserDetails(userIds: List<String>) {
        userIds.forEach { userId ->
            database.child("Users").child(userId)
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        val user = snapshot.getValue(User::class.java)
                        user?.let {
                            val friendRequest = FriendRequest(it.userId, "${it.firstName} ${it.lastName}", "", "")
                            acceptedRequests.add(friendRequest)
                            adapter.notifyDataSetChanged()
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                        // Handle possible errors
                    }
                })
        }
    }
}
