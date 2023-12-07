package com.example.completionist

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.example.completionist.HomePage.HomePage
import com.example.completionist.signinandup.SignIn

class SignInAndUp : AppCompatActivity(), SignInAndUpClickListener {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_in_and_up)

        supportFragmentManager.beginTransaction()
            .replace(R.id.signFragmentContainer, SignIn())
            .commit()
    }

    override fun onRegisterClick() {
        TODO("Not yet implemented")
    }

    override fun onSignUpClick() {
        TODO("Not yet implemented")
    }

    override fun onForgotPasswordClick() {
        TODO("Not yet implemented")
    }

    override fun onBackClick() {
        TODO("Not yet implemented")
    }

    private fun switchFragment(fragment: Fragment){
        supportFragmentManager.beginTransaction()
            .replace(R.id.signFragmentContainer, fragment)
            .addToBackStack(null) // Optionally add to back stack
            .commit()
    }
}