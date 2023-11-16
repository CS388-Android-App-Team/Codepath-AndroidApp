package com.example.completionist.HomePage

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.completionist.OnNavigationItemClickListener
import com.example.completionist.R

class HomePage : Fragment(R.layout.fragment_home_page) {

    private var listener: OnNavigationItemClickListener? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is OnNavigationItemClickListener) {
            listener = context
        } else {
            throw RuntimeException("$context must implement OnNavigationItemClickListener")
        }
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val dummyQuestList = mutableListOf<DummyQuest>()
//        dummyQuestList.add(DummyQuest("New QUest", false))

        val questRecyclerView = view.findViewById<RecyclerView>(R.id.home_page_quests)
        val partyRecyclerView = view.findViewById<RecyclerView>(R.id.home_page_party)

        val layoutManagerQuest = LinearLayoutManager(requireContext())
        val layoutManagerParty = LinearLayoutManager(requireContext())

        val questAdapter = QuestAdapter(dummyQuestList)
//        val partyMemberAdapter = PartyMemberAdapter(partyMemberList)

        questRecyclerView.layoutManager = layoutManagerQuest
        questRecyclerView.adapter = questAdapter
//        partyRecyclerView.adapter = partyMemberAdapter


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