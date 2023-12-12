package com.example.completionist.ProfiePage

import android.content.Context
import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import android.widget.Button
import androidx.fragment.app.FragmentContainerView
import androidx.fragment.app.FragmentManager
import com.example.completionist.OnNavigationItemClickListener
import com.example.completionist.R
import com.example.completionist.User

class SettingsPage : Fragment(R.layout.fragment_settings_page) {

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

        val homePageNav = view.findViewById<View>(R.id.home_nav)
        val taskPageNav = view.findViewById<View>(R.id.task_nav)
        val profilePageNav = view.findViewById<View>(R.id.profile_nav)

        val changeEmail = view.findViewById<Button>(R.id.change_email_button)
        val changePassword = view.findViewById<Button>(R.id.change_password_button)
        val changeProfile = view.findViewById<Button>(R.id.changeUserInfo)
        val changeReminder = view.findViewById<Button>(R.id.changeTimerInfo)


        val logoutButton = view.findViewById<Button>(R.id.logout)

        val fragmentManager: FragmentManager = childFragmentManager
        val settingsFragmentContainer = view.findViewById<FragmentContainerView>(R.id.settingsfragmentcontainer)
        val args = arguments


        changeEmail.setOnClickListener{
            val fragment = ChangeEmailSettings()
            fragment.arguments = args
            fragmentManager.beginTransaction()
                .replace(settingsFragmentContainer.id, fragment)
                .addToBackStack(null)
                .commit()
        }

        changePassword.setOnClickListener{
            val fragment = ChangePasswordSettings()
            fragment.arguments = args
            fragmentManager.beginTransaction()
                .replace(settingsFragmentContainer.id, fragment)
                .addToBackStack(null)
                .commit()
        }

        changeProfile.setOnClickListener{
            val fragment = ChangeProfileSettings()
            fragment.arguments = args
            fragmentManager.beginTransaction()
                .replace(settingsFragmentContainer.id, fragment)
                .addToBackStack(null)
                .commit()
        }

        changeReminder.setOnClickListener{
            val fragment = ChangeNotifTimerSettings()
            fragment.arguments = args
            fragmentManager.beginTransaction()
                .replace(settingsFragmentContainer.id, fragment)
                .addToBackStack(null)
                .commit()
        }

        homePageNav.setOnClickListener{
            listener?.onHomeClicked()
        }
        taskPageNav.setOnClickListener{
            listener?.onTaskClicked()
        }
        profilePageNav.setOnClickListener{
            listener?.onProfileClicked()
        }
        logoutButton.setOnClickListener{listener?.onSignOutClicked()}

    }

}