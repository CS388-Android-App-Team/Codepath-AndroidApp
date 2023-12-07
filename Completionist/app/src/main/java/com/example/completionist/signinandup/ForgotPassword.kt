package com.example.completionist.signinandup

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import com.example.completionist.R
import com.example.completionist.SignInAndUpClickListener

class ForgotPassword : Fragment(R.layout.fragment_forgot_password) {

    private var listener: SignInAndUpClickListener? = null
    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is SignInAndUpClickListener) {
            listener = context
        } else {
            throw RuntimeException("$context must implement SignInAndUpClickListener")
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val emailEditBox = view.findViewById<EditText>(R.id.emailFP)

        val sendLinkButton = view.findViewById<Button>(R.id.SendLinkFP)
        val backButton = view.findViewById<ImageView>(R.id.backArrowForgotPassword)

        sendLinkButton.setOnClickListener { listener?.onSendLinkClick() }
        backButton.setOnClickListener{listener?.onBackClick()}

    }
}