package com.example.completionist

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import com.example.completionist.HomePage.HomePage

class MainActivity : AppCompatActivity(), OnNavigationItemClickListener {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, HomePage())
            .commit()
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
    private fun switchFragment(fragment: Fragment){
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, fragment)
            .addToBackStack(null) // Optionally add to back stack
            .commit()
    }
}