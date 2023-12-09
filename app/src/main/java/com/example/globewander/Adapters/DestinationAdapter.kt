package com.example.globewander.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.globewander.R
import com.example.globewander.models.DestinationCard

// Adapter class responsible for managing data and creating views for the RecyclerView
class DestinationAdapter(
    private val context: Context,
    private val destinations: List<DestinationCard>,
    private val onItemClick: (DestinationCard) -> Unit
) : RecyclerView.Adapter<DestinationAdapter.ViewHolder>() {

    // Inflates the item view layout and returns a ViewHolder object
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_destination, parent, false)
        return ViewHolder(view)
    }

    // Binds data to the views in the ViewHolder based on the position in the list
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val destination = destinations[position]

        // Set the image resource and title for the current destination
        holder.imageView.setImageResource(destination.imageResId)
        holder.titleTextView.text = destination.title
    }

    // Returns the total number of items in the data set
    override fun getItemCount(): Int {
        return destinations.size
    }

    // ViewHolder class representing each item view in the RecyclerView
    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        // References to the ImageView and TextView in the item view
        val imageView: ImageView = itemView.findViewById(R.id.imageDestination)
        val titleTextView: TextView = itemView.findViewById(R.id.textTitle)

        init {
            // Set a click listener for the entire item view
            itemView.setOnClickListener {
                // Invoke the onItemClick lambda when an item is clicked, passing the corresponding DestinationCard
                onItemClick.invoke(destinations[adapterPosition])
            }
        }
    }
}
