package com.example.biblio.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.biblio.data.model.Section

@Entity(tableName = "genres")
data class GenreEntity(
    @PrimaryKey val id: Int,
    val title: String
)

fun GenreEntity.toSection(books: List<BookEntity>) = Section(
    id = id,
    title = title,
    books = books.map { it.toBuku() }
)
