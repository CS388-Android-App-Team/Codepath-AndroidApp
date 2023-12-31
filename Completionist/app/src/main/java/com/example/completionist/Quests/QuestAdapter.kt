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
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class QuestAdapter(
    private val questList: MutableList<Quest>,
    private val context: Context,
    private val questDao: QuestDao,
    private val userId: String
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

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

            // Only bind the quest if it belongs to the current user
            if (quest.userId == userId) {

                // Set up more options click listener
                holder.moreOptions.setOnClickListener { showPopupMenu(holder.moreOptions, quest) }

                // Remove the previous listener to avoid conflicts
                holder.complete?.setOnCheckedChangeListener(null)

                // Set the checked state without triggering the listener
                holder.complete?.isChecked = quest.isComplete

                // Disable the checkbox if the quest is complete
                holder.complete?.isEnabled = !quest.isComplete

                // Set up the new listener
                holder.complete?.setOnCheckedChangeListener { _, isChecked ->
                    if (isChecked) {
                        if (!quest.isComplete) {
                            // Mark the quest as complete
                            quest.isComplete = true
                        } else {
                            // If the quest is already complete, prevent unchecking
                            holder.complete?.isChecked = true
                        }
                    }
                }

                // Display the quest details as usual
                holder.questName?.text = quest.questName
                holder.questPoints?.text = "+" + quest.questPoints.toString()
                holder.questDate?.text = quest.questDate.toString()
            }
        }
    }

    fun updateQuests(newQuests: List<Quest?>) {
        questList.clear()
        questList.addAll(newQuests.filterNotNull().filter { it.userId == userId })
        notifyDataSetChanged()
    }

    // Add these methods
    fun removeQuest(quest: Quest) {
        questList.remove(quest)
        notifyDataSetChanged()
    }

    fun addQuest(quest: Quest) {
        questList.add(quest)
        notifyDataSetChanged()
    }

    fun getAllQuests(): List<Quest> {
        return questList.toList() // Returns a copy of the questList
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
                    GlobalScope.launch {
                        questDao.delete(quest)
                    }
                    true
                }
                // Add more menu items as needed
                else -> false
            }
        }

        popupMenu.show()
    }
}
