package com.example.wodbook

import android.net.Uri
import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity

class ShowImageActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Set the content view to your layout that contains an ImageView
        setContentView(R.layout.activity_show_image)

        // Now retrieve the ImageView from your layout
        val imageView = findViewById<ImageView>(R.id.ShowImageView)

        // Get the URI passed from WodActivity
        val imageUriString = intent.getStringExtra("image_uri")
        imageUriString?.let {
            val imageUri = Uri.parse(it)
            imageView.setImageURI(imageUri)
        }
    }
}



