package com.example.globewander.models

// Data class representing a destination
data class Destination(
    val city: String,
    val advice: String,
    val attractions: List<Attraction>,
    val language: String,
    val food: List<Food>,
    val budget: Double,
    val population: Long
) {
    // No-argument constructor for Firebase deserialization
    constructor() : this("", "", listOf(), "", listOf(), 0.0, 0)
}

// Data class representing an attraction
data class Attraction(
    val name: String,
    val description: String
) {
    // No-argument constructor for Firebase deserialization
    constructor() : this("", "")
}

// Data class representing food information
data class Food(
    val name: String,
    val description: String
) {
    // No-argument constructor for Firebase deserialization
    constructor() : this("", "")
}
