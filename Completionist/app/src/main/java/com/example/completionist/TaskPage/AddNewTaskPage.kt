package com.example.completionist.TaskPage

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
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
        val charCountTextView = findViewById<TextView>(R.id.char_count_text)

        val acceptButton = findViewById<ImageView>(R.id.accept_button)
        val declineButton = findViewById<ImageView>(R.id.decline_button)

        val formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy")

        // TextWatcher to update character count in real-time
        questName.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                val remainingChars = 16 - s?.length!! ?: 0
                charCountTextView.text = "Characters left: $remainingChars"
            }
        })

        acceptButton.setOnClickListener {
            val questNameText = questName.text.toString()

            // Check if questName is empty
            if (questNameText.isEmpty()) {
                showCustomToast(this, "Please enter a Quest Name")
                return@setOnClickListener
            }

            // Check questName length
            if (questNameText.length > 16) {
                showCustomToast(this, "Quest name must be 16 characters or less")
                return@setOnClickListener
            }

            // Check if questPoints is a valid number between 1 and 10
            val questPointsText = questPoints.text.toString().toIntOrNull()
            if (questPointsText == null || questPointsText !in 1..10) {
                showCustomToast(this, "Please enter a valid quest points number between 1 and 10")
                return@setOnClickListener
            }

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


