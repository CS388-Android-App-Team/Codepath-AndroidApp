package com.example.completionist.ProfiePage

import android.content.Context
import android.os.Bundle
import android.provider.ContactsContract.CommonDataKinds.Im
import androidx.fragment.app.Fragment
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.example.completionist.OnNavigationItemClickListener
import com.example.completionist.R

class ProfilePage : Fragment(R.layout.fragment_profile_page) {

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
        val signOutButton = view.findViewById<View>(R.id.signOutButtonProfile)

        val userName = view.findViewById<TextView>(R.id.username_profilepage)
        val userImage = view.findViewById<ImageView>(R.id.userImage_profilepage)
        val userPoints = view.findViewById<TextView>(R.id.points_profilepage)
        val userPartySize = view.findViewById<TextView>(R.id.partysize_profilepage)

        val settingsIcon = view.findViewById<ImageView>(R.id.settingsicon_profilepage)

        homePageNav.setOnClickListener{
            listener?.onHomeClicked()
        }
        taskPageNav.setOnClickListener{
            listener?.onTaskClicked()
        }
        profilePageNav.setOnClickListener{
            listener?.onProfileClicked()
        }
        signOutButton.setOnClickListener{
            listener?.onSignOutClicked()
        }
        settingsIcon.setOnClickListener(){listener?.onSettingsClicked()}

    }
}