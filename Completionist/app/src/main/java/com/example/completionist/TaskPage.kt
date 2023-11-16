package com.example.completionist

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
class TaskPage : Fragment(R.layout.fragment_task_page) {

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

        homePageNav.setOnClickListener{
            listener?.onHomeClicked()
        }
        taskPageNav.setOnClickListener{
            listener?.onTaskClicked()
        }
        profilePageNav.setOnClickListener{
            listener?.onProfileClicked()
        }
    }
}