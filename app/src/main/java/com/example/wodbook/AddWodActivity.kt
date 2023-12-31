package com.example.wodbook

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.lifecycleScope
import com.example.wodbook.data.WOD
import com.example.wodbook.data.WodDatabase
import com.example.wodbook.data.WodRepository
import kotlinx.coroutines.launch
import java.util.Date

class AddWodActivity : AppCompatActivity() {

    private lateinit var editTextPictureUri: EditText
    private lateinit var editTextDateTime: EditText
    private lateinit var switchDoItAgain: Switch
    private lateinit var editTextNotes: EditText
    private lateinit var buttonSaveWod: Button
    private lateinit var wodRepository: WodRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_wod)

        // Initialize UI components
        editTextPictureUri = findViewById(R.id.editTextPictureUri)
        editTextDateTime = findViewById(R.id.editTextDateTime)
        switchDoItAgain = findViewById(R.id.switchDoItAgain)
        editTextNotes = findViewById(R.id.editTextNotes)
        buttonSaveWod = findViewById(R.id.buttonSaveWod)

        // Initialize the repository (Assuming wodDao is available)
        val wodDao = WodDatabase.getDatabase(applicationContext).wodDao()
        wodRepository = WodRepository(wodDao)

        buttonSaveWod.setOnClickListener {
            saveWod()
        }
    }

    private fun saveWod() {
        val pictureUri = editTextPictureUri.text.toString()
        val dateTime = parseDateTime(editTextDateTime.text.toString()) // Implement this method
        val doItAgain = switchDoItAgain.isChecked
        val notes = editTextNotes.text.toString()

        // Validate inputs (optional but recommended)

        // Create a new WOD object
        val newWod = WOD(
            firebaseUid = "user_firebase_uid", // Replace with actual Firebase UID
            picture = pictureUri,
            dateTime = dateTime,
            doItAgain = doItAgain,
            notes = notes
        )

        // Save the WOD using the repository
        lifecycleScope.launch {
            wodRepository.insertWod(newWod)
            finish() // Close the activity after saving
        }
    }

    private fun parseDateTime(dateTimeString: String): Date {
        // Implement date-time parsing logic
        // Placeholder implementation, replace with actual parsing logic
        return Date()
    }
}
