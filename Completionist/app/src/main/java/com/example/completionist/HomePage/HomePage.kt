package com.example.completionist.HomePage

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.completionist.MainActivity
import com.example.completionist.OnNavigationItemClickListener
import com.example.completionist.R
import com.example.completionist.User
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.database

class HomePage : Fragment(R.layout.fragment_home_page) {

    private var listener: OnNavigationItemClickListener? = null
    private lateinit var database: FirebaseDatabase
    private lateinit var usersRef: DatabaseReference
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var currUser: FirebaseUser
    //private lateinit var currUserData: User

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


        database = Firebase.database
        usersRef = database.getReference("users")
        firebaseAuth = FirebaseAuth.getInstance()
        firebaseAuth.currentUser?.let { us ->
            currUser = us }

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
        val newFriendButton = view.findViewById<Button>(R.id.addFriendButton)
        val newFriendName = view.findViewById<EditText>(R.id.newCompanionEntry)

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


        newFriendButton.setOnClickListener{
            val friendName = newFriendName.text
            usersRef.child(currUser.uid).get().addOnSuccessListener { us ->
                usersRef.orderByChild("username").equalTo(friendName.toString()).get()
                    .addOnSuccessListener { fr ->
                        var it = fr.children.first()
                        val friendID = it.key.toString()
                        Log.i("firebase", "$it")
                        if (it.child("friends").child(currUser.uid).exists()){
                            //check if they sent a request to you, if yes then mark completed. if already friends, do nothing
                            if (it.child("friends").child(currUser.uid).value == false){
                                usersRef.child(friendID).child("friends").child(currUser.uid).setValue(true)
                                usersRef.child(currUser.uid).child("friends").child(friendID).setValue(true)
                            }
                        }
                        //if they did not send a request to you, mark your request in your friends list
                        else{
                            usersRef.child(currUser.uid).child("friends").child(friendID).setValue(false)
                        }
                    }.addOnFailureListener{
                        //invalid username entry
                        Log.e("firebase", "Error getting data, user $friendName may not exist", it)
                    }
            }
        }

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