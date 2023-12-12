package com.example.completionist

import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.completionist.HomePage.HomePage
import com.example.completionist.ProfiePage.ProfilePage
import com.example.completionist.ProfiePage.SettingsPage
import com.example.completionist.TaskPage.TaskPage
import com.google.firebase.Firebase
import com.google.firebase.auth.EmailAuthProvider
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

         if(firebaseAuth.currentUser == null){
             onSignOutClicked()
         }

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

    override fun onResume() {
        super.onResume()
        firebaseAuth = FirebaseAuth.getInstance()

        currentUserData = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            recievedIntent.getSerializableExtra("USER_DATA", User::class.java) as? User
        } else {
            recievedIntent.getSerializableExtra("USER_DATA") as? User
        }

        database = Firebase.database
        usersRef = database.getReference("users")

        if(firebaseAuth.currentUser == null){
            onSignOutClicked()
        }
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
            val bundle = Bundle()
            bundle.putSerializable("USER_DATA", currentUserData)
            switchFragment(SettingsPage(), bundle)
        }else{
            Log.v("NavBar", "Already Settings")
        }
    }

    override fun onEmailSaveClicked(email: String) {
//        validate email and save to firebase auth and realtime DB
        if(isValidEmail(email)){
            val user = FirebaseAuth.getInstance().currentUser
            user?.verifyBeforeUpdateEmail(email)?.addOnCompleteListener {
                if(it.isSuccessful){
                    val currentUser = firebaseAuth.currentUser
                    val userId = currentUser?.uid ?: ""
                    if (currentUser != null) {
                        val userData = hashMapOf<String, Any>("email" to email)
                        usersRef.child(userId).updateChildren(userData).addOnSuccessListener {
                            Toast.makeText(this, "Verification email sent", Toast.LENGTH_SHORT).show()
                            updateCurrentUser(newEmail = email)
                        }.addOnFailureListener{
                            Toast.makeText(this, "Failed to change email", Toast.LENGTH_SHORT).show()
                        }
                    }
                }else{
                    Toast.makeText(this, "Failed to change email", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    override fun onProfileSaveClicked(fName: String, lName: String, uName: String) {
        isValidUserName(uName) { isValid ->
            if (isValid) {
                val user = firebaseAuth.currentUser
                val userId = user?.uid ?: ""

                // Update data in Firebase Realtime Database
                val userData = hashMapOf<String, Any>(
                    "firstName" to fName,
                    "lastName" to lName,
                    "username" to uName
                )
                usersRef.child(userId).updateChildren(userData)
                    .addOnSuccessListener {
                        // Update local currentUserData
                        currentUserData?.apply {
                            firstName = fName
                            lastName = lName
                            username = uName
                        }

                        // Notify the user about the successful update
                        Toast.makeText(this, "Profile updated successfully", Toast.LENGTH_SHORT).show()
                    }
                    .addOnFailureListener {
                        // Notify the user about the failure
                        Toast.makeText(this, "Failed to update profile", Toast.LENGTH_SHORT).show()
                        Log.e("Firebase", "Failed to update profile", it)
                    }
            } else {
                // Notify the user that the username already exists
                Toast.makeText(this, "Username already exists", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onPasswordChangeClick(password: String, passwordC: String, passwordOld: String) {
        val user = FirebaseAuth.getInstance().currentUser
        val newPassword = password.trim()

        // Check if the new password and its confirmation match
        if (newPassword == passwordC.trim() && isValidPassword(newPassword)) {
            // Create credentials with user's email and current password
            val credential = EmailAuthProvider.getCredential(user?.email ?: "", passwordOld)

            // Re-authenticate user with current credentials
            user?.reauthenticate(credential)?.addOnCompleteListener { reAuthTask ->
                if (reAuthTask.isSuccessful) {
                    // User re-authenticated successfully, proceed to change password
                    user.updatePassword(newPassword).addOnCompleteListener { passwordUpdateTask ->
                        if (passwordUpdateTask.isSuccessful) {
                            // Password updated successfully
                            Toast.makeText(this, "Password changed successfully", Toast.LENGTH_SHORT).show()
                        } else {
                            // Failed to update password
                            Toast.makeText(this, "Failed to change password", Toast.LENGTH_SHORT).show()
                        }
                    }
                } else {
                    // Failed to re-authenticate
                    Toast.makeText(this, "Re-authentication failed", Toast.LENGTH_SHORT).show()
                }
            }
        } else {
            // Passwords do not match
            Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show()
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

    fun isValidEmail(email: String): Boolean{
        val emailPattern = Regex("^\\w+([.-]?\\w+)*@\\w+([.-]?\\w+)*(\\.\\w{2,})+\$")
        return emailPattern.matches(email)
    }
    fun isValidPassword(password: String): Boolean {
        val passwordPattern = Regex("^(?=.*[A-Z])(?=.*[!@#\$%^&*()-+])(?=\\S+\$).{8,}\$")
        return passwordPattern.matches(password)
    }

    fun updateCurrentUser(
        newUsername: String? = null,
        newEmail: String? = null,
        newFirstName: String? = null,
        newLastName: String? = null,
        newLevel: Int? = null,
        newXp: Int? = null,
        newStreak: Int? = null,
        newConsistency: Int? = null,
        newFriendCount: Int? = null
    ) {
        currentUserData?.apply {
            newUsername?.let { username = it }
            newEmail?.let { email = it }
            newFirstName?.let { firstName = it }
            newLastName?.let { lastName = it }
            newLevel?.let { level = it }
            newXp?.let { xp = it }
            newStreak?.let { streak = it }
            newConsistency?.let { consistency = it }
            newFriendCount?.let { friendCount = it }
        }
    }


}