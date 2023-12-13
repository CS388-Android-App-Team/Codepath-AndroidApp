package com.example.completionist

import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import androidx.lifecycle.ViewModelProvider
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.database

class SplashScreen : AppCompatActivity() {

    private val SPLASH_DELAY: Long = 1500 // Delay in milliseconds (e.g., 2000ms = 2 seconds)
    private lateinit var userViewModel: UserViewModel // Initialize the UserViewModel variable
    private lateinit var firebaseAuth: FirebaseAuth // Initialize FirebaseAuth variable


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)

        firebaseAuth = FirebaseAuth.getInstance()

        userViewModel = ViewModelProvider(this).get(UserViewModel::class.java)


        findViewById<View>(R.id.splashscreen).postDelayed({
            //        once room database setup change logic to check room DB for login status
            var isLoggedIn = false

            userViewModel.allUsers.observe(this) { users ->
                Log.v("Splash-screen users", "${users}")
                if(!users.isEmpty()){
                    Log.v("Splash-screen users", "user list is not empty")
                    isLoggedIn = true
                }
                if(!isLoggedIn){
                    navigateToSignInActivity()
                }else{
                    if(isConnectedToInternet(this)){
                        validateLoginCredentials(users[0].idToken)
                    }else{
                        navigateToMainActivity()
                    }
                }
            }

        }, SPLASH_DELAY)
    }

    private fun navigateToMainActivity() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun validateLoginCredentials(userId: String) {
        val database = Firebase.database
        val usersRef = database.getReference("users")
        val currentUserFB = firebaseAuth.currentUser

        usersRef.child(userId).get().addOnSuccessListener { dataSnapshot ->
            if (dataSnapshot.exists() && currentUserFB != null) {
                // User exists in the Realtime Database
                navigateToMainActivity()
            } else {
                // User does not exist in the Realtime Database
                navigateToSignInActivity()
            }
        }.addOnFailureListener { exception ->
            // Error occurred while fetching data, navigate to SignInActivity as a fallback
            Log.e("Firebase", "Error validating user credentials", exception)
            navigateToSignInActivity()
        }
    }

    private fun isConnectedToInternet(context: Context): Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            val network = connectivityManager.activeNetwork ?: return false
            val capabilities = connectivityManager.getNetworkCapabilities(network) ?: return false

            return capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
        }else{
            val networkInfo = connectivityManager.activeNetworkInfo
            return networkInfo?.isConnected ?: false
        }
    }

    private fun navigateToSignInActivity() {
        val intent = Intent(this, SignInAndUp::class.java)
        startActivity(intent)
        finish()
    }
}