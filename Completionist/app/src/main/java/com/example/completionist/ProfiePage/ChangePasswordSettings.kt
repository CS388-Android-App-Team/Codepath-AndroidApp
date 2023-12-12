package com.example.completionist.ProfiePage

import android.content.Context
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import androidx.fragment.app.Fragment
import com.example.completionist.OnNavigationItemClickListener
import com.example.completionist.R


class ChangePasswordSettings : Fragment(R.layout.fragment_change_password_settings) {

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

        val passwordEditText = view.findViewById<EditText>(R.id.passwordSettings)
        val passwordOldEditText = view.findViewById<EditText>(R.id.passwordOldSettings)
        val passwordConfirmEditText = view.findViewById<EditText>(R.id.passwordConfirmSettings)
        val savePasswordButton = view.findViewById<Button>(R.id.savePasswordSettings)

        savePasswordButton.setOnClickListener{listener?.onPasswordChangeClick(passwordEditText.text.toString(), passwordConfirmEditText.text.toString(), passwordOldEditText.text.toString())}
    }

}