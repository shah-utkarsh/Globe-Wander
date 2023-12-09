package com.example.globewander

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class FriendRequestAdapter(private val friendRequests: MutableList<FriendRequest>) :
    RecyclerView.Adapter<FriendRequestAdapter.ViewHolder>() {

    private val currentUserId = FirebaseAuth.getInstance().currentUser?.uid
    private val databaseRef = FirebaseDatabase.getInstance().reference

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val friendName: TextView = view.findViewById(R.id.friendName)
        val mutualFriendsCount: TextView = view.findViewById(R.id.mutualFriendsCount)
        val buttonConfirm: Button = view.findViewById(R.id.buttonConfirm)
        val buttonDelete: Button = view.findViewById(R.id.buttonDelete)
        val buttonStatus: Button = view.findViewById(R.id.buttonStatus)

        fun bind(friendRequest: FriendRequest, onConfirm: (Int) -> Unit, onDelete: (Int) -> Unit) {
            friendName.text = friendRequest.name
            mutualFriendsCount.text = friendRequest.mutualFriends

            // Set visibility based on status
            when (friendRequest.status) {
                "pending" -> {
                    buttonConfirm.visibility = View.VISIBLE
                    buttonDelete.visibility = View.VISIBLE
                    buttonStatus.visibility = View.GONE
                }
                "confirmed", "deleted" -> {
                    buttonConfirm.visibility = View.GONE
                    buttonDelete.visibility = View.GONE
                    buttonStatus.visibility = View.VISIBLE
                    buttonStatus.text = friendRequest.status.capitalize()
                }
            }

            // Attach click listeners
            buttonConfirm.setOnClickListener { onConfirm(adapterPosition) }
            buttonDelete.setOnClickListener { onDelete(adapterPosition) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_friend_request, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val friendRequest = friendRequests[position]
        holder.bind(friendRequest,
            onConfirm = { pos -> handleConfirm(pos) },
            onDelete = { pos -> handleDelete(pos) }
        )
    }

    override fun getItemCount() = friendRequests.size

    private fun handleConfirm(position: Int) {
        val friendRequest = friendRequests[position]
        updateFirebase("confirmed", friendRequest)
        friendRequests.removeAt(position)
        notifyItemRemoved(position)
    }

    private fun handleDelete(position: Int) {
        val friendRequest = friendRequests[position]
        updateFirebase("deleted", friendRequest)
        friendRequests.removeAt(position)
        notifyItemRemoved(position)
    }

    private fun updateFirebase(status: String, friendRequest: FriendRequest) {
        currentUserId?.let { userId ->
            databaseRef.child("UserActions").child(userId).child(status).push()
                .setValue(friendRequest.userId)
        }
    }
}