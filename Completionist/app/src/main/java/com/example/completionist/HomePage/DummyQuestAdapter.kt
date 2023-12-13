import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.lifecycle.LiveData
import androidx.recyclerview.widget.RecyclerView
import com.example.completionist.Quests.Quest
import com.example.completionist.R

class DummyQuestAdapter(private val questLiveData: LiveData<List<Quest>>) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var questList: List<Quest> = listOf()
    private val VIEW_TYPE_QUEST = 1
    private val VIEW_TYPE_EMPTY = 2

    init {
        questLiveData.observeForever { quests ->
            questList = quests
            notifyDataSetChanged()
        }
    }

    inner class QuestViewHolder(item: View) : RecyclerView.ViewHolder(item) {
        val questName = item.findViewById<TextView>(R.id.quest_name_task)
    }

    inner class EmptyViewHolder(item: View) : RecyclerView.ViewHolder(item)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            VIEW_TYPE_QUEST -> {
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.home_quest_item_layout, parent, false)
                QuestViewHolder(view)
            }
            else -> {
                val emptyView = LayoutInflater.from(parent.context)
                    .inflate(R.layout.home_quest_empty_view_layout, parent, false)
                EmptyViewHolder(emptyView)
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is QuestViewHolder) {
            val quest = questList[position]
            holder.questName.text = quest.questName ?: ""
        }
    }

    override fun getItemCount(): Int {
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
