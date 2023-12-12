package com.example.completionist.HomePage

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.completionist.Friend
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

        val friendList = mutableListOf<Friend>()
        val partyRecyclerView = view.findViewById<RecyclerView>(R.id.home_page_party)
        val layoutManagerParty =  GridLayoutManager(requireContext(), 1)
        val fnameDisplay = view.findViewById<TextView>(R.id.fNameTextView)

        partyRecyclerView.layoutManager = layoutManagerParty

        //this also sucks
        usersRef.get().addOnSuccessListener { users ->
            // var friendCount = users.child(currUser.uid).child("friends").childrenCount
            fnameDisplay.text = users.child(currUser.uid).child("firstName").value.toString()

             users.child(currUser.uid).child("friends").children.forEach(){ friendListEntry ->

                 val friendObject = friendListEntry.key?.let { users.child(it) }
                 if (friendListEntry.value==true) {
                    // Log.i("firebase", "Got friend ${friendListEntry.key} entry")
                     if (friendObject != null) {
                         Log.i("firebase", "Got friend ${friendObject.child("username").value} object")
                         friendList.add(
                             Friend(
                                 friendObject.child("idToken").value.toString(),
                                 friendObject.child("username").value.toString(),
                                 friendObject.child("level").value.toString().toInt(),
                                 friendObject.child("streak").value.toString().toInt(),
                                 friendObject.child("consistency").value.toString().toInt()
                             )
                         )
                     }
                 }
             }
            Log.i("firebase", "Friend list is $friendList")

            partyRecyclerView.adapter = PartyAdapter(friendList)

        }.addOnFailureListener{
            Log.e("firebase", "Error getting users", it)
            partyRecyclerView.adapter = PartyAdapter(friendList)
        }





        val questRecyclerView = view.findViewById<RecyclerView>(R.id.home_page_quests)
      //  val partyRecyclerView = view.findViewById<RecyclerView>(R.id.home_page_party)
     //   val usernameDisplay = view.findViewById<TextView>(R.id.username)
        val newFriendButton = view.findViewById<Button>(R.id.addFriendButton)
        val newFriendName = view.findViewById<EditText>(R.id.newCompanionEntry)

        val layoutManagerQuest =  GridLayoutManager(requireContext(), 2)
//        layoutManagerQuest.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
//            override fun getSpanSize(position: Int): Int {
//                return if (position % 2 == 0) 2 else 1
//            }
//        }
        //val layoutManagerParty = LinearLayoutManager(requireContext())

        val questAdapter = DummyQuestAdapter(dummyQuestList)
      //  val partyMemberAdapter = PartyAdapter(friendList)

        questRecyclerView.layoutManager = layoutManagerQuest
        questRecyclerView.adapter = questAdapter
       // partyRecyclerView.adapter = partyMemberAdapter



        val homePageNav = view.findViewById<View>(R.id.home_nav)
        val taskPageNav = view.findViewById<View>(R.id.task_nav)
        val profilePageNav = view.findViewById<View>(R.id.profile_nav)


        newFriendButton.setOnClickListener{
            val friendName = newFriendName.text
            usersRef.child(currUser.uid).get().addOnSuccessListener { us ->
                usersRef.orderByChild("username").equalTo(friendName.toString()).get()
                    .addOnSuccessListener { fr ->
                        if (fr.childrenCount > 0){
                        var it = fr.children.first()
                        val friendID = it.key.toString()
                        Log.i("firebase", "$it")
                        if (it.child("friends").child(currUser.uid).exists()) {
                            //check if they sent a request to you, if yes then mark completed. if already friends, do nothing
                            if (it.child("friends").child(currUser.uid).value == false) {
                                usersRef.child(friendID).child("friends").child(currUser.uid)
                                    .setValue(true)
                                usersRef.child(currUser.uid).child("friends").child(friendID)
                                    .setValue(true)
                                Toast.makeText(activity, "New Companion!", Toast.LENGTH_SHORT)
                                    .show()
                            }
                            else{
                                Toast.makeText(activity, "You're already companions!", Toast.LENGTH_SHORT).show()
                            }
                        }
                        //if they did not send a request to you, mark your request in your friends list
                        else {
                            usersRef.child(currUser.uid).child("friends").child(friendID)
                                .setValue(false)
                            Toast.makeText(activity, "Invite Sent!", Toast.LENGTH_SHORT).show()
                        }
                    }
                        else{
                            Log.e("firebase", "user $friendName may not exist")
                            Toast.makeText(activity, "Character not created yet:(", Toast.LENGTH_SHORT).show()
                        }
                    }.addOnFailureListener{
                        //invalid username entry
                        Log.e("firebase", "Error getting data", it)
                        Toast.makeText(activity, "User does not exist :(", Toast.LENGTH_SHORT).show()
                    }
            }
            newFriendName.setText("")
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