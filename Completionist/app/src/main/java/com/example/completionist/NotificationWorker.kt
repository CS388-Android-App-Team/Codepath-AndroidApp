package com.example.completionist

import android.Manifest
import android.app.Activity
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import androidx.annotation.NonNull
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.firebase.ui.auth.AuthUI.getApplicationContext


class NotificationWorker(context: Context, workerParams: WorkerParameters) :
    Worker(context, workerParams) {
    @NonNull
    override fun doWork(): Result {
        // Create and display the notification
        createNotificationChannel(applicationContext)
        createNotification(applicationContext)
        return Result.success()
    }

    private fun createNotification(context: Context) {
        val notificationId = System.currentTimeMillis().toInt()

        val notificationBuilder = NotificationCompat.Builder(context, "my_channel_id")
            .setContentTitle("Complete your tasks!")
            .setContentText("Don't forget to complete your tasks for the day")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setSmallIcon(R.mipmap.ic_launcher_round)

        // Check if the app has the required permission to post notifications
        if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // If the permission is not granted, request it from the user
            val PERMISSION_REQUEST_CODE = 123
            ActivityCompat.requestPermissions(
                context as Activity, // Assuming 'context' is an Activity
                arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                PERMISSION_REQUEST_CODE // Define a constant integer for the request code
            )
        }
        // If the permission is granted, post the notification
        with(NotificationManagerCompat.from(context)) {
            notify(notificationId, notificationBuilder.build())
        }
    }

    private fun createNotificationChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channelId = "my_channel_id"
            val channelName = "My Channel"
            val desc = "Notification Description"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val notificationChannel = NotificationChannel(channelId, channelName, importance).apply{
                description=desc
            }

            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(notificationChannel)

            Log.v("Notification", "Notification Channel Created")
        }
    }
}