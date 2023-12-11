package com.example.completionist

import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import com.example.completionist.HomePage.HomePage
import com.example.completionist.ProfiePage.ProfilePage
import com.example.completionist.ProfiePage.SettingsPage
import com.example.completionist.TaskPage.TaskPage
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.database

class MainActivity : AppCompatActivity(), OnNavigationItemClickListener {
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var recievedIntent: Intent
    private var currentUserData: User? = null

    private lateinit var database: FirebaseDatabase
    private lateinit var usersRef: DatabaseReference

     override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, HomePage())
            .commit()

        firebaseAuth = FirebaseAuth.getInstance()
        recievedIntent = intent;

         currentUserData = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
             recievedIntent.getSerializableExtra("USER_DATA", User::class.java) as? User
         } else {
             recievedIntent.getSerializableExtra("USER_DATA") as? User
         }

         database = Firebase.database
         usersRef = database.getReference("users")

        Log.v("MainActivity UID", "${firebaseAuth.currentUser?.uid}, recieved value: ${recievedIntent.getStringExtra("USER_UID")}" )
    }
    override fun onHomeClicked() {
        Log.v("NavBar", "Home Clicked")
        val currentFragment = supportFragmentManager.findFragmentById(R.id.fragmentContainer)
        if (currentFragment !is HomePage) {
            switchFragment(HomePage())
        }else{
            Log.v("NavBar", "Already Home")
        }
    }
    override fun onTaskClicked() {
        Log.v("NavBar", "Task Clicked")
        val currentFragment = supportFragmentManager.findFragmentById(R.id.fragmentContainer)
        if (currentFragment !is TaskPage) {
            switchFragment(TaskPage())
        }else{
            Log.v("NavBar", "Already Task")
        }
    }
    override fun onProfileClicked() {
        Log.v("NavBar", "Profile Clicked")
        val currentFragment = supportFragmentManager.findFragmentById(R.id.fragmentContainer)
        if (currentFragment !is ProfilePage) {
            val bundle = Bundle()
            bundle.putSerializable("USER_DATA", currentUserData)
            switchFragment(ProfilePage(), bundle)
        }else{
            Log.v("NavBar", "Already Profile")
        }
    }
    override fun onSignOutClicked() {
        Log.v("NavBar", "Sign In Clicked")
        firebaseAuth.signOut()
        val intent = Intent(this, SplashScreen::class.java)
        startActivity(intent)
    }

    override fun onSettingsClicked() {
        Log.v("Nav", "Settings Clicked")
        val currentFragment = supportFragmentManager.findFragmentById(R.id.fragmentContainer)
        if (currentFragment !is SettingsPage) {
            switchFragment(SettingsPage())
        }else{
            Log.v("NavBar", "Already Settings")
        }
    }

    private fun switchFragment(fragment: Fragment, data: Bundle? = null){
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.fragmentContainer, fragment)
        transaction.addToBackStack(null) // Optionally add to back stack
        if (data != null) {
            fragment.arguments = data
        }
        transaction.commit()
    }

    fun isValidUserName(userName: String, callback: (Boolean) -> Unit) {
        usersRef.orderByChild("username").equalTo(userName).get().addOnSuccessListener { dataSnapshot ->
            if (dataSnapshot.exists()) {
                // Username already exists in the database
                callback(false)
            } else {
                // Username does not exist in the database
                callback(true)
            }
        }.addOnFailureListener {
            // Error occurred while fetching data, consider it as a valid username
            callback(true)
            Log.e("firebase", "Error checking username", it)
        }
    }

}