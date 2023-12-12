package com.example.completionist.ProfiePage

import android.content.Context
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import androidx.fragment.app.Fragment
import com.example.completionist.OnNavigationItemClickListener
import com.example.completionist.R
import com.example.completionist.User


class ChangeEmailSettings : Fragment(R.layout.fragment_change_email_settings) {

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

        val emailEditBox = view.findViewById<EditText>(R.id.emailSettings)
        val saveEmailButton = view.findViewById<Button>(R.id.saveEmailSettings)

        val args = arguments
        if(args!=null){
            val currentUserData = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                args.getSerializable("USER_DATA", User::class.java) as? User
            } else {
                args.getSerializable("USER_DATA") as? User
            }
            emailEditBox.setText(currentUserData?.email)
        }

        saveEmailButton.setOnClickListener{listener?.onEmailSaveClicked(emailEditBox.text.toString())}
    }

}