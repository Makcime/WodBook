package com.example.wodbook.data.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.example.wodbook.data.WOD

@Dao
interface WodDao {
    @Query("SELECT * FROM wod WHERE firebaseUid = :firebaseUid ORDER BY dateTime DESC")
    suspend fun getWodsByUser(firebaseUid: String): List<WOD>

    @Query("SELECT * FROM wod WHERE id = :wodId")
    suspend fun getWodById(wodId: Int): WOD?

    @Upsert
    suspend fun upsert(wod: WOD)

    @Query("UPDATE wod SET doItAgain = :doItAgain WHERE id = :wodId")
    suspend fun updateDoItAgain(wodId: Int, doItAgain: Boolean)

    @Query("DELETE FROM wod WHERE id = :id")
    suspend fun deleteWod(id: Int)
}
