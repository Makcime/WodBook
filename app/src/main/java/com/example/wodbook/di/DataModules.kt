package com.example.wodbook.di

import android.content.Context
import androidx.room.Room
import com.example.wodbook.data.WodDatabase
import com.example.wodbook.data.dao.WodDao
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Singleton
    @Provides
    fun provideDataBase(@ApplicationContext context: Context): WodDatabase {
        return Room.databaseBuilder(
            context.applicationContext,
            WodDatabase::class.java,
            "Wods.db"
        ).build()
    }
}

@Provides
fun provideTaskDao(database: WodDatabase) : WodDao = database.wodDao()
