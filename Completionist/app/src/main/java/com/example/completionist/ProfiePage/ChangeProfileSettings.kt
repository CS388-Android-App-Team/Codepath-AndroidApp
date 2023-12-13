package com.example.completionist.ProfiePage

import android.content.Context
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.completionist.OnNavigationItemClickListener
import com.example.completionist.R
import com.example.completionist.User
import com.example.completionist.UserViewModel
import com.google.firebase.auth.FirebaseAuth
import kotlin.math.log


class ChangeProfileSettings : Fragment(R.layout.fragment_change_profile_settings) {

    private var listener: OnNavigationItemClickListener? = null

    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var userViewModel: UserViewModel // Initialize the UserViewModel variable

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is OnNavigationItemClickListener) {
            listener = context
        } else {
            throw RuntimeException("$context must implement OnNavigationItemClickListener")
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        firebaseAuth = FirebaseAuth.getInstance()
        userViewModel = ViewModelProvider(this).get(UserViewModel::class.java)

        val usernameEditText = view.findViewById<EditText>(R.id.usernameSettings)
        val firstnameEditText = view.findViewById<EditText>(R.id.FirstNameSettings)
        val lastnameEditText = view.findViewById<EditText>(R.id.LastNameSettings)

        val saveProfileButton = view.findViewById<Button>(R.id.saveProfileSettings)

        val uid = firebaseAuth.currentUser?.uid ?: ""
        userViewModel.getUserById(uid).observe(viewLifecycleOwner){user ->
            usernameEditText.setText(user?.username)
            firstnameEditText.setText(user?.firstName)
            lastnameEditText.setText(user?.lastName)
        }

        saveProfileButton.setOnClickListener{listener?.onProfileSaveClicked(firstnameEditText.text.toString(), lastnameEditText.text.toString(), usernameEditText.text.toString())}
    }

}