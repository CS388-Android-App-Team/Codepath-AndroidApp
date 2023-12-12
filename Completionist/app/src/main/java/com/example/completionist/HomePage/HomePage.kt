package com.example.completionist.HomePage

import DummyQuestAdapter
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.completionist.MainActivity
import com.example.completionist.OnNavigationItemClickListener
import com.example.completionist.Quests.QuestViewModel
import com.example.completionist.Quests.QuestViewModelFactory
import com.example.completionist.R
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.database
import com.google.firebase.firestore.auth.User
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

class HomePage : Fragment(R.layout.fragment_home_page) {

    private var listener: OnNavigationItemClickListener? = null
    private lateinit var database: FirebaseDatabase
    private lateinit var usersRef: DatabaseReference
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var currUser: FirebaseUser
    //private lateinit var currUserData: User

    private lateinit var questViewModel: QuestViewModel


    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is OnNavigationItemClickListener) {
            listener = context
        } else {
            throw RuntimeException("$context must implement OnNavigationItemClickListener")
        }
    }
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        database = Firebase.database
        usersRef = database.getReference("users")
        firebaseAuth = FirebaseAuth.getInstance()
        firebaseAuth.currentUser?.let { us ->
            currUser = us }

        questViewModel = ViewModelProvider(this, QuestViewModelFactory(requireActivity().application)).get(
            QuestViewModel::class.java)
        questViewModel.allQuests.observe(viewLifecycleOwner, Observer { quests ->
            // Handle the changes in the list of quests here
            // For example, update UI or perform actions based on changes in quests
        })

       // val activity: MainActivity? = activity as MainActivity?

        val formattedDate = LocalDate.now().format(DateTimeFormatter.ofPattern("MM/dd/yyyy", Locale.getDefault()))

        val dummyQuestList = questViewModel.getQuestsByDate(formattedDate)


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