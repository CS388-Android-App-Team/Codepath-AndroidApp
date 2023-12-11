package com.example.completionist.Quests

import android.content.Context
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.PopupMenu
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.completionist.R

class QuestAdapter(private val questList: MutableList<Quest>, private val context: Context) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val VIEW_TYPE_QUEST = 1
    private val VIEW_TYPE_EMPTY = 2

    inner class QuestViewHolder(item: View) : RecyclerView.ViewHolder(item) {
        val questName: TextView? = item.findViewById(R.id.quest_name_task)
        val questPoints: TextView? = item.findViewById(R.id.quest_points_amount)
        val questDate: TextView? = item.findViewById(R.id.quest_date)
        val complete: CheckBox? = item.findViewById(R.id.quest_complete_task)
        val moreOptions: View = item.findViewById(R.id.popup_menu)
    }

    inner class EmptyViewHolder(item: View) : RecyclerView.ViewHolder(item)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val context = parent.context
        return when (viewType) {
            VIEW_TYPE_QUEST -> {
                val view = LayoutInflater.from(context)
                    .inflate(R.layout.task_quest_item_layout, parent, false)
                QuestViewHolder(view)
            }
            else -> {
                val emptyView = LayoutInflater.from(context)
                    .inflate(R.layout.home_quest_empty_view_layout, parent, false)
                EmptyViewHolder(emptyView)
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is QuestViewHolder) {
            val quest = questList[position]

            holder.complete?.setOnCheckedChangeListener { _, isChecked ->
                if (isChecked) {
                    // Quest is marked as complete, remove it from the list
                    questList.remove(quest)
                    notifyDataSetChanged()
                }
            }

            if (!quest.isComplete) {
                // Display the quest details as usual
                holder.questName?.text = quest.questName
                holder.questPoints?.text = "+" + quest.questPoints.toString()
                holder.questDate?.text = quest.questDate.toString()
                holder.complete?.isChecked = quest.isComplete

                // Set up more options click listener
                holder.moreOptions.setOnClickListener { showPopupMenu(holder.moreOptions, quest) }
            } else {
                // Hide or handle completed quests as needed
                holder.itemView.visibility = View.GONE
            }
        }
    }

    fun updateQuests(newQuests: List<Quest>) {
        questList.clear()
        questList.addAll(newQuests)
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int {
        return if (questList.isEmpty()) {
            1
        } else {
            questList.size
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if (questList.isEmpty()) {
            VIEW_TYPE_EMPTY
        } else {
            VIEW_TYPE_QUEST
        }
    }

    private fun showPopupMenu(anchorView: View, quest: Quest) {
        val popupMenu = PopupMenu(context, anchorView)
        popupMenu.inflate(R.menu.quest_popup_menu) // Create a menu resource file (res/menu/quest_popup_menu.xml)

        // Set up menu item click listener
        popupMenu.setOnMenuItemClickListener { menuItem: MenuItem ->
            when (menuItem.itemId) {
                R.id.menu_delete -> {
                    // Handle delete action
                    questList.remove(quest)
                    notifyDataSetChanged()
                    true
                }
                // Add more menu items as needed
                else -> false
            }
        }

        popupMenu.show()
    }
}