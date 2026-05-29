package com.example.biblio.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.biblio.data.local.dao.BookDao
import com.example.biblio.data.local.dao.ProfileDao
import com.example.biblio.data.local.dao.ReadingProgressDao
import com.example.biblio.data.local.entities.BookEntity
import com.example.biblio.data.local.entities.GenreEntity
import com.example.biblio.data.local.entities.ProfileEntity
import com.example.biblio.data.local.entities.ReadingProgress

@Database(
    entities = [BookEntity::class, GenreEntity::class, ProfileEntity::class, ReadingProgress::class],
    version = 1,
    exportSchema = false
)
abstract class BiblioDatabase : RoomDatabase() {
    abstract fun bookDao(): BookDao
    abstract fun profileDao(): ProfileDao
    abstract fun readingProgressDao(): ReadingProgressDao

    companion object {
        const val DATABASE_NAME = "biblio_db"
    }
}
