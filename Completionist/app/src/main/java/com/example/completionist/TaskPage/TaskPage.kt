package com.example.completionist.TaskPage

import com.example.completionist.Quests.QuestAdapter
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.completionist.OnNavigationItemClickListener
import com.example.completionist.Quests.Quest
import com.example.completionist.R
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

val questList = mutableListOf<Quest>()
private lateinit var questAdapter: QuestAdapter

class TaskPage : Fragment(R.layout.fragment_task_page) {

    private lateinit var dateText: TextView
    private lateinit var changeDateArrowRight: ImageView
    private lateinit var changeDateArrowLeft: ImageView

    @RequiresApi(Build.VERSION_CODES.O)
    private var currentDate: LocalDate = LocalDate.now()
    @RequiresApi(Build.VERSION_CODES.O)
    private val dateFormatter = DateTimeFormatter.ofPattern("MM/dd/yyyy", Locale.getDefault())

    private val ADD_NEW_QUEST_REQUEST_CODE = 123
    private var listener: OnNavigationItemClickListener? = null

    @RequiresApi(Build.VERSION_CODES.O)
    private fun addNewQuest(
        questName: String?,
        questPoints: Int?,
        questDate: LocalDate
    ) {
        val newQuest = Quest(questName, questPoints, questDate, false)
        questList.add(newQuest)
        updateQuestsAdapter()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun updateQuestsAdapter() {
        val filteredQuests = questList.filter { it.questDate == currentDate && !it.isComplete }
        questAdapter.updateQuests(filteredQuests)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == ADD_NEW_QUEST_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            val formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy")

            // Retrieve quest details from the resultIntent
            val questName = data?.getStringExtra("QUEST_NAME")
            val questPoints = data?.getIntExtra("QUEST_POINTS", 0)
            val questDateStr = data?.getStringExtra("QUEST_DATE")

            // Check if questDateStr is not empty before parsing
            val questDate = if (!questDateStr.isNullOrEmpty()) {
                LocalDate.parse(questDateStr, formatter)
            } else {
                // Use some default value or handle it accordingly
                LocalDate.now()
            }

            // Call addNewQuest function with the retrieved quest details
            addNewQuest(questName, questPoints, questDate)
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is OnNavigationItemClickListener) {
            listener = context
            questAdapter = QuestAdapter(questList, context) // Initialize it here
        } else {
            throw RuntimeException("$context must implement OnNavigationItemClickListener")
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun updateDateText() {
        dateText.text = currentDate.format(dateFormatter)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val questRecyclerView = view.findViewById<RecyclerView>(R.id.quests_recycler_view)

        val layoutManagerQuest =  GridLayoutManager(requireContext(), 2)

        questRecyclerView.layoutManager = layoutManagerQuest
        questRecyclerView.adapter = questAdapter

        val addNewQuestButton = view.findViewById<ImageView>(R.id.add_new_quest_button_task)

        dateText = view.findViewById(R.id.selected_date_text)
        changeDateArrowRight = view.findViewById(R.id.change_date_arrow_forward)
        changeDateArrowLeft = view.findViewById(R.id.change_date_arrow_backward)

        // Set initial date
        updateDateText()

        // Handle arrow clicks
        changeDateArrowRight.setOnClickListener {
            currentDate = currentDate.plusDays(1)
            updateDateText()
            updateQuestsAdapter()
        }

        changeDateArrowLeft.setOnClickListener {
            currentDate = currentDate.minusDays(1)
            updateDateText()
            updateQuestsAdapter()
        }

        addNewQuestButton.setOnClickListener {
            val intent = Intent(requireContext(), AddNewTaskPage::class.java)
            startActivityForResult(intent, ADD_NEW_QUEST_REQUEST_CODE)
        }

        val homePageNav = view.findViewById<View>(R.id.home_nav)
        val taskPageNav = view.findViewById<View>(R.id.task_nav)
        val profilePageNav = view.findViewById<View>(R.id.profile_nav)

        homePageNav.setOnClickListener{
            listener?.onHomeClicked()
        }
        taskPageNav.setOnClickListener{
            listener?.onTaskClicked()
        }
        profilePageNav.setOnClickListener{
            listener?.onProfileClicked()
        }
    }
}