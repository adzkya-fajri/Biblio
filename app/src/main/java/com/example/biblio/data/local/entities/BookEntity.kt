package com.example.biblio.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.biblio.data.model.Buku

@Entity(tableName = "books")
data class BookEntity(
    @PrimaryKey val id: String,
    val isbn: String,
    val title: String,
    val description: String,
    val author: String,
    val page: Int,
    val cover: String,
    val genreId: Int
)

fun BookEntity.toBuku() = Buku(
    id = id,
    isbn = isbn,
    title = title,
    description = description,
    author = author,
    page = page,
    cover = cover
)

fun Buku.toEntity(genreId: Int) = BookEntity(
    id = id,
    isbn = isbn,
    title = title,
    description = description,
    author = author,
    page = page,
    cover = cover,
    genreId = genreId
)
