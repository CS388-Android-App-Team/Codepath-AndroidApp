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
    private lateinit var questAdapter: QuestAdapter
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
        // Format the current date to match the database format
        val formattedDate = currentDate.format(DateTimeFormatter.ofPattern("MM/dd/yyyy", Locale.getDefault()))

        questViewModel.getQuestsByDate(formattedDate).observe(viewLifecycleOwner) { quests ->
            // Log the quests to check if they are being retrieved
            quests?.let {
                for (quest in it) {
                    println("Quest Name: ${quest.questName}, Points: ${quest.questPoints}, Date: ${quest.questDate}")
                }
            }

            // Update the adapter with the latest quests
            questAdapter.updateQuests(quests.orEmpty())
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is OnNavigationItemClickListener) {
            listener = context
            val questDatabase = QuestDatabase.getDatabase(requireContext())
            val questDao = questDatabase.questDao()
            questAdapter = QuestAdapter(mutableListOf(), context, questDao)
            questViewModel = ViewModelProvider(this, QuestViewModelFactory(requireActivity().application))
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
            var questDate = data?.getStringExtra("QUEST_DATE")

            // Check if the data is not null and questDate is empty
            if (questName != null && questPoints != null) {
                // Use the current selected date as default
                questDate = questDate.takeIf { it?.isNotEmpty() == true } ?: currentDate.format(dateFormatter)

                // Add the new quest using the data
                addNewQuest(questName, questPoints, questDate)
            }
        }
    }


    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val questRecyclerView = view.findViewById<RecyclerView>(R.id.quests_recycler_view)

        val layoutManagerQuest = GridLayoutManager(requireContext(), 2)

        questRecyclerView.layoutManager = layoutManagerQuest
        questRecyclerView.adapter = questAdapter

        val addNewQuestButton = view.findViewById<ImageView>(R.id.add_new_quest_button_task)

        dateText = view.findViewById(R.id.selected_date_text)
        changeDateArrowRight = view.findViewById(R.id.change_date_arrow_forward)
        changeDateArrowLeft = view.findViewById(R.id.change_date_arrow_backward)

        // Set initial date
        updateDateText()

        updateQuestsAdapter()

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
    }
}