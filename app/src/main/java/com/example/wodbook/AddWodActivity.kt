package com.example.wodbook

import android.Manifest
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

    private var selectedDateTime: Calendar = Calendar.getInstance()

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


        val wodId = intent.getIntExtra(EXTRA_WOD_ID, -1)
        if (wodId != -1) {
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

    private fun saveWod() {
        val user = UserManager.currentUser

        if (user == null) {
            redirectToLogin()
            return
        }

        Log.d("AddWodActivity", "Selected Date Time: ${selectedDateTime.time}")

        val newWod = WOD(
            firebaseUid = user.uid,
            picture = imageViewPicture.tag.toString(),
            dateTime = parseDateTime(selectedDateTime.time.toString()),
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

    private fun parseDateTime(dateTimeString: String): Date {
        // Implement date-time parsing logic
        return Date() // Replace with actual implementation
    }
}
