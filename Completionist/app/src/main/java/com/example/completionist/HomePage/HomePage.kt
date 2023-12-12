package com.example.completionist.HomePage

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.completionist.MainActivity
import com.example.completionist.OnNavigationItemClickListener
import com.example.completionist.Quests.Quest
import com.example.completionist.Quests.QuestAdapter
import com.example.completionist.Quests.QuestDatabase
import com.example.completionist.Quests.QuestViewModel
import com.example.completionist.Quests.QuestViewModelFactory
import com.example.completionist.R

class HomePage : Fragment(R.layout.fragment_home_page) {

    private var listener: OnNavigationItemClickListener? = null
    private lateinit var questAdapter: QuestAdapter
    private lateinit var questViewModel: QuestViewModel

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is OnNavigationItemClickListener) {
            listener = context
            val questDatabase = QuestDatabase.getDatabase(requireContext())
            val questDao = questDatabase.questDao()
            questAdapter = QuestAdapter(mutableListOf(), requireContext(), questDao, true)
            questViewModel =
                ViewModelProvider(this, QuestViewModelFactory(requireActivity().application))
                    .get(QuestViewModel::class.java)
        } else {
            throw RuntimeException("$context must implement OnNavigationItemClickListener")
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val questRecyclerView = view.findViewById<RecyclerView>(R.id.home_page_quests)

        val layoutManagerQuest = GridLayoutManager(requireContext(), 2)

        questRecyclerView.layoutManager = layoutManagerQuest
        questRecyclerView.adapter = questAdapter

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

        // Add a method to update quests when the fragment is created
        updateQuests()
    }

    private fun updateQuests() {
        val questViewModel = ViewModelProvider(this).get(QuestViewModel::class.java)

        questViewModel.getSortedQuests().observe(viewLifecycleOwner) { quests ->
            // Update the adapter with the latest quests
            questAdapter.updateQuests(quests)
        }
    }
}
