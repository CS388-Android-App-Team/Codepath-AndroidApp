package com.example.completionist

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log

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
    }

    override fun onTaskClicked() {
        Log.v("NavBar", "Task Clicked")

    }

    override fun onProfileClicked() {
        Log.v("NavBar", "Profile Clicked")
    }
}