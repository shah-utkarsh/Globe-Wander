// Author: Raj Patel
package com.example.globewander.Adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RatingBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.globewander.R
import com.example.globewander.models.Rating

// Adapter for RecyclerView that displays a list of user ratings. It handles binding rating data to the views.
class RatingAdapter(private val blogs: List<Rating>, private val onClick: (Rating) -> Unit) :
    RecyclerView.Adapter<RatingAdapter.RatingViewHolder>() {

    // ViewHolder class for each rating item, holding and binding views.
    class RatingViewHolder(val view: View) : RecyclerView.ViewHolder(view) {

        // Sets user name, rating, and review text to the view
        fun bind(rating: Rating, onClick: (Rating) -> Unit) {
            view.findViewById<TextView>(R.id.userNameTextView).text = rating.userName
            val ratingBar = itemView.findViewById<RatingBar>(R.id.ratingBar)
            ratingBar.scaleX = 0.5f
            ratingBar.scaleY = 0.5f
            ratingBar.rating = rating.rating.toFloat()
            view.findViewById<TextView>(R.id.reviewTextView).text = rating.review

            // Configures an onClick listener for each item.
            view.setOnClickListener { onClick(rating) }
        }
    }

    // Inflates the layout for each rating item and returns a ViewHolder.
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RatingViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val itemView = layoutInflater.inflate(R.layout.rating_item, parent, false)
        return RatingViewHolder(itemView)
    }


    // Binds each rating item to a ViewHolder.
    override fun onBindViewHolder(holder: RatingViewHolder, position: Int) {
        holder.bind(blogs[position], onClick)
    }

    // Returns the total number of rating items in the list.
    override fun getItemCount() = blogs.size
}

