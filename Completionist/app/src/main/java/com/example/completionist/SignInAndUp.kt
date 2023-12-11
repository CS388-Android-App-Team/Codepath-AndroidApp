package com.example.completionist

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.completionist.signinandup.ForgotPassword
import com.example.completionist.signinandup.SignIn
import com.example.completionist.signinandup.SignUp
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.database

class SignInAndUp : AppCompatActivity(), SignInAndUpClickListener {

    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var database: FirebaseDatabase
    private lateinit var usersRef: DatabaseReference

    public lateinit var currentUserData: User

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_in_and_up)
        supportFragmentManager.beginTransaction()
            .replace(R.id.signFragmentContainer, SignIn())
            .commit()

        firebaseAuth = FirebaseAuth.getInstance()
        Log.v("firebase isntance sign in", "${firebaseAuth.currentUser?.uid}")
        database = Firebase.database
        usersRef = database.getReference("users")
    }

    override fun onSignInClick(email: String, password: String) {
//        Toast.makeText(this, "${email}, ${password}", Toast.LENGTH_SHORT).show()
        if(isValidEmail(email)){
            firebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener {
                if(it.isSuccessful){
                    val currentUser = firebaseAuth.currentUser
                    val userId = currentUser?.uid ?: ""
                    Log.v("Sign In and Up", "$currentUser, $userId")

//                    Get current user information from realtime DB
                    getUserInfo(userId){user ->
                        if(user!=null){
                            val userData = User(user.idToken, user.username, user.email, user.firstName, user.lastName, user.level, user.xp, user.streak, user.consistency, user.friendCount)

                            val intent = Intent(this, MainActivity::class.java)
                            intent.putExtra("USER_UID", userId)
                            intent.putExtra("USER_DATA", userData)
                            startActivity(intent)
                        }else{
                            Log.e("User Info", "Failed to fetch data")
                        }
                    }

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
        passwordConfirm: String,
        userName: String
    ) {
        if(isValidEmail(email)){
            if(isValidPassword(password)){
                if(password == passwordConfirm){
                    isValidUserName(userName) { isValid ->
                        if (isValid) {
                            firebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener{
                                if (it.isSuccessful){
                                    val currentUser = firebaseAuth.currentUser
                                    val userId = currentUser?.uid ?: ""
                                    if (currentUser != null) {
                                        writeNewUser(currentUser, email, fName, lName, userName)
                                    }
                                    Log.v("Sign In and Up", "$currentUser, $userId")
                                    Toast.makeText(this, "Account Created", Toast.LENGTH_SHORT).show()
                                    switchFragment(SignIn())
                                }else{
                                    Toast.makeText(this, "Could not create user", Toast.LENGTH_SHORT).show()
                                }
                            }
                        } else {
                            Toast.makeText(this, "Username already exists", Toast.LENGTH_SHORT).show()
                        }
                    }
                }else{
                    Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show()
                }
            }else{
                Toast.makeText(this, "Invalid Password, must be 8 character long and have one capital letter and one special symbol", Toast.LENGTH_SHORT).show()
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
    fun writeNewUser(currUser: FirebaseUser, newEmail: String, fName: String, lName: String, uName: String) {
        val user = User(currUser.uid, uName, newEmail, fName, lName,0, 0, 0, 0, 0)
        usersRef.child(currUser.uid).setValue(user)
        usersRef.child(currUser.uid).child("friends").child("testUID").setValue(false)
    }
    fun getUserInfo(uid: String, callback: (User?) -> Unit) {
        usersRef.child(uid).get().addOnSuccessListener { dataSnapshot ->
            val user = dataSnapshot.getValue(User::class.java)
            callback(user)
        }.addOnFailureListener { exception ->
            callback(null) // Sending null in case of failure
            Log.e("firebase-signin", "Error getting data", exception)
        }
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

}