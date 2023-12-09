// Author : Shah Utkarsh

package com.example.globewander.Adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.globewander.R
import com.example.globewander.models.Blog

// Adapter for RecyclerView that displays a list of blog entries. It binds the blog data to the views.
class BlogAdapter(private var blogs: List<Blog>, private val onClick: (Blog) -> Unit) :
    RecyclerView.Adapter<BlogAdapter.BlogViewHolder>() {

    // Updates the dataset of the adapter and notifies that the data has changed.
    fun updateData(newBlogs: List<Blog>) {
        blogs = newBlogs
        notifyDataSetChanged()
    }

    // ViewHolder class to hold and bind views for each blog item.
    class BlogViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
        val blogTitleTextView: TextView = view.findViewById(R.id.blogTitleTextView)
        val blogSnippetTextView: TextView = view.findViewById(R.id.blogSnippetTextView)

        // Binds the blog data to the view elements.
        fun bind(blog: Blog, onClick: (Blog) -> Unit) {
            blogTitleTextView.text = blog.title
            blogSnippetTextView.text = blog.content.take(50) + "..." // Shows first 100 chars


            // Sets up a click listener for the blog item.
            view.setOnClickListener { onClick(blog) }
        }
    }

    // Inflates the blog item layout and returns the ViewHolder.
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BlogViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val itemView = layoutInflater.inflate(R.layout.item_blog_card, parent, false)
        return BlogViewHolder(itemView)
    }

    // Bind the blog data to the holder, setting the title and a snippet of the blog's content.
    override fun onBindViewHolder(holder: BlogViewHolder, position: Int) {
        holder.bind(blogs[position], onClick)
    }

    // Returns the total number of blog items in the list.
    override fun getItemCount() = blogs.size
}
