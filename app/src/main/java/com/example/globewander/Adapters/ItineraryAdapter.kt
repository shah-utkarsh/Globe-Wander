// Author: Raj Patel
package com.example.globewander.Adapters

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.globewander.R
import com.example.globewander.models.Itinerary

class ItineraryAdapter(private val itineraries: List<Itinerary>, private val onClick: (Itinerary) -> Unit) :
    RecyclerView.Adapter<ItineraryAdapter.ItineraryViewHolder>() {

    // ViewHolder class for each itinerary item.
    class ItineraryViewHolder(val view: View) : RecyclerView.ViewHolder(view) {

        // Bind itinerary data to the view.
        fun bind(itinerary: Itinerary, onClick: (Itinerary) -> Unit) {
            Log.d("image", itinerary.image)

            // Map itinerary image string to drawable resource
            val image = when (itinerary.image) {
                "dubai1" -> R.drawable.dubai1
                "paris1" -> R.drawable.paris1
                "tokyo1" -> R.drawable.tokyo1
                "paris2" -> R.drawable.paris2
                "singapore1" -> R.drawable.singapore1
                "singapore2" -> R.drawable.singapore2
                else -> null
            }
            // Set image if available.
            if (image != null) {
                view.findViewById<ImageView>(R.id.itineraryImageView).setImageResource(image)
            }
            view.findViewById<TextView>(R.id.itineraryTitleTextView).text = itinerary.title
            view.findViewById<TextView>(R.id.itineraryCityTextView).text = itinerary.city
            view.findViewById<TextView>(R.id.itineraryDaysTextView).text = itinerary.days + " days"

            view.setOnClickListener { onClick(itinerary) }
        }
    }

    // Create new views for each item in the RecyclerView.
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItineraryViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val itemView = layoutInflater.inflate(R.layout.itinerary_item, parent, false)
        return ItineraryViewHolder(itemView)
    }

    // Bind data to each view holder.
    override fun onBindViewHolder(holder: ItineraryViewHolder, position: Int) {
        holder.bind(itineraries[position], onClick)
    }


    // Return the size of the dataset.
    override fun getItemCount() = itineraries.size
}