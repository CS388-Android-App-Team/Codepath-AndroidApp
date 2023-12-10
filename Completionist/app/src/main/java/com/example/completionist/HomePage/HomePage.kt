package com.example.completionist.HomePage

import android.content.Context
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.completionist.MainActivity
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
       // val activity: MainActivity? = activity as MainActivity?
        val dummyQuestList = mutableListOf<DummyQuest>()
        dummyQuestList.add(DummyQuest("New QUest", false))
        dummyQuestList.add(DummyQuest("New QUest", false))
        dummyQuestList.add(DummyQuest("New QUest", false))
        dummyQuestList.add(DummyQuest("New QUest", false))
        dummyQuestList.add(DummyQuest("New QUest", false))
        dummyQuestList.add(DummyQuest("New QUest", false))
        dummyQuestList.add(DummyQuest("New QUest", false))


        val questRecyclerView = view.findViewById<RecyclerView>(R.id.home_page_quests)
        val partyRecyclerView = view.findViewById<RecyclerView>(R.id.home_page_party)
        val usernameDisplay = view.findViewById<TextView>(R.id.username)

        val layoutManagerQuest =  GridLayoutManager(requireContext(), 2)
//        layoutManagerQuest.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
//            override fun getSpanSize(position: Int): Int {
//                return if (position % 2 == 0) 2 else 1
//            }
//        }
        val layoutManagerParty = LinearLayoutManager(requireContext())

        val questAdapter = DummyQuestAdapter(dummyQuestList)
//        val partyMemberAdapter = PartyMemberAdapter(partyMemberList)

        questRecyclerView.layoutManager = layoutManagerQuest
        questRecyclerView.adapter = questAdapter

        //usernameDisplay.text = currUserData.username
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