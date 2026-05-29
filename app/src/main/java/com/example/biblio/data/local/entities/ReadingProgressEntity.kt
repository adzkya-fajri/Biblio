package com.example.biblio.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "reading_progress")
data class ReadingProgress(
    @PrimaryKey val bookId: String,
    val locatorJson: String,
    val isDismissed: Boolean = false,
    val updatedAt: Long = System.currentTimeMillis()
)
