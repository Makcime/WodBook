package com.example.wodbook

import android.app.Application
import com.example.wodbook.data.WodDatabase

class WodBookApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        initializeRoomDatabase()
    }

    private fun initializeRoomDatabase() {
        // Initialize the database
        WodDatabase.getDatabase(this)
    }
}
