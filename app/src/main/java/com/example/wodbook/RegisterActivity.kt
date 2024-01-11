package com.example.wodbook


import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.auth.userProfileChangeRequest

class RegisterActivity : AppCompatActivity() {

    private lateinit var editTextUsername: TextInputEditText
    private lateinit var editTextEmail: TextInputEditText
    private lateinit var editTextPassword: TextInputEditText
    private lateinit var editTextConfirmPassword: TextInputEditText
    private lateinit var buttonReg: Button
    private lateinit var auth: FirebaseAuth
    private lateinit var progressBar: ProgressBar
    private lateinit var loginNow: TextView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)
        auth = Firebase.auth

        editTextUsername = findViewById(R.id.username)
        editTextEmail = findViewById(R.id.email)
        editTextPassword = findViewById(R.id.password)
        editTextConfirmPassword = findViewById(R.id.confirm_password)
        buttonReg = findViewById(R.id.btn_register)
        progressBar = findViewById(R.id.pgb_register)

        loginNow = findViewById(R.id.loginNow)
        loginNow.setOnClickListener {
            val intent = Intent(this@RegisterActivity, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }

        buttonReg.setOnClickListener {
            // Code to be executed when button is clicked
            val username = editTextUsername.text.toString().trim()
            val email = editTextEmail.text.toString().trim()
            val password = editTextPassword.text.toString().trim()
            val confirmPassword = editTextConfirmPassword.text.toString().trim()

            progressBar.visibility = View.VISIBLE

            if (username.isEmpty() || email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
                Toast.makeText(this, "All fields are required", Toast.LENGTH_LONG).show()
                progressBar.visibility = View.GONE
                return@setOnClickListener
            }

            if (password != confirmPassword) {
                Toast.makeText(this, "Passwords do not match", Toast.LENGTH_LONG).show()
                progressBar.visibility = View.GONE
                return@setOnClickListener
            } else {
                // Both fields have input
                // Toast.makeText(this, "Email: $email\nPassword: $password", Toast.LENGTH_LONG).show()
                auth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this) { task ->
                        progressBar.visibility = View.GONE
                        if (task.isSuccessful) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "createUserWithEmail:success")
                            val user = auth.currentUser

                            // updateUI(user)
                            Toast.makeText(
                                baseContext,
                                "Account created!",
                                Toast.LENGTH_SHORT,
                            ).show()

                            // Update user name
                            val profileUpdates = userProfileChangeRequest {
                                displayName = username
                            }

                            user!!.updateProfile(profileUpdates)
                                .addOnCompleteListener { updateTask ->
                                    if (updateTask.isSuccessful) {
                                        Log.d(TAG, "User profile updated.")
                                        saveUsername(username)
                                    }
                                }

                            val sharedPref = getSharedPreferences("MyApp", MODE_PRIVATE)
                            sharedPref.edit().putString("username", username).apply()

                            // go back to login activity
                            val intent = Intent(this@RegisterActivity, LoginActivity::class.java)
                            startActivity(intent)
                            finish()

                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "createUserWithEmail:failure", task.exception)
                            Toast.makeText(
                                baseContext,
                                "Registration failed.",
                                Toast.LENGTH_SHORT,
                            ).show()
                            // updateUI(null)
                        }
                    }
            }
        }
    }
    private fun saveUsername(username: String) {
        val sharedPref = getSharedPreferences("WodBookPrefs", MODE_PRIVATE)
        with(sharedPref.edit()) {
            putString("RegisteredUsername", username)
            apply()
        }
    }

}