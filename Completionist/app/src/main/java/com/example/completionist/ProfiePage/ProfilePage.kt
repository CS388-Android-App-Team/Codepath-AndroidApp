package com.example.completionist.ProfiePage

import android.content.Context
import android.os.Build
import android.os.Bundle
import android.provider.ContactsContract.CommonDataKinds.Im
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.example.completionist.OnNavigationItemClickListener
import com.example.completionist.R
import com.example.completionist.User
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.database
import com.google.firebase.database.getValue

class ProfilePage : Fragment(R.layout.fragment_profile_page) {

    private var listener: OnNavigationItemClickListener? = null
    private lateinit var database: FirebaseDatabase
    private lateinit var usersRef: DatabaseReference
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var currUser: FirebaseUser
    private lateinit var currUserData: User

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

        val homePageNav = view.findViewById<View>(R.id.home_nav)
        val taskPageNav = view.findViewById<View>(R.id.task_nav)
        val profilePageNav = view.findViewById<View>(R.id.profile_nav)

        val userName = view.findViewById<TextView>(R.id.username_profilepage)
        val userImage = view.findViewById<ImageView>(R.id.userImage_profilepage)
        val userPoints = view.findViewById<TextView>(R.id.points_profilepage)
        val userPartySize = view.findViewById<TextView>(R.id.partysize_profilepage)


        val settingsIcon = view.findViewById<ImageView>(R.id.settingsicon_profilepage)


//        usersRef.child(currUser.uid).get().addOnSuccessListener {
//          //  currUserData = User(currUser.uid, it.child("username").value.toString(), it.child("email").value.toString(), it.child("level").value.toString().toInt(), it.child("xp").value.toString().toInt(), it.child("streak").value.toString().toInt(), it.child("consistency").value.toString().toInt(), it.child("friendCount").value.toString().toInt())
//            userName.text = it.child("username").value.toString()
//            userPoints.text = it.child("level").value.toString()
//            userPartySize.text = it.child("friendCount").value.toString()
//
//            Log.i("firebase", "Got username value ${it.value}")
//        }.addOnFailureListener{
//            Log.e("firebase", "Error getting data", it)
//        }

        val args = arguments
        if(args!=null){
            val currentUserData = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                args.getSerializable("USER_DATA", User::class.java) as? User
            } else {
                args.getSerializable("USER_DATA") as? User
            }
            userName.text = currentUserData?.username
            userPoints.text = "Level ${currentUserData?.level.toString()}"
            userPartySize.text = "${currentUserData?.friendCount.toString()} Party Members"
//            Log.v("Profile page user data", "${currentUserData}")
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