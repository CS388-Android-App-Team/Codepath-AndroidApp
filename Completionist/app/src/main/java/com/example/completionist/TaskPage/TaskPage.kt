package com.example.completionist.TaskPage

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
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.completionist.OnNavigationItemClickListener
import com.example.completionist.Quests.Quest
import com.example.completionist.Quests.QuestAdapter
import com.example.completionist.Quests.QuestDatabase
import com.example.completionist.Quests.QuestViewModel
import com.example.completionist.Quests.QuestViewModelFactory
import com.example.completionist.R
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

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
    private lateinit var ongoingQuestAdapter: QuestAdapter
    private lateinit var completedQuestAdapter: QuestAdapter
    private lateinit var questViewModel: QuestViewModel

    @RequiresApi(Build.VERSION_CODES.O)
    private fun addNewQuest(
        questName: String?,
        questPoints: Int?,
        questDate: String?
    ) {
        val newQuest = Quest(
            questName = questName,
            questPoints = questPoints,
            questDate = questDate,
            isComplete = false
        )

        // Insert new quest into the database
        lifecycleScope.launch {
            val questDatabase = QuestDatabase.getDatabase(requireContext())
            val questDao = questDatabase.questDao()

            // Log the quest details
            println("Adding quest: $newQuest")

            questDao.insert(newQuest)

            // Update the UI with the latest data
            updateQuestsAdapter()
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun updateQuestsAdapter() {
        val formattedDate =
            currentDate.format(DateTimeFormatter.ofPattern("MM/dd/yyyy", Locale.getDefault()))

        questViewModel.getQuestsByDate(formattedDate).observe(viewLifecycleOwner) { quests ->
            quests?.let {
                val ongoingQuests = it.filter { !it.isComplete }
                val completedQuests = it.filter { it.isComplete }

                ongoingQuestAdapter.updateQuests(ongoingQuests)
                completedQuestAdapter.updateQuests(completedQuests)
            }
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is OnNavigationItemClickListener) {
            listener = context
            val questDatabase = QuestDatabase.getDatabase(requireContext())
            val questDao = questDatabase.questDao()
            ongoingQuestAdapter = QuestAdapter(mutableListOf(), context, questDao)
            completedQuestAdapter = QuestAdapter(mutableListOf(), context, questDao)
            questViewModel =
                ViewModelProvider(this, QuestViewModelFactory(requireActivity().application))
                    .get(QuestViewModel::class.java)
        } else {
            throw RuntimeException("$context must implement OnNavigationItemClickListener")
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun updateDateText() {
        dateText.text = currentDate.format(dateFormatter)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == ADD_NEW_QUEST_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            // Extract data from the returned intent using correct keys
            val questName = data?.getStringExtra("QUEST_NAME")
            val questPoints = data?.getIntExtra("QUEST_POINTS", 0)
            val questDate = data?.getStringExtra("QUEST_DATE")

            // Check if the data is not null
            if (questName != null && questPoints != null && questDate != null) {
                // Add the new quest using the data
                addNewQuest(questName, questPoints, questDate)
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun addTempQuests() {
        // Add some temporary quests for testing
        val quest1 = Quest("Quest 1", 10, currentDate.toString(), false)
        val quest2 = Quest("Quest 2", 20, currentDate.toString(), false)
        val quest3 = Quest("Quest 3", 15, currentDate.toString(), false)

        lifecycleScope.launch {
            val questDatabase = QuestDatabase.getDatabase(requireContext())
            val questDao = questDatabase.questDao()

            // Insert temporary quests into the database
            questDao.insert(quest1)
            questDao.insert(quest2)
            questDao.insert(quest3)

            // Update the UI with the latest data
            updateQuestsAdapter()
        }
    }

    private fun completeQuest(quest: Quest) {
        quest.isComplete = true
        lifecycleScope.launch {
            val questDatabase = QuestDatabase.getDatabase(requireContext())
            val questDao = questDatabase.questDao()

            // Remove the quest from ongoingQuestRecyclerView
            ongoingQuestAdapter.removeQuest(quest)

            // Add the quest to completedQuestRecyclerView
            completedQuestAdapter.addQuest(quest)

            // Update the quest in the database
            questDao.update(quest)
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val ongoingQuestRecyclerView =
            view.findViewById<RecyclerView>(R.id.ongoing_quests_recycler_view)
        val completedQuestRecyclerView =
            view.findViewById<RecyclerView>(R.id.completed_quests_recycler_view)

        val ongoingQuestLayoutManager = GridLayoutManager(requireContext(), 2)
        val completedQuestLayoutManager = GridLayoutManager(requireContext(), 2)

        ongoingQuestRecyclerView.layoutManager = ongoingQuestLayoutManager
        completedQuestRecyclerView.layoutManager = completedQuestLayoutManager

        val addNewQuestButton = view.findViewById<ImageView>(R.id.add_new_quest_button_task)
        val completeButton = view.findViewById<ImageView>(R.id.complete_button)

        dateText = view.findViewById(R.id.selected_date_text)
        changeDateArrowRight = view.findViewById(R.id.change_date_arrow_forward)
        changeDateArrowLeft = view.findViewById(R.id.change_date_arrow_backward)

        // Set initial date
        updateDateText()

        // Set up the complete button click listener
        completeButton.setOnClickListener {
            // For demonstration purposes, let's assume you want to complete the first ongoing quest
            ongoingQuestAdapter.getAllQuests().firstOrNull()?.let { firstOngoingQuest ->
                completeQuest(firstOngoingQuest)
                // Update the adapters after completing the quest
                updateQuestsAdapter()
            }
        }

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

        homePageNav.setOnClickListener {
            listener?.onHomeClicked()
        }
        taskPageNav.setOnClickListener {
            listener?.onTaskClicked()
        }
        profilePageNav.setOnClickListener {
            listener?.onProfileClicked()
        }

        // Set up the adapters after all the UI components are initialized
        ongoingQuestRecyclerView.adapter = ongoingQuestAdapter
        completedQuestRecyclerView.adapter = completedQuestAdapter

        // Load temporary quests
        addTempQuests()
    }
}