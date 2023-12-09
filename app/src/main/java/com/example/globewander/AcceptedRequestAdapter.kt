package com.example.globewander

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class AcceptedRequestAdapter(
    private val context: Context,
    private val acceptedRequests: List<FriendRequest>
) : RecyclerView.Adapter<AcceptedRequestAdapter.ViewHolder>() {

    // ViewHolder class for each item in the RecyclerView
    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val friendName: TextView = view.findViewById(R.id.friend_name)
        // Add any other UI elements you want to bind data to

        // Bind the data to the views
        fun bind(friendRequest: FriendRequest) {
            friendName.text = friendRequest.name
            // Set other data elements to your views here
        }
    }

    // Create new views (invoked by the layout manager)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        // Inflate the custom layout
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_confirmed_friend_request, parent, false)

        // Return a new ViewHolder
        return ViewHolder(view)
    }

    // Replace the contents of a view (invoked by the layout manager)
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        // Get element from your dataset at this position
        val friendRequest = acceptedRequests[position]

        // Bind the data to the holder
        holder.bind(friendRequest)

        // Set the click listener
        holder.itemView.setOnClickListener {
            val intent = Intent(context, PersonDetailActivity::class.java).apply {
                putExtra("USER_ID", friendRequest.userId) // Assuming FriendRequest has a userId field
                putExtra("NAME", friendRequest.name) // Assuming FriendRequest has a name field
                // Add any other data elements you want to pass to the detail activity
            }
            context.startActivity(intent)
        }
    }

    // Return the size of your dataset (invoked by the layout manager)
    override fun getItemCount() = acceptedRequests.size
}
