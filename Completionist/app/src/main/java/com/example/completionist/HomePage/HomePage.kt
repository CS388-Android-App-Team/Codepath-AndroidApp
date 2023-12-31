package com.example.completionist.HomePage

import android.content.Context
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.completionist.Friend
import com.example.completionist.OnNavigationItemClickListener
import com.example.completionist.Quests.QuestAdapter
import com.example.completionist.Quests.QuestDatabase
import com.example.completionist.Quests.QuestViewModel
import com.example.completionist.Quests.QuestViewModelFactory
import com.example.completionist.R
import com.example.completionist.UserViewModel
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.database
//import com.google.firebase.firestore.auth.User
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

class HomePage : Fragment(R.layout.fragment_home_page) {

    private var listener: OnNavigationItemClickListener? = null
    private lateinit var database: FirebaseDatabase
    private lateinit var usersRef: DatabaseReference
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var currUser: FirebaseUser
    private lateinit var userViewModel: UserViewModel // Initialize the UserViewModel variable

    private lateinit var questAdapter: QuestAdapter
    private lateinit var questViewModel: QuestViewModel

    private fun getCurrentUserId(): String {
        // Assuming you have the Firebase user
        val firebaseUser = FirebaseAuth.getInstance().currentUser
        return firebaseUser?.uid ?: ""
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is OnNavigationItemClickListener) {
            listener = context
            val questDatabase = QuestDatabase.getDatabase(requireContext())
            val questDao = questDatabase.questDao()

            // Get the current user ID
            val currentUserId = getCurrentUserId()

            // Pass the user ID to the QuestViewModel
            questViewModel = ViewModelProvider(
                this,
                QuestViewModelFactory(requireActivity().application, currentUserId)
            ).get(QuestViewModel::class.java)

            questAdapter = QuestAdapter(mutableListOf(), context, questDao, currentUserId)
        } else {
            throw RuntimeException("$context must implement OnNavigationItemClickListener")
        }
    }

    private fun updateQuests() {
        val questViewModel = ViewModelProvider(this).get(QuestViewModel::class.java)

        questViewModel.getSortedQuestsForCurrentUser().observe(viewLifecycleOwner) { quests ->
            // Filter out completed quests
            val ongoingQuests = quests.filter { !it.isComplete }

            // Update the adapter with the ongoing quests
            questAdapter.updateQuests(ongoingQuests)
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
        userViewModel = ViewModelProvider(this).get(UserViewModel::class.java)

       // val activity: MainActivity? = activity as MainActivity?

        val formattedDate = LocalDate.now().format(DateTimeFormatter.ofPattern("MM/dd/yyyy", Locale.getDefault()))

        val friendList = mutableListOf<Friend>()
        val partyRecyclerView = view.findViewById<RecyclerView>(R.id.home_page_party)
        val layoutManagerParty =  GridLayoutManager(requireContext(), 1)
        val fnameDisplay = view.findViewById<TextView>(R.id.fNameTextView)

        partyRecyclerView.layoutManager = layoutManagerParty


        userViewModel.getUserById(currUser.uid).observe(viewLifecycleOwner){user ->
            fnameDisplay.text = "Hi, " + user?.firstName
        }

        //friend display
        usersRef.get().addOnSuccessListener { users ->
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
        val newFriendButton = view.findViewById<Button>(R.id.addFriendButton)
        val newFriendName = view.findViewById<EditText>(R.id.newCompanionEntry)

        val layoutManagerQuest =  GridLayoutManager(requireContext(), 2)
//        layoutManagerQuest.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
//            override fun getSpanSize(position: Int): Int {
//                return if (position % 2 == 0) 2 else 1
//            }
//        }

        questRecyclerView.layoutManager = layoutManagerQuest
        questRecyclerView.adapter = questAdapter

        val homePageNav = view.findViewById<View>(R.id.home_nav)
        val taskPageNav = view.findViewById<View>(R.id.task_nav)
        val profilePageNav = view.findViewById<View>(R.id.profile_nav)

        newFriendName.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                // Keyboard is shown
                activity?.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING)
            } else {
                // Keyboard is hidden
                activity?.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
            }
        }

        //friend request stuff
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
                                //become friends
                                usersRef.child(friendID).child("friends").child(currUser.uid)
                                    .setValue(true)
                                usersRef.child(currUser.uid).child("friends").child(friendID)
                                    .setValue(true)
                                //increment friend count
                                usersRef.child(friendID).child("friendCount")
                                    .setValue(it.child("friendCount").value.toString().toInt()+1)
                                userViewModel.addToFriendCountById(friendID)

                                usersRef.child(currUser.uid).child("friendCount")
                                    .setValue(us.child("friendCount").value.toString().toInt()+1)
                                userViewModel.addToFriendCountById(currUser.uid)

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

        // Add a method to update quests when the fragment is created
        updateQuests()

    }
}