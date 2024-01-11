package com.example.wodbook

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import android.widget.Toast
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.userProfileChangeRequest

class AccountActivity : AppCompatActivity() {

    private lateinit var editTextUsername: TextInputEditText
    private lateinit var editTextEmail: TextInputEditText
    private lateinit var buttonSave: Button
    private lateinit var buttonDelete: Button
    private lateinit var buttonCancel: Button
    private lateinit var progressBar: ProgressBar
    private var auth: FirebaseAuth = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_account)

        initUI()
        loadUserData()
    }

    private fun initUI() {
        editTextUsername = findViewById(R.id.username)
        editTextEmail = findViewById(R.id.email)
        buttonSave = findViewById(R.id.btn_save)
        buttonDelete = findViewById(R.id.btn_delete)
        buttonCancel = findViewById(R.id.btn_cancel)
        progressBar = findViewById(R.id.pgb_register)

        buttonSave.setOnClickListener { updateUserProfile() }
        buttonDelete.setOnClickListener { deleteUserAccount() }
        buttonCancel.setOnClickListener { redirectToMain() }
    }

    private fun loadUserData() {
        val currentUser = auth.currentUser
        editTextEmail.setText(currentUser?.email)
        editTextUsername.setText(currentUser?.displayName)
    }

    private fun updateUserProfile() {
        val user = auth.currentUser
        val email = editTextEmail.text.toString().trim()
        val username = editTextUsername.text.toString().trim()

        progressBar.visibility = View.VISIBLE

        val profileUpdates = userProfileChangeRequest {
            displayName = username
        }

        user?.updateProfile(profileUpdates)?.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                user.updateEmail(email).addOnCompleteListener { emailUpdateTask ->
                    progressBar.visibility = View.GONE
                    if (emailUpdateTask.isSuccessful) {
                        Toast.makeText(baseContext, "Profile updated successfully", Toast.LENGTH_SHORT).show()
                        redirectToMain()
                    } else {
                        Toast.makeText(baseContext, "Failed to update email", Toast.LENGTH_SHORT).show()
                    }
                }
            } else {
                progressBar.visibility = View.GONE
                Toast.makeText(baseContext, "Failed to update profile", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun deleteUserAccount() {
        val user = auth.currentUser
        progressBar.visibility = View.VISIBLE

        user?.delete()?.addOnCompleteListener { task ->
            progressBar.visibility = View.GONE
            if (task.isSuccessful) {
                Toast.makeText(baseContext, "Account deleted successfully", Toast.LENGTH_SHORT).show()
                redirectToLogin()
            } else {
                Toast.makeText(baseContext, "Failed to delete account", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun redirectToLogin() {
        startActivity(Intent(this, LoginActivity::class.java))
        finish()
    }

    private fun redirectToMain() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }
}
