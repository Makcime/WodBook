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
    private lateinit var textView: TextView
    private lateinit var recyclerView: RecyclerView
    private lateinit var fabAddWod: FloatingActionButton
    private lateinit var wodAdapter: WodAdapter

    companion object {
        private const val REQUEST_CODE_READ_EXTERNAL_STORAGE = 1
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initializeUI()
        checkAndRequestPermission()
    }

    private fun initializeUI() {
        setupLogoutButton()
        setupRecyclerView()
        setupFloatingActionButton()
        loadWods()
    }

    private fun setupLogoutButton() {
        buttonLogout = findViewById(R.id.btn_logout)
        buttonLogout.setOnClickListener {
            UserManager.signOut()
            redirectToLogin()
        }
    }

    private fun setupRecyclerView() {
        recyclerView = findViewById(R.id.recycler_view_wods)
        recyclerView.layoutManager = GridLayoutManager(this, 2)
        wodAdapter = WodAdapter(onItemClicked = { wod ->
            val intent = Intent(this, AddWodActivity::class.java).apply {
                putExtra(AddWodActivity.EXTRA_WOD_ID, wod.id)
            }
            startActivity(intent)
        })
        recyclerView.adapter = wodAdapter
    }


    private fun setupFloatingActionButton() {
        fabAddWod = findViewById(R.id.fab_add_wod)
        fabAddWod.setOnClickListener {
            val intent = Intent(this, AddWodActivity::class.java)
            startActivity(intent)
        }
    }

    private fun loadWods() {
        UserManager.currentUser?.let { user ->
            lifecycleScope.launch {
                val wodRepository = WodRepository(WodDatabase.getDatabase(applicationContext).wodDao())
                val userWods = wodRepository.getWodsByUser(user.uid)
                wodAdapter.setWods(userWods)
            }
        }
    }

    private fun checkAndRequestPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), REQUEST_CODE_READ_EXTERNAL_STORAGE)
        }
    }

    private fun redirectToLogin() {
        startActivity(Intent(this, LoginActivity::class.java))
        finish()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_READ_EXTERNAL_STORAGE && grantResults.isNotEmpty()) {
            if (grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                // Handle permission denial
            }
        }
    }
}
