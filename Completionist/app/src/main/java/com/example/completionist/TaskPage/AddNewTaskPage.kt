package com.example.completionist.TaskPage

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.example.completionist.R
import com.google.firebase.auth.FirebaseAuth
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class AddNewTaskPage : AppCompatActivity() {

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.task_add_new_quest_page)

        val questName = findViewById<EditText>(R.id.quest_name)
        val questPoints = findViewById<EditText>(R.id.XP_amount)
        val questDate = findViewById<EditText>(R.id.quest_date)

        val acceptButton = findViewById<ImageView>(R.id.accept_button)
        val declineButton = findViewById<ImageView>(R.id.decline_button)

        val formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy")

        acceptButton.setOnClickListener {
            val questNameText = questName.text.toString()
            val questPointsText = questPoints.text.toString().toInt()

            // Check if quest_date is empty
            val questDateText = questDate.text.toString()

            // Validate the date
            if (questDateText.isNotEmpty() && !isValidDate(questDateText)) {
                // Show an error message or handle the invalid date case
                // For example, you can show a Toast message
                showCustomToast(this, "Please enter a valid date")
                return@setOnClickListener
            }

            // If quest_date is empty, use the current selected date as default
            val finalQuestDate = if (questDateText.isNotEmpty()) {
                questDateText
            } else {
                intent.getStringExtra("CURRENT_DATE")
            }

            // Get the user ID from your authentication system (Firebase, etc.)
            val userId = getCurrentUserId() // Replace this with your actual method to get the user ID

            val resultIntent = Intent()
            resultIntent.putExtra("QUEST_NAME", questNameText)
            resultIntent.putExtra("QUEST_POINTS", questPointsText)
            resultIntent.putExtra("QUEST_DATE", finalQuestDate)
            resultIntent.putExtra("USER_ID", userId)

            setResult(Activity.RESULT_OK, resultIntent)
            finish()
        }


        declineButton.setOnClickListener {
            onBackPressed()
        }
    }

    // Replace this with your actual method to get the user ID (Firebase, etc.)
    private fun getCurrentUserId(): String {
        // Assuming you have the Firebase user
        val firebaseUser = FirebaseAuth.getInstance().currentUser
        return firebaseUser?.uid ?: ""
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun isValidDate(dateText: String): Boolean {
        val currentDate = LocalDate.now()
        val enteredDate = LocalDate.parse(dateText, DateTimeFormatter.ofPattern("MM/dd/yyyy"))
        return !enteredDate.isBefore(currentDate)
    }

    fun showCustomToast(context: Context, message: String) {
        val builder = AlertDialog.Builder(context)
        builder.setMessage(message)
        builder.setPositiveButton("OK") { dialog, _ ->
            dialog.dismiss()
        }

        val dialog = builder.create()
        dialog.show()
    }
}


