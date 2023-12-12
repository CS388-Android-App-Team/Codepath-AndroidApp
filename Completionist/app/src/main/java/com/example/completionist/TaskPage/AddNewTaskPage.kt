package com.example.completionist.TaskPage

import android.app.Activity
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.widget.EditText
import android.widget.ImageView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.example.completionist.R
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
            val questDateText = if (questDate.text.toString().isNotEmpty()) {
                questDate.text.toString()
            } else {
                // Use the current selected date as default
                val currentDate = intent.getStringExtra("CURRENT_DATE") ?: ""
                currentDate
            }

            val resultIntent = Intent()
            resultIntent.putExtra("QUEST_NAME", questNameText)
            resultIntent.putExtra("QUEST_POINTS", questPointsText)
            resultIntent.putExtra("QUEST_DATE", questDateText)

            setResult(Activity.RESULT_OK, resultIntent)
            finish()
        }

        declineButton.setOnClickListener {
            val intent = Intent(this, TaskPage::class.java)
            startActivity(intent)
        }
    }
}
