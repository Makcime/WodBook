package com.example.wodbook

import android.Manifest
import android.app.AlertDialog
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Switch
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.example.wodbook.data.WOD
import com.example.wodbook.data.WodDatabase
import com.example.wodbook.data.WodRepository
import com.example.wodbook.domain.UserManager
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class AddWodActivity : AppCompatActivity() {

    private lateinit var imageViewPicture: ImageView
    private lateinit var textViewDateTime: TextView
    private lateinit var switchDoItAgain: Switch
    private lateinit var editTextNotes: EditText
    private lateinit var buttonSaveWod: Button
    private lateinit var buttonDeleteWod: Button

    private var selectedDateTime: Calendar = Calendar.getInstance()

    private var wodId: Int = -1 // Add this line to store the WOD ID

    private val wodRepository: WodRepository by lazy {
        WodRepository(WodDatabase.getDatabase(applicationContext).wodDao())
    }

    companion object {
        private const val PICK_IMAGE_REQUEST = 1
        const val EXTRA_WOD_ID = "extra_wod_id"
        private const val REQUEST_CODE_READ_EXTERNAL_STORAGE = 1

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_wod)

        initializeUI()

        wodId = intent.getIntExtra(EXTRA_WOD_ID, -1)
        if (wodId != -1) {
            // Enable the delete button
            buttonDeleteWod = findViewById(R.id.buttonDeleteWod)
            buttonDeleteWod.isEnabled = true
            buttonDeleteWod.setOnClickListener { deleteWod() }

            // Get the wod
            lifecycleScope.launch {
                val wod = wodRepository.getWodById(wodId)
                if (wod != null) {
                    loadWodData(wod)
                }
            }
        }

        textViewDateTime = findViewById(R.id.textViewDateTime)
        updateDateTimeDisplay()

        textViewDateTime.setOnClickListener {
            openDateTimePicker()
        }

    }

    private fun deleteWod() {
        if (wodId != -1) {
            AlertDialog.Builder(this)
                .setTitle("Confirm Deletion")
                .setMessage("Are you sure you want to delete this WOD?")
                .setPositiveButton("Delete") { dialog, which ->
                    performDeletion()
                }
                .setNegativeButton("Cancel", null)
                .show()
        }
    }

    private fun performDeletion() {
        lifecycleScope.launch {
            try {
                wodRepository.deleteWod(wodId)
                Log.d("AddWodActivity", "WOD deleted successfully")
                finish() // Close the activity after deletion
            } catch (e: Exception) {
                Log.e("AddWodActivity", "Error deleting WOD", e)
            }
        }
    }


    private fun initializeUI() {
        imageViewPicture = findViewById(R.id.imageViewPicture)
        imageViewPicture.setImageResource(R.drawable.ic_placeholder_foreground) // Default placeholder

        textViewDateTime = findViewById(R.id.textViewDateTime)
        switchDoItAgain = findViewById(R.id.switchDoItAgain)
        editTextNotes = findViewById(R.id.editTextNotes)
        buttonSaveWod = findViewById(R.id.buttonSaveWod)

        imageViewPicture.setOnClickListener {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                val galleryIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                startActivityForResult(galleryIntent, PICK_IMAGE_REQUEST)
            } else {
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), REQUEST_CODE_READ_EXTERNAL_STORAGE)
            }
        }

        buttonSaveWod.setOnClickListener { saveWod() }
    }

    private fun openDateTimePicker() {
        val dateSetListener = DatePickerDialog.OnDateSetListener { _, year, month, dayOfMonth ->
            selectedDateTime.set(Calendar.YEAR, year)
            selectedDateTime.set(Calendar.MONTH, month)
            selectedDateTime.set(Calendar.DAY_OF_MONTH, dayOfMonth)
            isDateUpdated = true
            openTimePicker()
        }

        DatePickerDialog(this, dateSetListener,
            selectedDateTime.get(Calendar.YEAR),
            selectedDateTime.get(Calendar.MONTH),
            selectedDateTime.get(Calendar.DAY_OF_MONTH)
        ).show()
    }

    private fun openTimePicker() {
        val timeSetListener = TimePickerDialog.OnTimeSetListener { _, hour, minute ->
            selectedDateTime.set(Calendar.HOUR_OF_DAY, hour)
            selectedDateTime.set(Calendar.MINUTE, minute)
            isDateUpdated = true
            updateDateTimeDisplay()
        }

        TimePickerDialog(this, timeSetListener,
            selectedDateTime.get(Calendar.HOUR_OF_DAY),
            selectedDateTime.get(Calendar.MINUTE), true
        ).show()
    }

    private fun updateDateTimeDisplay() {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
        textViewDateTime.text = dateFormat.format(selectedDateTime.time)
    }

    private var isDateUpdated = false // Flag to track if date has been updated

    private fun saveWod() {
        val user = UserManager.currentUser
        if (user == null) {
            redirectToLogin()
            return
        }

        lifecycleScope.launch {
            val existingWod = if (wodId != -1) wodRepository.getWodById(wodId) else null

            val newPicture = imageViewPicture.tag?.toString() ?: existingWod?.picture ?: ""
            val newDateTime = if (isDateUpdated) selectedDateTime.time else existingWod?.dateTime ?: Date()

            try {
                if (wodId == -1) {
                    wodRepository.insertWod(
                        firebaseUid = user.uid,
                        picture = newPicture,
                        dateTime = newDateTime,
                        doItAgain = switchDoItAgain.isChecked,
                        notes = editTextNotes.text.toString()
                    )
                } else {
                    wodRepository.editWod(
                        wodId = wodId,
                        firebaseUid = user.uid,
                        picture = newPicture,
                        dateTime = newDateTime,
                        doItAgain = switchDoItAgain.isChecked,
                        notes = editTextNotes.text.toString()
                    )
                }
                Log.d("AddWodActivity", "WOD saved successfully")
                finish()
            } catch (e: Exception) {
                Log.e("AddWodActivity", "Error saving WOD", e)
            }
        }
    }


    private fun loadWodData(wod: WOD) {
        lifecycleScope.launch {
            if (wod != null) {
                textViewDateTime.setText(wod.dateTime.toString())
                switchDoItAgain.isChecked = wod.doItAgain
                editTextNotes.setText(wod.notes)

                if (wod.picture.isNullOrEmpty()) {
                    imageViewPicture.setImageResource(R.drawable.ic_placeholder_foreground) // Set placeholder
                } else {
                    val uri = Uri.parse(wod.picture)
                    try {
                        contentResolver.openInputStream(uri)?.use { inputStream ->
                            val drawable = Drawable.createFromStream(inputStream, uri.toString())
                            imageViewPicture.setImageDrawable(drawable)
                        }
                    } catch (e: Exception) {
                        Log.e("AddWodActivity", "Error loading image", e)
                        imageViewPicture.setImageResource(R.drawable.ic_placeholder_foreground)
                    }
                }
            }
        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null) {
            val selectedImageUri = data.data
            selectedImageUri?.let { uri ->
                try {
                    contentResolver.openInputStream(uri)?.use { inputStream ->
                        val drawable = Drawable.createFromStream(inputStream, uri.toString())
                        imageViewPicture.setImageDrawable(drawable)
                        imageViewPicture.tag = uri.toString() // Set the tag to the URI
                    }
                } catch (e: Exception) {
                    Log.e("AddWodActivity", "Error loading image", e)
                    imageViewPicture.setImageResource(R.drawable.ic_placeholder_foreground)
                }
            }
        }
    }


    private fun redirectToLogin() {
        startActivity(Intent(this, LoginActivity::class.java))
        finish()
    }

}
