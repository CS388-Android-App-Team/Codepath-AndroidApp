package com.example.completionist

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import com.example.completionist.HomePage.HomePage
import com.example.completionist.ProfiePage.ProfilePage
import com.example.completionist.ProfiePage.SettingsPage
import com.example.completionist.TaskPage.TaskPage
import com.google.firebase.auth.FirebaseAuth

class MainActivity : AppCompatActivity(), OnNavigationItemClickListener {
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var recievedIntent: Intent
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, HomePage())
            .commit()
    /*  testing basic connection to realtime db
        val database = Firebase.database
        val myRef = database.getReference("message")
        myRef.setValue("Hello, World!")

     */

        firebaseAuth = FirebaseAuth.getInstance()
        recievedIntent = intent

        Log.v("MainActivity UID", "${firebaseAuth.currentUser?.uid}, recieved value: ${recievedIntent.getStringExtra("USER_UID")}" )
    }
    override fun onHomeClicked() {
        Log.v("NavBar", "Home Clicked")
        val currentFragment = supportFragmentManager.findFragmentById(R.id.fragmentContainer)
        // Check if the current fragment is not HomePage
        if (currentFragment !is HomePage) {
            switchFragment(HomePage())
        }else{
            Log.v("NavBar", "Already Home")
        }
    }
    override fun onTaskClicked() {
        Log.v("NavBar", "Task Clicked")
        val currentFragment = supportFragmentManager.findFragmentById(R.id.fragmentContainer)
        // Check if the current fragment is not TaskPage
        if (currentFragment !is TaskPage) {
            switchFragment(TaskPage())
        }else{
            Log.v("NavBar", "Already Task")
        }
    }
    override fun onProfileClicked() {
        Log.v("NavBar", "Profile Clicked")
        val currentFragment = supportFragmentManager.findFragmentById(R.id.fragmentContainer)
        // Check if the current fragment is not ProfilePage
        if (currentFragment !is ProfilePage) {
            switchFragment(ProfilePage())
        }else{
            Log.v("NavBar", "Already Profile")
        }
    }
    override fun onSignOutClicked() {
        Log.v("NavBar", "Sign In Clicked")
        firebaseAuth.signOut()
        val intent = Intent(this, SplashScreen::class.java)
        startActivity(intent)
//        val currentFragment = supportFragmentManager.findFragmentById(R.id.fragmentContainer)
//        // Check if the current fragment is not SignIn
//        if (currentFragment !is SignIn) {
//            switchFragment(SignIn())
//        }else{
//            Log.v("SignInButton", "Already Sign In")
//        }
    }

    override fun onSettingsClicked() {
        Log.v("Nav", "Settings Clicked")
        val currentFragment = supportFragmentManager.findFragmentById(R.id.fragmentContainer)
        // Check if the current fragment is not ProfilePage
        if (currentFragment !is SettingsPage) {
            switchFragment(SettingsPage())
        }else{
            Log.v("NavBar", "Already Settings")
        }
    }

    private fun switchFragment(fragment: Fragment){
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, fragment)
            .addToBackStack(null) // Optionally add to back stack
            .commit()
    }
}