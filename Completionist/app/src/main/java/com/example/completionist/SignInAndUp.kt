package com.example.completionist

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.completionist.HomePage.HomePage
import com.example.completionist.signinandup.ForgotPassword
import com.example.completionist.signinandup.SignIn
import com.example.completionist.signinandup.SignUp
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth

class SignInAndUp : AppCompatActivity(), SignInAndUpClickListener {

    private lateinit var firebaseAuth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_in_and_up)

        supportFragmentManager.beginTransaction()
            .replace(R.id.signFragmentContainer, SignIn())
            .commit()

        firebaseAuth = FirebaseAuth.getInstance()
        Log.v("firebase isntance sign in", "${firebaseAuth.currentUser?.uid}")
    }

    override fun onSignInClick(email: String, password: String) {
//        Toast.makeText(this, "${email}, ${password}", Toast.LENGTH_SHORT).show()
        if(isValidEmail(email)){
            firebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener {
                if(it.isSuccessful){
                    val currentUser = firebaseAuth.currentUser
                    val userId = currentUser?.uid ?: ""
                    Log.v("Sign In and Up", "$currentUser, $userId")

                    val intent = Intent(this, MainActivity::class.java)
                    intent.putExtra("USER_UID", userId)
                    startActivity(intent)
                }else{
                    Toast.makeText(this, "Failed to sign in", Toast.LENGTH_SHORT).show()
                }
            }
        }else{
            Toast.makeText(this, "Not Valid email", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onSendLinkClick(email: String) {
        if(isValidEmail(email)){
            firebaseAuth.sendPasswordResetEmail(email).addOnCompleteListener {
                if(it.isSuccessful){
                    Toast.makeText(this, "Link Sent", Toast.LENGTH_SHORT).show()
                    switchFragment(SignIn())
                }
            }
        }else{
            Toast.makeText(this, "Not Valid email", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onSignUpClick(
        fName: String,
        lName: String,
        email: String,
        password: String,
        passwordConfirm: String
    ) {
        if(isValidEmail(email)){
            if(isValidPassword(password)){
                if(password == passwordConfirm){
                    firebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener{
                        if (it.isSuccessful){
                            val currentUser = firebaseAuth.currentUser
                            val userId = currentUser?.uid ?: ""
                            Log.v("Sign In and Up", "$currentUser, $userId")
                            switchFragment(SignIn())
                        }else{
                            Toast.makeText(this, "Could not create user", Toast.LENGTH_SHORT).show()
                        }
                    }
                }else{
                    Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show()
                }
            }else{
                Snackbar.make(findViewById(android.R.id.content), "Invalid Password, must be 8 character long and have one capital letter and one special symbol", Snackbar.LENGTH_LONG).show()
            }
        }else{
            Toast.makeText(this, "Not Valid email", Toast.LENGTH_SHORT).show()
        }
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

    fun isValidEmail(email: String): Boolean{
        val emailPattern = Regex("^\\w+([.-]?\\w+)*@\\w+([.-]?\\w+)*(\\.\\w{2,})+\$")
        return emailPattern.matches(email)
    }
    fun isValidPassword(password: String): Boolean {
        val passwordPattern = Regex("^(?=.*[A-Z])(?=.*[!@#\$%^&*()-+])(?=\\S+\$).{8,}\$")
        return passwordPattern.matches(password)
    }
}