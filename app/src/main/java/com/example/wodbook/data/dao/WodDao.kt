package com.example.wodbook.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import androidx.room.Upsert
import com.example.wodbook.data.WOD

@Dao
interface WodDao {
    @Query("SELECT * FROM wod WHERE firebaseUid = :firebaseUid")
    suspend fun getWodsByUser(firebaseUid: String): List<WOD>

    @Upsert
    suspend fun upsert(wod: WOD)

    @Query("UPDATE wod SET doItAgain = :doItAgain WHERE id = :wodId")
    suspend fun updateDoItAgain(wodId: String, doItAgain: Boolean)

    @Query("DELETE FROM wod WHERE id = :id")
    suspend fun deleteWod(id: Int)
}
