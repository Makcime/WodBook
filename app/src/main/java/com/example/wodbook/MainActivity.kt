package com.example.wodbook

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Button
import android.widget.Switch
import android.widget.TextView
import android.widget.Toast
import android.widget.ToggleButton
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.wodbook.data.WOD
import com.example.wodbook.data.WodDatabase
import com.example.wodbook.data.WodRepository
import com.example.wodbook.domain.UserManager
import com.example.wodbook.domain.WodAdapter
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var userDetails: TextView
    private lateinit var recyclerView: RecyclerView
    private lateinit var switchFilter: Switch

    private lateinit var wodAdapter: WodAdapter
    private lateinit var wodRepository: WodRepository

    private val readExternalStorageRequestCode = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initRepository()
        initUI()
        checkAndRequestPermission()
    }

    override fun onResume() {
        super.onResume()
        loadWods(switchFilter.isChecked)
    }

    private fun initRepository() {
        val wodDao = WodDatabase.getDatabase(applicationContext).wodDao()
        wodRepository = WodRepository(wodDao)
    }

    private fun initUI() {
        userDetails = findViewById(R.id.user_details)
        setUpUserDetails()

        findViewById<Button>(R.id.btn_logout).apply {
            setOnClickListener { logOut() }
        }

        setUpRecyclerView()
        setUpFloatingActionButtons()
        setUpSwitchFilter()
    }

    private fun setUpSwitchFilter() {
        switchFilter = findViewById(R.id.switch_filter)
        switchFilter.setOnCheckedChangeListener { _, isChecked ->
            loadWods(isChecked)
        }
    }

    private fun setUpUserDetails() {
        userDetails.text = getString(
            R.string.logged_in_as,
            UserManager.currentUser?.email ?: getString(R.string.anonymous)
        )
    }

    private fun logOut() {
        UserManager.signOut()
        redirectToLogin()
    }

    private fun setUpRecyclerView() {
        recyclerView = findViewById(R.id.recycler_view_wods)
        recyclerView.layoutManager = GridLayoutManager(this, 2)

        wodAdapter = WodAdapter { wod ->
            startActivity(Intent(this, WodActivity::class.java).apply {
                putExtra(WodActivity.EXTRA_WOD_ID, wod.id)
            })
        }

        recyclerView.adapter = wodAdapter
    }

    private fun setUpFloatingActionButtons() {
        findViewById<FloatingActionButton>(R.id.fab_add_wod).setOnClickListener {
            startActivity(Intent(this, WodActivity::class.java))
        }

        findViewById<FloatingActionButton>(R.id.fab_random_wod).setOnClickListener {
            displayRandomWod()
        }
    }

    private fun displayRandomWod() {
        lifecycleScope.launch {
            UserManager.currentUser?.let { user ->
                val randomWod = wodRepository.getRandomWod(user.uid)
                if (randomWod != null) {
                    // Handle the WOD, e.g., show it in full screen
                    showWodFullScreen(randomWod)
                } else {
                    // Show a Toast message if no WOD is found
                    Toast.makeText(this@MainActivity,
                        getString(R.string.no_good_wod_found_to_display), Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun showWodFullScreen(wod: WOD) {
        startActivity(Intent(this, ShowImageActivity::class.java).apply {
            putExtra("image_uri", wod.picture)
        })
    }

    private fun loadWods(filterDoItAgain: Boolean = false) {
        UserManager.currentUser?.uid?.let { uid ->
            lifecycleScope.launch {
                val wods = if (filterDoItAgain) {
                    wodRepository.getWodsDoItAgain(uid)
                } else {
                    wodRepository.getWodsByUser(uid)
                }
                wodAdapter.setWods(wods)
            }
        }
    }

    private fun checkAndRequestPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), readExternalStorageRequestCode)
        }
    }

    private fun redirectToLogin() {
        startActivity(Intent(this, LoginActivity::class.java))
        finish()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == readExternalStorageRequestCode && grantResults.isNotEmpty() && grantResults[0] != PackageManager.PERMISSION_GRANTED) {
            // Handle permission denial
        }
    }
}
