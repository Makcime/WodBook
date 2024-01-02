package com.example.wodbook

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Switch
import androidx.lifecycle.lifecycleScope
import com.example.wodbook.data.WOD
import com.example.wodbook.data.WodDatabase
import com.example.wodbook.data.WodRepository
import com.example.wodbook.domain.UserManager
import kotlinx.coroutines.launch
import java.util.Date

class AddWodActivity : AppCompatActivity() {

    private lateinit var editTextPictureUri: EditText
    private lateinit var editTextDateTime: EditText
    private lateinit var switchDoItAgain: Switch
    private lateinit var editTextNotes: EditText
    private lateinit var buttonSaveWod: Button

    private val wodRepository: WodRepository by lazy {
        WodRepository(WodDatabase.getDatabase(applicationContext).wodDao())
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_wod)

        initializeUI()
        buttonSaveWod.setOnClickListener { saveWod() }
    }

    private fun initializeUI() {
        editTextPictureUri = findViewById(R.id.editTextPictureUri)
        editTextDateTime = findViewById(R.id.editTextDateTime)
        switchDoItAgain = findViewById(R.id.switchDoItAgain)
        editTextNotes = findViewById(R.id.editTextNotes)
        buttonSaveWod = findViewById(R.id.buttonSaveWod)
    }

    private fun saveWod() {
        val user = UserManager.currentUser

        if (user == null) {
            redirectToLogin()
            return
        }

        val newWod = WOD(
            firebaseUid = user.uid,
            picture = editTextPictureUri.text.toString(),
            dateTime = parseDateTime(editTextDateTime.text.toString()),
            doItAgain = switchDoItAgain.isChecked,
            notes = editTextNotes.text.toString()
        )

        lifecycleScope.launch {
            wodRepository.insertWod(
                firebaseUid = newWod.firebaseUid,
                picture = newWod.picture,
                dateTime = newWod.dateTime,
                doItAgain = newWod.doItAgain,
                notes = newWod.notes
            )
            finish()
        }
    }

    private fun redirectToLogin() {
        startActivity(Intent(this, LoginActivity::class.java))
        finish()
    }

    private fun parseDateTime(dateTimeString: String): Date {
        // Implement date-time parsing logic
        return Date() // Replace with actual implementation
    }
}
