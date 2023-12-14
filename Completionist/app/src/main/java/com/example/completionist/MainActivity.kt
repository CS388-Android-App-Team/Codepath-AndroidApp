package com.example.completionist

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.work.PeriodicWorkRequest
import androidx.work.WorkManager
import com.example.completionist.HomePage.HomePage
import com.example.completionist.ProfiePage.ProfilePage
import com.example.completionist.ProfiePage.SettingsPage
import com.example.completionist.Quests.Quest
import com.example.completionist.Quests.QuestViewModel
import com.example.completionist.Quests.QuestViewModelFactory
import com.example.completionist.TaskPage.TaskPage
import com.google.firebase.Firebase
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.database
import java.util.Calendar
import java.util.concurrent.TimeUnit

class MainActivity : AppCompatActivity(), OnNavigationItemClickListener {
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var recievedIntent: Intent
    var currentUserData: User? = null

    private lateinit var databaseFirebase: FirebaseDatabase
    private lateinit var usersRef: DatabaseReference

    private lateinit var userViewModel: UserViewModel // Initialize the UserViewModel variable

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        scheduleDailyNotification()
//        sendBroadcast(Intent(this, NotificationReceiver::class.java))

        userViewModel = ViewModelProvider(this).get(UserViewModel::class.java)

        supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, HomePage())
            .commit()

        firebaseAuth = FirebaseAuth.getInstance()

         if(firebaseAuth.currentUser == null){
             onSignOutClicked()
         }

        recievedIntent = intent;

//         currentUserData = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
//             recievedIntent.getSerializableExtra("USER_DATA", User::class.java) as? User
//         } else {
//             recievedIntent.getSerializableExtra("USER_DATA") as? User
//         }
//        currentUserData = userViewModel.getUserById(firebaseAuth.currentUser?.uid)

        currentUserData = null

         databaseFirebase = Firebase.database
         usersRef = databaseFirebase.getReference("users")

        Log.v("MainActivity UID", "${firebaseAuth.currentUser?.uid}, recieved value: ${recievedIntent.getStringExtra("USER_UID")}" )

     }

    override fun onResume() {
        super.onResume()
        firebaseAuth = FirebaseAuth.getInstance()

        databaseFirebase = Firebase.database
        usersRef = databaseFirebase.getReference("users")

        if(firebaseAuth.currentUser == null){
            onSignOutClicked()
        }
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
//            bundle.putSerializable("USER_DATA", currentUserData)
//            switchFragment(ProfilePage(), bundle)
            switchFragment(ProfilePage())

        }else{
            Log.v("NavBar", "Already Profile")
        }
    }
    override fun onSignOutClicked() {
        Log.v("NavBar", "Sign In Clicked")
        firebaseAuth.signOut()
        userViewModel.deleteAllUsers()
        val intent = Intent(this, SplashScreen::class.java)
        startActivity(intent)
    }

    override fun onSettingsClicked() {
        Log.v("Nav", "Settings Clicked")
        val currentFragment = supportFragmentManager.findFragmentById(R.id.fragmentContainer)
        if (currentFragment !is SettingsPage) {
            val bundle = Bundle()
//            bundle.putSerializable("USER_DATA", currentUserData)
//            switchFragment(SettingsPage(), bundle)
            switchFragment(SettingsPage())

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
                            userViewModel.updateEmailById(userId, email)
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
                        userViewModel.updateFirstNameById(userId, fName)
                        userViewModel.updateLastNameById(userId, lName)
                        userViewModel.updateUsernameById(userId, uName)
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

    override fun onReminderSaveClick() {
        scheduleDailyNotification()
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

    fun scheduleDailyNotification() {
        val currentTime = Calendar.getInstance()
        val initialDelay = calculateInitialDelay(currentTime)

        val notificationWorkRequest =
            PeriodicWorkRequest.Builder(NotificationWorker::class.java, 24, TimeUnit.HOURS)
                .setInitialDelay(initialDelay, TimeUnit.MILLISECONDS)
                .build()

        WorkManager.getInstance(this).enqueue(notificationWorkRequest)
    }

    private fun calculateInitialDelay(currentTime: Calendar): Long {
        val sharedPreferences = getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE)
        val savedNotificationTime = getNotificationTime(this)
        val savedHour = savedNotificationTime.first
        val savedMinute = savedNotificationTime.second

        val calendar = Calendar.getInstance().apply {
            timeInMillis = System.currentTimeMillis()
            set(Calendar.HOUR_OF_DAY, savedHour) // Set the hour to 3 AM
            set(Calendar.MINUTE, savedMinute)
            set(Calendar.SECOND, 0)

            // Check if the current time is after 3 AM, if so, set for the next day
            if (before(currentTime) || equals(currentTime)) {
                add(Calendar.DAY_OF_MONTH, 1)
            }
        }

        return calendar.timeInMillis - currentTime.timeInMillis
    }

    // Retrieve notification time from SharedPreferences
    fun getNotificationTime(context: Context): Pair<Int, Int> {
        val sharedPrefs = context.getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE)
        val hourOfDay = sharedPrefs?.getInt("notification_hour", -1) ?: 12
        val minute = sharedPrefs?.getInt("notification_minute", -1) ?: 0
        return Pair(hourOfDay, minute)
    }




}