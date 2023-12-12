package com.example.completionist.HomePage

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.completionist.Friend
import com.example.completionist.R

class PartyAdapter (private val friendList: MutableList<Friend>):
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val VIEW_TYPE_FRIEND = 1
    private val VIEW_TYPE_EMPTY = 2


    inner class FriendViewHolder(item: View) : RecyclerView.ViewHolder(item) {
        val friendName = item.findViewById<TextView>(R.id.party_member_name)
        val friendLevel = item.findViewById<TextView>(R.id.party_member_level)
        val friendStreak = item.findViewById<TextView>(R.id.party_member_streak)
        val friendConsistency = item.findViewById<TextView>(R.id.party_member_consistency)
    }

    inner class EmptyViewHolder(item: View) : RecyclerView.ViewHolder(item) {
        // Bind data to empty view layout
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        Log.v("HomePage", "ViewHolder being created with viewType $viewType")

        return when (viewType) {
            VIEW_TYPE_FRIEND -> {
                Log.v("HomePage", " Friend Item View being inflated")
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.home_party_item_layout, parent, false)
                FriendViewHolder(view)
            }

            else -> {
                Log.v("HomePage", "Empty Friend View being inflated")
                val emptyView = LayoutInflater.from(parent.context)
                    .inflate(R.layout.home_party_empty_view_layout, parent, false)
                EmptyViewHolder(emptyView)
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is FriendViewHolder) {
            val friend = friendList[position]
            holder.friendName?.text =
                friend.username // Assuming questName is a property in your Quest class
            holder.friendLevel?.text =
                "LVL: " + friend.level.toString() // Assuming questName is a property in your Quest class
            holder.friendStreak?.text =
                friend.streak.toString() + " Days"  // Assuming questName is a property in your Quest class
            holder.friendConsistency?.text =
                friend.consistency.toString() + "%"  // Assuming questName is a property in your Quest class
        }
    }

    override fun getItemCount(): Int {
        Log.v("HomePage", "Getting item count ${friendList.isEmpty()}")
        return if (friendList.isEmpty()) {
            1 // Return 1 for the empty view if questList is empty
        } else {
            friendList.size
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if (friendList.isEmpty()) {
            VIEW_TYPE_EMPTY // Return empty view type if questList is empty
        } else {
            VIEW_TYPE_FRIEND // Return quest view type for regular quests
        }
    }
}
