package com.example.wodbook.data

import com.example.wodbook.data.dao.WodDao
import java.util.Date

class WodRepository(private val wodDao: WodDao) {

    suspend fun getWodsByUser(firebaseUid: String): List<WOD> {
        return wodDao.getWodsByUser(firebaseUid)
    }

    suspend fun insertWod(firebaseUid: String, picture: String, dateTime: Date, doItAgain: Boolean, notes: String?) {
        val wod = WOD(
            firebaseUid = firebaseUid,
            picture = picture,
            dateTime = dateTime,
            doItAgain = doItAgain,
            notes = notes
        )
        wodDao.upsert(wod)
    }

    suspend fun editWod(wodId: Int, firebaseUid: String, picture: String, dateTime: Date, doItAgain: Boolean, notes: String) {
        val updatedWod = WOD(
            id = wodId,
            firebaseUid = firebaseUid,
            picture = picture,
            dateTime = dateTime,
            doItAgain = doItAgain,
            notes = notes
        )
        wodDao.upsert(updatedWod)
    }

    suspend fun toggleDoItAgain(wodId: Int, doItAgain: Boolean) {
        wodDao.updateDoItAgain( wodId, doItAgain)
    }

    suspend fun deleteWod(wodId: Int) {
        wodDao.deleteWod(wodId)
    }

    suspend fun getWodById(wodId: Int): WOD? {
        return wodDao.getWodById(wodId)
    }
}


