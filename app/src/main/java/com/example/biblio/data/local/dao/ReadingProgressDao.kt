package com.example.biblio.data.local.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.example.biblio.data.local.entities.ReadingProgress

@Dao
interface ReadingProgressDao {
    @Query("SELECT * FROM reading_progress WHERE bookId = :bookId")
    suspend fun getProgress(bookId: String): ReadingProgress?

    @Query("SELECT * FROM reading_progress WHERE isDismissed = 0 ORDER BY updatedAt DESC LIMIT 1")
    suspend fun getLatestActiveProgress(): ReadingProgress?

    @Upsert
    suspend fun saveProgress(progress: ReadingProgress)

    @Query("UPDATE reading_progress SET isDismissed = 1 WHERE bookId = :bookId")
    suspend fun dismissProgress(bookId: String)

    @Query("DELETE FROM reading_progress WHERE bookId = :bookId")
    suspend fun deleteProgress(bookId: String)
}
