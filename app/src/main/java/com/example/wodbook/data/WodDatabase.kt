package com.example.wodbook.data

import android.content.Context
import androidx.databinding.adapters.Converters
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.wodbook.data.dao.WodDao

@Database(entities = [WOD::class], version = 1, exportSchema = false)
@TypeConverters(Converters::class) // For handling Date type
abstract class WodDatabase : RoomDatabase() {

    abstract fun wodDao(): WodDao

    companion object {
        @Volatile
        private var INSTANCE: WodDatabase? = null

        fun getDatabase(context: Context): WodDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    WodDatabase::class.java,
                    "wod_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
