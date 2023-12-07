package com.example.completionist

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import com.example.completionist.HomePage.HomePage
import com.example.completionist.signinandup.ForgotPassword
import com.example.completionist.signinandup.SignIn
import com.example.completionist.signinandup.SignUp

class SignInAndUp : AppCompatActivity(), SignInAndUpClickListener {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_in_and_up)

        supportFragmentManager.beginTransaction()
            .replace(R.id.signFragmentContainer, SignIn())
            .commit()
    }

    override fun onSignInClick() {
        TODO("Not yet implemented")
    }

    override fun onSendLinkClick() {
        TODO("Not yet implemented")
    }

    override fun onSignUpClick() {
        TODO("Not yet implemented")
    }

    override fun onRegisterClick() {
        Log.v("NavBar", "Register Clicked")
        val currentFragment = supportFragmentManager.findFragmentById(R.id.signFragmentContainer)
        // Check if the current fragment is not ProfilePage
        if (currentFragment !is SignUp) {
            switchFragment(SignUp())
        }else{
            Log.v("NavBar", "Already SignUp")
        }
    }

    override fun onForgotPasswordClick() {
        Log.v("NavBar", "FP Clicked")
        val currentFragment = supportFragmentManager.findFragmentById(R.id.signFragmentContainer)
        // Check if the current fragment is not ProfilePage
        if (currentFragment !is ForgotPassword) {
            switchFragment(ForgotPassword())
        }else{
            Log.v("NavBar", "Already ForgotPassword")
        }
    }

    override fun onBackClick() {
        Log.v("NavBar", "Back Clicked")
        val currentFragment = supportFragmentManager.findFragmentById(R.id.signFragmentContainer)
        // Check if the current fragment is not ProfilePage
        if (currentFragment !is SignIn) {
            switchFragment(SignIn())
        }else{
            Log.v("NavBar", "Already SignIn")
        }
    }

    private fun switchFragment(fragment: Fragment){
        supportFragmentManager.beginTransaction()
            .replace(R.id.signFragmentContainer, fragment)
            .addToBackStack(null) // Optionally add to back stack
            .commit()
    }
}