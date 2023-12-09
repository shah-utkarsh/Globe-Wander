package com.example.globewander
data class FriendRequest(
    val userId: String,
    val name: String,
    val details: String,
    val mutualFriends: String,
    var status: String = "pending"
)
