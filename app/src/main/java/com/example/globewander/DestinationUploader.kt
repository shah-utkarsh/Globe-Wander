package com.example.globewander

import com.example.globewander.models.Destination
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener

class DestinationUploader(private val database: DatabaseReference) {

    fun uploadDestination(destination: Destination) {
        val destinationId = destination.city // Generate a unique key for the destination
        destinationId?.let {
            database.child(it).setValue(destination)
        }
    }

    interface DestinationCallback {
        fun onDestinationLoaded(destination: Destination?)
    }

    fun getDestinationByCity(cityName: String, callback: DestinationCallback) {
        val query = database.orderByChild("city").equalTo(cityName)

        query.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    // Assuming there is only one destination for a given city
                    val destination = dataSnapshot.children.first().getValue(Destination::class.java)
                    callback.onDestinationLoaded(destination)
                } else {
                    // No matching destination found
                    callback.onDestinationLoaded(null)
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Handle errors if needed
                callback.onDestinationLoaded(null)
            }
        })
    }
}
