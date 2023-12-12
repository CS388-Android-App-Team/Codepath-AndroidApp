package com.example.completionist

import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.view.View

class SplashScreen : AppCompatActivity() {

    private val SPLASH_DELAY: Long = 1500 // Delay in milliseconds (e.g., 2000ms = 2 seconds)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)



        findViewById<View>(R.id.splashscreen).postDelayed({
            //        once room database setup change logic to check room DB for login status
            val isLoggedIn = false

            if(!isLoggedIn){
                navigateToSignInActivity()
            }else{
                if(isConnectedToInternet(this)){
                    validateLoginCredentials()
                }else{
                    navigateToMainActivity()
                }
            }
        }, SPLASH_DELAY)
    }

    private fun navigateToMainActivity() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun validateLoginCredentials() {
        TODO("Not yet implemented")
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