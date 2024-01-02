package com.example.wodbook

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider.getApplicationContext
import com.example.wodbook.data.WOD
import com.example.wodbook.data.WodDatabase
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import java.util.Date

class TaskDaoTest {
    private lateinit var database: WodDatabase

    @Before
    fun initDb() {
        database = Room.inMemoryDatabaseBuilder(
            getApplicationContext(),
            WodDatabase::class.java
        ).allowMainThreadQueries().build()
    }

    @Test
    fun insertWodAndGetWods() = runTest {
        val firebaseUid = "user123" // Replace with actual Firebase UID
        val picturePath = "/path/to/picture.jpg" // Replace with actual file path or URI
        val currentDateTime = Date() // This will set dateTime to the current time and date
        val doItAgain = true // or false, depending on your requirement
        val notes = "This was a great workout!"

        val wod = WOD(
            firebaseUid = firebaseUid,
            picture = picturePath,
            dateTime = currentDateTime,
            doItAgain = doItAgain,
            notes = notes
        )
        database.wodDao().upsert(wod)

        val wods = database.wodDao().getWodsByUser(firebaseUid)

        // Add code to test that one and only one wod is provided by the data source and it is the same wod which was inserted.
        assertEquals(1, wods.size)

        val retrievedWod = wods[0]
        assertEquals(firebaseUid, retrievedWod.firebaseUid)
        assertEquals(picturePath, retrievedWod.picture)
        assertEquals(doItAgain, retrievedWod.doItAgain)
        assertEquals(notes, retrievedWod.notes)
    }


}

