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


class ChangeEmailSettings : Fragment(R.layout.fragment_change_email_settings) {

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

        val emailEditBox = view.findViewById<EditText>(R.id.emailSettings)
        val saveEmailButton = view.findViewById<Button>(R.id.saveEmailSettings)

        val uid = firebaseAuth.currentUser?.uid ?: ""
        userViewModel.getUserById(uid).observe(viewLifecycleOwner){user ->
            emailEditBox.setText(user?.email)
        }

        saveEmailButton.setOnClickListener{listener?.onEmailSaveClicked(emailEditBox.text.toString())}
    }

}