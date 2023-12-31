package com.example.completionist.ProfiePage

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.lifecycle.ViewModelProvider
import com.example.completionist.OnNavigationItemClickListener
import com.example.completionist.Quests.QuestViewModel
import com.example.completionist.Quests.QuestViewModelFactory
import com.example.completionist.R
import com.example.completionist.User
import com.example.completionist.UserViewModel
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.database

class ProfilePage : Fragment(R.layout.fragment_profile_page) {

    private var listener: OnNavigationItemClickListener? = null
    private lateinit var database: FirebaseDatabase
    private lateinit var usersRef: DatabaseReference
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var currUser: FirebaseUser
    private var currUserData: User? = null

    private lateinit var userViewModel: UserViewModel // Initialize the UserViewModel variable
    private lateinit var questViewModel: QuestViewModel // Initialize the QuestViewModel variable
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

        userViewModel = ViewModelProvider(this).get(UserViewModel::class.java)

        val uid = firebaseAuth.currentUser?.uid ?: ""

        val homePageNav = view.findViewById<View>(R.id.home_nav)
        val taskPageNav = view.findViewById<View>(R.id.task_nav)
        val profilePageNav = view.findViewById<View>(R.id.profile_nav)

        val userName = view.findViewById<TextView>(R.id.username_profilepage)
        val userImage = view.findViewById<ImageView>(R.id.userImage_profilepage)
        val userPoints = view.findViewById<TextView>(R.id.points_profilepage)
        val userXP = view.findViewById<TextView>(R.id.xp_progress_textview)
        val userPartySize = view.findViewById<TextView>(R.id.partysize_profilepage)
        val questingStats = view.findViewById<TextView>(R.id.quest_stats)


        val settingsIcon = view.findViewById<ImageView>(R.id.settingsicon_profilepage)

        userViewModel.getUserById(uid).observe(viewLifecycleOwner){user ->
            userName.text = user?.username
            userPoints.text = "LVL: ${user?.level.toString()}"
            userXP.text = "${(user?.xp?.rem(100)).toString()}/100 XP to Level Up"
            userPartySize.text = "${user?.friendCount.toString()} Party Members"
        }

        questViewModel =
            ViewModelProvider(this, QuestViewModelFactory(this.requireActivity().application, uid))[QuestViewModel::class.java]

        questViewModel.getSortedQuestsForCurrentUser().observe(viewLifecycleOwner){quests ->
            var completedQuests = 0
            var totalQuests = 0
            for (quest in quests){
                if (quest.isComplete){
                    completedQuests += 1
                }
                totalQuests += 1
            }
            val completionRate = if (totalQuests != 0) completedQuests.toDouble() / totalQuests.toDouble() * 100 else 0.0
            questingStats.text = "${completedQuests} Quests Completed\n${totalQuests} Total Quests\n${String.format("%.1f", completionRate)} Percent Completion Rate"

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
        settingsIcon.setOnClickListener(){listener?.onSettingsClicked()}

    }
/*
    fun loadUser( currUser: FirebaseUser): User {
          var currentUserData: User
          currentUserData = User("not2", "loaded", "yet2")
        usersRef.child(currUser.uid).child("username").get().addOnSuccessListener {
             currentUserData = User(currUser.uid, it.value.toString(), currUser.email)
            Log.i("firebase", "Got username value ${it.value}")
        }.addOnFailureListener{
            Log.e("firebase", "Error getting data", it)
        }
        return currentUserData
    }

 */
}