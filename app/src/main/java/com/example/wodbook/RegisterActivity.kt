package com.example.wodbook


import android.content.ContentValues.TAG
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth

class RegisterActivity : AppCompatActivity() {

    private lateinit var editTextEmail: TextInputEditText
    private lateinit var editTextPassword: TextInputEditText
    private lateinit var buttonReg: Button
    private lateinit var auth: FirebaseAuth
    private lateinit var progressBar: ProgressBar
    private lateinit var loginNow : TextView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)
        auth = Firebase.auth

        editTextEmail = findViewById(R.id.email)
        editTextPassword = findViewById(R.id.password)
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
            val email = editTextEmail.text.toString().trim()
            val password = editTextPassword.text.toString().trim()
            progressBar.visibility = View.VISIBLE

            if (email.isEmpty() || password.isEmpty()) {
                // One or both fields are empty
                Toast.makeText(this, "Email and password must not be empty", Toast.LENGTH_LONG).show()
                progressBar.visibility = View.GONE
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

            // Here, you can add more code to handle user registration
            // For instance, you might want to check if the email and password are valid
            // and then use them to create a new account in your Firebase project
        }
    }
}