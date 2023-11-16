package com.example.completionist.HomePage

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.completionist.R
import org.w3c.dom.Text

class PartyAdapter(private val partyList: MutableList<PartyMemberDummy>) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val VIEW_TYPE_PARTY = 1
    private val VIEW_TYPE_EMPTY = 2

    inner class PartyViewHolder(item: View): RecyclerView.ViewHolder(item){
        val memberName = item.findViewById<TextView>(R.id.partymembernamehome)
        val memberExp = item.findViewById<TextView>(R.id.pmhomeExp)
        val memberTasks = item.findViewById<TextView>(R.id.pmhomeTasks)
    }
    inner class EmptyViewHolder(item: View) : RecyclerView.ViewHolder(item) {
        val missingTitle = item.findViewById<TextView>(R.id.missingTitle)
        // Bind data to empty view layout
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            VIEW_TYPE_PARTY -> {
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.home_party_item_layout, parent, false)
                PartyViewHolder(view)
            }
            else -> {
                val emptyView = LayoutInflater.from(parent.context)
                    .inflate(R.layout.home_quest_empty_view_layout, parent, false)
                EmptyViewHolder(emptyView)
            }
        }
    }

    override fun getItemCount(): Int {
        return if (partyList.isEmpty()) {
            1 // Return 1 for the empty view if questList is empty
        } else {
            partyList.size
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if(holder is PartyViewHolder){
            val member = partyList[position]
            holder.memberName.text = member.name
            holder.memberExp.text = "EXP: ${member.exp}"
            holder.memberTasks.text = "Number of Tasks: ${member.tasksToday}"
        }
        if(holder is EmptyViewHolder){
            holder.missingTitle.text = "No Current Party Members"
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if (partyList.isEmpty()) {
            VIEW_TYPE_EMPTY // Return empty view type if questList is empty
        } else {
            VIEW_TYPE_PARTY // Return quest view type for regular quests
        }
    }
}