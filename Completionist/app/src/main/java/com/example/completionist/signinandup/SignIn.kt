package com.example.completionist.signinandup

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import com.example.completionist.OnNavigationItemClickListener
import com.example.completionist.R
import com.example.completionist.SignInAndUpClickListener

class SignIn : Fragment(R.layout.fragment_sign_in) {

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

        val emailEditBox = view.findViewById<EditText>(R.id.emailSignIn)
        val passwordEditBox = view.findViewById<EditText>(R.id.passwordSignIn)
        val signInButton = view.findViewById<Button>(R.id.signinbuttonSI)
        val forgotPasswordLink = view.findViewById<TextView>(R.id.forgotpassword_signin)
        val signUpLink = view.findViewById<TextView>(R.id.signuplink_signin)

        signInButton.setOnClickListener {
            listener?.onSignInClick()
        }
        forgotPasswordLink.setOnClickListener {
            listener?.onForgotPasswordClick()
        }
        signUpLink.setOnClickListener {
            listener?.onRegisterClick()
        }
    }

}