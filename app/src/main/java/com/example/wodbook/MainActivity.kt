package com.example.wodbook

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.wodbook.data.WodDatabase
import com.example.wodbook.data.WodRepository
import com.example.wodbook.domain.UserManager
import com.example.wodbook.domain.WodAdapter
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var buttonLogout: Button
    private lateinit var textView : TextView
    private lateinit var user : UserManager

    private lateinit var recyclerView: RecyclerView
    private lateinit var fabAddWod: FloatingActionButton
    private lateinit var wodAdapter: WodAdapter // Assuming you have a WodAdapter

    companion object {
        private const val REQUEST_CODE_READ_EXTERNAL_STORAGE = 1
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        wodAdapter = WodAdapter()

        // auth = Firebase.auth
        buttonLogout = findViewById(R.id.btn_logout)
        textView = findViewById(R.id.user_details)

        val user = UserManager.currentUser

        if (user == null) {
            // No user is signed in
            val intent = Intent(this@MainActivity, LoginActivity::class.java)
            startActivity(intent)
            finish()
        } else {
            textView.setText(user.email)
        }

        buttonLogout.setOnClickListener {
            UserManager.signOut()

            // go back to login activity
            val intent = Intent(this@MainActivity, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }

        recyclerView = findViewById(R.id.recycler_view_wods)
        fabAddWod = findViewById(R.id.fab_add_wod)

        recyclerView = findViewById(R.id.recycler_view_wods)
        recyclerView.layoutManager = GridLayoutManager(this, 2) // 2 columns in grid
        recyclerView.adapter = wodAdapter

        // Initialize the repository (Assuming wodDao is available)
        val wodDao = WodDatabase.getDatabase(applicationContext).wodDao()
        var wodRepository = WodRepository(wodDao)

        user?.let {
            lifecycleScope.launch {
                // Load WODs asynchronously and update the adapter
                val userWods = wodRepository.getWodsByUser(it.uid)
                wodAdapter.setWods(userWods) // Update your adapter with the new data
            }
        }

        recyclerView.adapter = wodAdapter

        fabAddWod.setOnClickListener {
            val intent = Intent(this, AddWodActivity::class.java) // Replace with your 'Add WOD' activity
            startActivity(intent)
        }

        // TODO: Load and display WODs from the database

        checkAndRequestPermission()
    }

    private fun checkAndRequestPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), REQUEST_CODE_READ_EXTERNAL_STORAGE)
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_READ_EXTERNAL_STORAGE) {
            if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                // Permission granted, handle the granted permission
            } else {
                // Permission denied, handle the denial
            }
        }
    }

}