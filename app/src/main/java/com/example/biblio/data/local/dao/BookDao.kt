package com.example.biblio.data.local.dao

import androidx.room.*
import com.example.biblio.data.local.entities.BookEntity
import com.example.biblio.data.local.entities.GenreEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface BookDao {
    @Query("SELECT * FROM genres")
    suspend fun getAllGenres(): List<GenreEntity>

    @Query("SELECT * FROM books WHERE genreId = :genreId")
    suspend fun getBooksByGenre(genreId: Int): List<BookEntity>

    @Query("SELECT * FROM books WHERE id = :bookId")
    suspend fun getBookById(bookId: String): BookEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGenres(genres: List<GenreEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBooks(books: List<BookEntity>)

    @Query("DELETE FROM genres")
    suspend fun deleteAllGenres()

    @Query("DELETE FROM books")
    suspend fun deleteAllBooks()

    @Transaction
    suspend fun refreshDatabase(genres: List<GenreEntity>, books: List<BookEntity>) {
        deleteAllGenres()
        deleteAllBooks()
        insertGenres(genres)
        insertBooks(books)
    }
}
