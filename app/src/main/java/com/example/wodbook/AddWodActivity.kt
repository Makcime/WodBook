package com.example.wodbook

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Switch
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.example.wodbook.data.WOD
import com.example.wodbook.data.WodDatabase
import com.example.wodbook.data.WodRepository
import com.example.wodbook.domain.UserManager
import kotlinx.coroutines.launch
import java.util.Date

class AddWodActivity : AppCompatActivity() {

    private lateinit var imageViewPicture: ImageView
    private lateinit var editTextDateTime: EditText
    private lateinit var switchDoItAgain: Switch
    private lateinit var editTextNotes: EditText
    private lateinit var buttonSaveWod: Button

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
    }

    private fun initializeUI() {
        imageViewPicture = findViewById(R.id.imageViewPicture)
        editTextDateTime = findViewById(R.id.editTextDateTime)
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

    private fun saveWod() {
        val user = UserManager.currentUser

        if (user == null) {
            redirectToLogin()
            return
        }

        val newWod = WOD(
            firebaseUid = user.uid,
            picture = imageViewPicture.tag.toString(),
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

    private fun loadWodData(wod: WOD) {
        editTextDateTime.setText(wod.dateTime.toString())
        switchDoItAgain.isChecked = wod.doItAgain
        editTextNotes.setText(wod.notes)
        imageViewPicture.tag = wod.picture
        val uri = Uri.parse(wod.picture)
        imageViewPicture.setImageURI(uri) // Set the image from URI
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null) {
            val selectedImageUri = data.data
            imageViewPicture.setImageURI(selectedImageUri)
            imageViewPicture.tag = selectedImageUri.toString() // Store URI as tag
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
