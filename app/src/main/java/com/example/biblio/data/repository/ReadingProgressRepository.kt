package com.example.biblio.data.repository

import com.example.biblio.data.local.dao.ReadingProgressDao
import com.example.biblio.data.local.entities.ReadingProgress
import com.example.biblio.data.remote.apis.ProgressApi
import com.example.biblio.data.remote.dto.UpsertProgressRequest
import com.example.biblio.data.remote.dto.User
import com.example.biblio.data.local.dao.ProfileDao
import com.example.biblio.data.local.entities.toEntity
import org.json.JSONObject
import org.readium.r2.shared.publication.Locator
import java.util.UUID

class ReadingProgressRepository(
    private val dao: ReadingProgressDao,
    private val api: ProgressApi,
    private val profileDao: ProfileDao
) {

    suspend fun getLocator(bookId: String): Locator? {
        val progress = dao.getProgress(bookId) ?: return null
        return Locator.fromJSON(JSONObject(progress.locatorJson))
    }

    suspend fun saveLocator(bookId: String, locator: Locator) {
        dao.saveProgress(
            ReadingProgress(
                bookId = bookId,
                locatorJson = locator.toJSON().toString(),
                isDismissed = false // Reset dismissal if saved again
            )
        )
    }

    suspend fun dismissProgress(bookId: String) {
        dao.dismissProgress(bookId)
    }

    suspend fun getLatestActiveLocalProgress(): ReadingProgress? {
        return dao.getLatestActiveProgress()
    }

    suspend fun upsertRemoteProgress(bookId: String, lastPage: Int): Result<User> {
        return try {
            val response = api.upsertProgress(
                UpsertProgressRequest(
                    bookId = UUID.fromString(bookId),
                    lastPage = lastPage
                )
            ).execute()

            if (response.isSuccessful) {
                val user = response.body()!!
                // Sync ke local profile database agar UI terupdate
                profileDao.insertProfile(user.toEntity())
                Result.success(user)
            } else {
                Result.failure(Exception("Failed to upsert progress: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

//    suspend fun deleteRemoteProgress(): Result<Unit> {
//        return try {
//            val response = api.deleteProgress().execute()
//            if (response.isSuccessful) {
//                // Clear progress di local cache jika ProfileEntity mendukungnya di masa depan
//                // Saat ini ProfileEntity belum menyimpan progress
//                Result.success(Unit)
//            } else {
//                Result.failure(Exception("Failed to delete progress: ${response.code()}"))
//            }
//        } catch (e: Exception) {
//            Result.failure(e)
//        }
//    }
}

