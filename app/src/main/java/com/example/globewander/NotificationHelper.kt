package com.example.globewander

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat

class NotificationHelper(private val context: Context) {

    // Static variables for notification channel properties
    companion object {
        private const val CHANNEL_ID = "faq_channel_id"
        private const val CHANNEL_NAME = "FAQ Notifications"
        private const val CHANNEL_DESCRIPTION = "Notifications for new FAQs"
    }

    // Initializer block to create the notification channel when an instance is created
    init {
        createNotificationChannel()
    }

    // Method to create a notification channel (for Android Oreo and above)
    private fun createNotificationChannel() {
        // Check if the Android version is Oreo or higher
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            // Create a new notification channel with the specified id, name, and importance
            val channel = NotificationChannel(CHANNEL_ID, CHANNEL_NAME, importance).apply {
                description = CHANNEL_DESCRIPTION
            }
            // Get the notification manager from the system services
            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            // Create the notification channel using the notification manager
            notificationManager.createNotificationChannel(channel)
        }
    }

    // Method to display a notification with specified title, content, and icon
    fun showNotification(title: String, content: String, icon: Int) {
        // Get the notification manager from the system services
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        // Build the notification with the specified title, content, and icon
        val builder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(icon) // Use icon passed as parameter
            .setContentTitle(title)
            .setContentText(content)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)

        // Notify the user with the built notification
        // The id for the notification is a unique integer derived from the current time
        notificationManager.notify((System.currentTimeMillis() and 0xfffffff).toInt(), builder.build())
    }
}