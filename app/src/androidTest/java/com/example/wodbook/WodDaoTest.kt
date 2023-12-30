package com.example.wodbook

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider.getApplicationContext
import com.example.wodbook.data.WodDatabase
import org.junit.Before

class TaskDaoTest {
    private lateinit var database: WodDatabase

    @Before
    fun initDb() {
        database = Room.inMemoryDatabaseBuilder(
            getApplicationContext(),
            WodDatabase::class.java
        ).allowMainThreadQueries().build()
    }

}