package com.example.completionist.signinandup

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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


    }

}