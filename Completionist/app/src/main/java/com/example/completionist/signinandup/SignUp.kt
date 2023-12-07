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

class SignUp : Fragment(R.layout.fragment_sign_up) {

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

        val firstNameEditBox = view.findViewById<EditText>(R.id.FirstNameSU)
        val lastNameEditBox = view.findViewById<EditText>(R.id.LastNameSU)
        val emailEditBox = view.findViewById<EditText>(R.id.EmailSU)
        val passwordEditBox = view.findViewById<EditText>(R.id.PasswordSU)
        val passwordCEditBox = view.findViewById<EditText>(R.id.PasswordCSU)

        val signUpButton = view.findViewById<Button>(R.id.SingUpButtonSU)
        val backButton = view.findViewById<ImageView>(R.id.backArrowSignUp)

        signUpButton.setOnClickListener {
            listener?.onSignUpClick()
        }
        backButton.setOnClickListener{
            listener?.onBackClick()
        }
    }
}