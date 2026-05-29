package com.example.biblio.data.repository

import android.content.Context
import android.util.Log
import com.example.biblio.data.model.Buku
import com.example.biblio.data.model.BukuDatabase
import com.example.biblio.data.model.Section
import com.example.biblio.data.preferences.TokenPreferences
import com.example.biblio.data.remote.apis.BooksApi
import com.example.biblio.data.remote.apis.GenresApi
import com.example.biblio.data.remote.dto.Book
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.withContext
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import com.example.biblio.data.local.dao.BookDao
import com.example.biblio.data.local.entities.toBuku
import com.example.biblio.data.local.entities.toEntity
import com.example.biblio.data.local.entities.toSection
import com.example.biblio.data.local.entities.GenreEntity
import com.example.biblio.di.AppModule
import com.example.biblio.utils.toAbsoluteUrl
import java.util.UUID

class BukuRepository(
    private val context: Context,
    private val booksApi: BooksApi,
    private val genresApi: GenresApi,
    private val tokenPreferences: TokenPreferences,
    private val bookDao: BookDao
) {
    private val json = Json {
        ignoreUnknownKeys = true
        prettyPrint = true
    }

    suspend fun getCachedGenresWithBooks(): BukuDatabase? = withContext(Dispatchers.IO) {
        val cachedGenres = bookDao.getAllGenres()
        if (cachedGenres.isNotEmpty()) {
            val sections = cachedGenres.map { genreEntity ->
                val books = bookDao.getBooksByGenre(genreEntity.id)
                genreEntity.toSection(books)
            }
            BukuDatabase(sections)
        } else {
            null
        }
    }

    suspend fun getGenreWithBooks(): Result<BukuDatabase> = withContext(Dispatchers.IO) {
        try {
            tokenPreferences.token.firstOrNull()?.let {
                AppModule.setToken(it)
            }
            val response = genresApi.getGenreWithBooks().execute()
            
            if (response.isSuccessful) {
                val remoteGenres = response.body() ?: emptyList()
                
                // Refresh local database
                val genreEntities = remoteGenres.map { 
                    GenreEntity(id = it.id ?: 0, title = it.name ?: "Unknown")
                }
                val bookEntities = remoteGenres.flatMap { remoteGenre ->
                    remoteGenre.books?.map { it.toBuku().toEntity(remoteGenre.id ?: 0) } ?: emptyList()
                }
                
                bookDao.refreshDatabase(genreEntities, bookEntities)

                val sections = remoteGenres.map { remoteGenre ->
                    Section(
                        id = remoteGenre.id ?: 0,
                        title = remoteGenre.name ?: "Unknown",
                        books = remoteGenre.books?.map { it.toBuku() } ?: emptyList()
                    )
                }
                Result.success(BukuDatabase(sections))
            } else {
                // If remote fails, try to return cached data as ultimate fallback
                val cachedGenres = bookDao.getAllGenres()
                if (cachedGenres.isNotEmpty()) {
                    val sections = cachedGenres.map { genreEntity ->
                        val books = bookDao.getBooksByGenre(genreEntity.id)
                        genreEntity.toSection(books)
                    }
                    Result.success(BukuDatabase(sections))
                } else {
                    Result.failure(Exception("Gagal mengambil data: ${response.code()}"))
                }
            }
        } catch (e: Exception) {
            Log.e("BukuRepository", "Error fetching genres with books", e)
            
            // Fallback to cache on exception
            val cachedGenres = bookDao.getAllGenres()
            if (cachedGenres.isNotEmpty()) {
                val sections = cachedGenres.map { genreEntity ->
                    val books = bookDao.getBooksByGenre(genreEntity.id)
                    genreEntity.toSection(books)
                }
                Result.success(BukuDatabase(sections))
            } else {
                Result.failure(e)
            }
        }
    }

    suspend fun getBookDetails(bookId: String): Result<Buku> = withContext(Dispatchers.IO) {
        val cachedBook = bookDao.getBookById(bookId)
        try {
            tokenPreferences.token.firstOrNull()?.let {
                AppModule.setToken(it)
            }
            // Check if bookId is a valid UUID
            val uuid = try {
                UUID.fromString(bookId)
            } catch (e: Exception) {
                null
            }

            if (uuid == null) {
                // If not UUID, it might be a cached dummy ID or invalid
                cachedBook?.let { return@withContext Result.success(it.toBuku()) }
                return@withContext Result.failure(Exception("Invalid Book ID format"))
            }

            val response = booksApi.getBook(uuid).execute()
            
            if (response.isSuccessful) {
                val remoteBook = response.body() ?: return@withContext Result.failure(Exception("Buku tidak ditemukan"))
                val buku = remoteBook.toBuku()
                
                // Update specific book in cache if we know its genreId (preserving it if already exists)
                cachedBook?.let { 
                    bookDao.insertBooks(listOf(buku.toEntity(it.genreId)))
                }

                Result.success(buku)
            } else {
                cachedBook?.let { return@withContext Result.success(it.toBuku()) }
                Result.failure(Exception("Gagal mengambil detail buku: ${response.code()}"))
            }
        } catch (e: Exception) {
            Log.e("BukuRepository", "Error fetching book details", e)
            cachedBook?.let { return@withContext Result.success(it.toBuku()) }
            Result.failure(e)
        }
    }

    suspend fun downloadBook(bookId: String): Result<String> = withContext(Dispatchers.IO) {
        try {
            tokenPreferences.token.firstOrNull()?.let {
                AppModule.setToken(it)
            }
            val uuid = UUID.fromString(bookId)
            val response = booksApi.downloadBook(uuid).execute()
            if (response.isSuccessful) {
                val url = response.body()?.url ?: return@withContext Result.failure(Exception("URL download tidak ditemukan"))
                Result.success(url)
            } else {
                Result.failure(Exception("Gagal mendapatkan URL download: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    companion object {
        fun Book.toBuku(): Buku {
            return Buku(
                id = this.id ?: "",
                isbn = this.isbn ?: "-",
                title = this.title ?: "Untitled",
                description = this.description ?: "Description",
                page = this.pageCount ?: 0,
                author = this.author ?: "Unknown Author",
                cover = (this.coverLg ?: this.coverMd ?: this.coverSm ?: this.coverUrl).toAbsoluteUrl(),
                price = this.price
            )
        }
    }

    fun loadBooksFromAssets(): BukuDatabase {
        return try {
            val jsonString = context.assets.open("buku.json")
                .bufferedReader()
                .use { reader -> reader.readText() }
            json.decodeFromString<BukuDatabase>(jsonString)
        } catch (e: Exception) {
            Log.e("BookRepository", "Error loading books", e)
            BukuDatabase(emptyList())
        }
    }

    suspend fun saveBooksToFile(database: BukuDatabase) {
        withContext(Dispatchers.IO) {
            val jsonString = json.encodeToString(database)
            context.openFileOutput("buku.json", Context.MODE_PRIVATE).use { output ->
                output.write(jsonString.toByteArray())
            }
        }
    }
}
