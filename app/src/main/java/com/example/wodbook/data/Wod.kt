package com.example.wodbook.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*

@Entity(tableName = "wod")
data class WOD(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val firebaseUid: String, // Firebase unique user.uid
    val picture: String, // File path or URI for the picture, instead of Bitmap
    val dateTime: Date,
    val doItAgain: Boolean,
    val notes: String
)
