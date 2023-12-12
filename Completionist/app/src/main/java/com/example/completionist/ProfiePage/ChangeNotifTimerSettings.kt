package com.example.completionist.ProfiePage

import android.content.Context
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.completionist.OnNavigationItemClickListener
import com.example.completionist.R


class ChangeNotifTimerSettings : Fragment(R.layout.fragment_change_notif_timer_settings) {

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

        val hourSpinner = view.findViewById<Spinner>(R.id.hourSpinner)
        val minuteSpinner = view.findViewById<Spinner>(R.id.minuteSpinner)
        val ampmSpinner = view.findViewById<Spinner>(R.id.amPmSpinner)

        val saveButton = view.findViewById<Button>(R.id.saveTimerSettings)

        saveButton.setOnClickListener {
            val sharedPrefs = requireActivity().getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE)
            val editor = sharedPrefs.edit()

            val selectedHour = hourSpinner.selectedItem.toString().toInt()
            val selectedMinute = minuteSpinner.selectedItem.toString().toInt()
            val selectedAmPm = ampmSpinner.selectedItem.toString()

            // Convert 12-hour format to 24-hour format
            var hourOfDay = selectedHour
            if (selectedAmPm.equals("PM", ignoreCase = true) && selectedHour < 12) {
                hourOfDay += 12
            } else if (selectedAmPm.equals("AM", ignoreCase = true) && selectedHour == 12) {
                hourOfDay = 0
            }
            // Save the 24-hour formatted time to SharedPreferences
            editor.putInt("notification_hour", hourOfDay)
            editor.putInt("notification_minute", selectedMinute)
            editor.apply()
            Toast.makeText(requireActivity(), "Timer updated", Toast.LENGTH_SHORT).show()
            listener?.onReminderSaveClick()
        }
    }

}