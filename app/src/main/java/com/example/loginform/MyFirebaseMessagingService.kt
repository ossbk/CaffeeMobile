package com.example.loginform

import android.app.NotificationChannel
import android.util.Log
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class MyFirebaseMessagingService : FirebaseMessagingService() {
    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)
        Log.d("TAG", "onMessageReceived: ${message.notification?.title}")
        message.notification?.let {
            showNotification(it.title.toString(), it.body.toString())
        }
    }

    override fun onNewToken(token: String) {
        super.onNewToken(token)
    }

    private fun showNotification(toString: String, toString1: String) {
        val notificationChannel = NotificationChannel(
            "100",
            "Order Updates",
            android.app.NotificationManager.IMPORTANCE_HIGH
        )

        val notificationManager = getSystemService(android.app.NotificationManager::class.java)
        notificationManager.createNotificationChannel(notificationChannel)
        val notificationHelper = NotificationCompat.Builder(applicationContext, "100")
            .setSmallIcon(R.drawable.cake)
            .setContentTitle(toString)
            .setContentText(toString1)
            .build()

        notificationManager.notify(10, notificationHelper)
    }
}