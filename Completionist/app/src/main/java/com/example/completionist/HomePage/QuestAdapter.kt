package com.example.completionist.HomePage

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.completionist.R

class QuestAdapter(private val questList: MutableList<DummyQuest>) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

        private val VIEW_TYPE_QUEST = 1
        private val VIEW_TYPE_EMPTY = 2


    inner class QuestViewHolder(item: View) : RecyclerView.ViewHolder(item) {
        val questName = item.findViewById<TextView>(R.id.QuestNameHome)
        val complete = item.findViewById<CheckBox>(R.id.QuestCompleteHome)
    }

    inner class EmptyViewHolder(item: View) : RecyclerView.ViewHolder(item) {
        // Bind data to empty view layout
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        Log.v("HomePage", "ViewHolder being created with viewType $viewType")

        return when (viewType) {
            VIEW_TYPE_QUEST -> {
                Log.v("HomePage", "Item View being inflated")
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.home_quest_item_layout, parent, false)
                QuestViewHolder(view)
            }
            else -> {
                Log.v("HomePage", "Empty View being inflated")
                val emptyView = LayoutInflater.from(parent.context)
                    .inflate(R.layout.home_quest_empty_view_layout, parent, false)
                EmptyViewHolder(emptyView)
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is QuestViewHolder) {
            val quest = questList[position]
            holder.questName.text = quest.questName // Assuming questName is a property in your Quest class
            holder.complete.isChecked = quest.isComplete // Assuming isComplete is a property in your Quest class
        }
    }

    override fun getItemCount(): Int {
        Log.v("HomePage", "Getting item count ${questList.isEmpty()}")
        return if (questList.isEmpty()) {
            1 // Return 1 for the empty view if questList is empty
        } else {
            questList.size
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if (questList.isEmpty()) {
            VIEW_TYPE_EMPTY // Return empty view type if questList is empty
        } else {
            VIEW_TYPE_QUEST // Return quest view type for regular quests
        }
    }

}