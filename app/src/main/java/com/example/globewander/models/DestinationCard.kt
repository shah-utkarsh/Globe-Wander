package com.example.globewander.models

import java.io.Serializable

// Data class representing a simplified version of a destination for RecyclerView cards
data class DestinationCard(
    val title: String,    // Title or name of the destination
    val imageResId: Int    // Resource ID of the image associated with the destination
) : Serializable
