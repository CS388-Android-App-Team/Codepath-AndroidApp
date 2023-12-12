package com.example.completionist.Quests

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.PopupMenu
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.completionist.MainActivity
import com.example.completionist.R
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class QuestAdapter(
    private val questList: MutableList<Quest>,
    private val context: Context,
    private val questDao: QuestDao
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
            val questXp = quest.questPoints

            holder.complete?.setOnCheckedChangeListener { _, isChecked ->
                if (isChecked) {
                    // Quest is marked as complete, remove it from the list and database
                    questList.remove(quest)
                    notifyDataSetChanged()
                    // Delete from the database
                    GlobalScope.launch {
                        questDao.delete(quest)
                    }
                }
                //add xp to user
                var oldXP: Int? = MainActivity().currentUserData?.xp
                var moreXP: Int? = questXp
                Log.i("User Level", "Old XP + Completion XP: $oldXP + $moreXP}")
                if (oldXP != null && moreXP != null) {
                    MainActivity().updateCurrentUser(newXp = (oldXP + moreXP))
                    Log.i("User Level", "New XP: ${oldXP + moreXP}")
                    Toast.makeText(MainActivity(), "You gained $moreXP XP", Toast.LENGTH_SHORT).show()
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

    fun updateQuests(newQuests: List<Quest?>) {
        questList.clear()
        questList.addAll(newQuests.filterNotNull())
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
