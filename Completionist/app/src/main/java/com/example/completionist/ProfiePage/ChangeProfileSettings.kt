package com.example.completionist.ProfiePage

import android.content.Context
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import androidx.fragment.app.Fragment
import com.example.completionist.OnNavigationItemClickListener
import com.example.completionist.R
import com.example.completionist.User
import kotlin.math.log


class ChangeProfileSettings : Fragment(R.layout.fragment_change_profile_settings) {

    private var listener: OnNavigationItemClickListener? = null
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

        val usernameEditText = view.findViewById<EditText>(R.id.usernameSettings)
        val firstnameEditText = view.findViewById<EditText>(R.id.FirstNameSettings)
        val lastnameEditText = view.findViewById<EditText>(R.id.LastNameSettings)

        val saveProfileButton = view.findViewById<Button>(R.id.saveProfileSettings)

        val args = arguments
        if(args!=null){
            val currentUserData = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                args.getSerializable("USER_DATA", User::class.java) as? User
            } else {
                args.getSerializable("USER_DATA") as? User
            }
            Log.v("Settings page", "${currentUserData}")
            usernameEditText.setText(currentUserData?.username)
            firstnameEditText.setText(currentUserData?.firstName)
            lastnameEditText.setText(currentUserData?.lastName)
        }

        saveProfileButton.setOnClickListener{listener?.onProfileSaveClicked(firstnameEditText.text.toString(), lastnameEditText.text.toString(), usernameEditText.text.toString())}
    }

}