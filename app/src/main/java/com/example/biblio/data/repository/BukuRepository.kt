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
import java.util.UUID

class BukuRepository(
    private val context: Context,
    private val booksApi: BooksApi,
    private val genresApi: GenresApi,
    private val tokenPreferences: TokenPreferences
) {
    private val json = Json {
        ignoreUnknownKeys = true
        prettyPrint = true
    }

    suspend fun getGenreWithBooks(): Result<BukuDatabase> = withContext(Dispatchers.IO) {
        try {
            tokenPreferences.token.firstOrNull()?.let {
                com.example.biblio.di.AppModule.setToken(it)
            }
            val response = genresApi.getGenreWithBooks().execute()
            
            if (response.isSuccessful) {
                val remoteGenres = response.body() ?: emptyList()
                val sections = remoteGenres.map { remoteGenre ->
                    Section(
                        id = remoteGenre.id ?: 0,
                        title = remoteGenre.name ?: "Unknown",
                        books = remoteGenre.books?.map { it.toBuku() } ?: emptyList()
                    )
                }
                Result.success(BukuDatabase(sections))
            } else {
                Result.failure(Exception("Gagal mengambil data: ${response.code()}"))
            }
        } catch (e: Exception) {
            Log.e("BukuRepository", "Error fetching genres with books", e)
            Result.failure(e)
        }
    }

    suspend fun getBookDetails(bookId: String): Result<Buku> = withContext(Dispatchers.IO) {
        try {
            tokenPreferences.token.firstOrNull()?.let {
                com.example.biblio.di.AppModule.setToken(it)
            }
            // Check if bookId is a valid UUID
            val uuid = try {
                UUID.fromString(bookId)
            } catch (e: Exception) {
                null
            }

            if (uuid == null) {
                return@withContext Result.failure(Exception("Invalid Book ID format"))
            }

            val response = booksApi.getBook(uuid).execute()
            
            if (response.isSuccessful) {
                val remoteBook = response.body() ?: return@withContext Result.failure(Exception("Buku tidak ditemukan"))
                Result.success(remoteBook.toBuku())
            } else {
                Result.failure(Exception("Gagal mengambil detail buku: ${response.code()}"))
            }
        } catch (e: Exception) {
            Log.e("BukuRepository", "Error fetching book details", e)
            Result.failure(e)
        }
    }

    private fun Book.toBuku(): Buku {
        return Buku(
            id = this.id ?: "",
            isbn = this.isbn ?: "-",
            title = this.title ?: "Untitled",
            description = this.description ?: "Description",
            page = this.pageCount ?: 0,
            author = this.author ?: "Unknown Author",
            cover = (this.coverLg ?: this.coverMd ?: this.coverSm ?: this.coverUrl).toAbsoluteUrl()
        )
    }

    private fun String?.toAbsoluteUrl(): String {
        if (this.isNullOrBlank()) return ""
        if (this.startsWith("http")) return this
        
        // Ambil base URL dari BuildConfig, buang "/api" jika ada untuk akses file statis/storage
        val baseUrl = com.example.biblio.BuildConfig.BASE_URL
            .removeSuffix("/")
            .removeSuffix("/api")
            
        return "$baseUrl/${this.removePrefix("/")}"
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

//    fun generateDummyData(): BukuDatabase {
//        val sections = listOf(
//            Section(
//                id = 1,
//                title = "Fiksi",
//                books = List(20) { i ->
//                    Buku(
//                        id = "fiction_$i",
//                        isbn = "978-0${String.format("%09d", i)}",
//                        title = "Novel Fiksi ${i + 1}",
//                        author = "Penulis ${(i % 5) + 1}",
//                        cover = "https://picsum.photos/seed/fiction$i/300/450"
//                    )
//                }
//            ),
//            Section(
//                id = 2,
//                title = "Non-Fiksi",
//                books = List(15) { i ->
//                    Buku(
//                        id = "nonfiction_$i",
//                        isbn = "978-1${String.format("%09d", i)}",
//                        title = "Buku Non-Fiksi ${i + 1}",
//                        author = "Ahli ${(i % 3) + 1}",
//                        cover = "https://picsum.photos/seed/nonfiction$i/300/450"
//                    )
//                }
//            ),
//            Section(
//                id = 3,
//                title = "Sains & Teknologi",
//                books = List(12) { i ->
//                    Buku(
//                        id = "science_$i",
//                        isbn = "978-2${String.format("%09d", i)}",
//                        title = "Buku Sains ${i + 1}",
//                        author = "Ilmuwan ${(i % 4) + 1}",
//                        cover = "https://picsum.photos/seed/science$i/300/450"
//                    )
//                }
//            )
//        )
//
//        return BukuDatabase(sections)
//    }

    suspend fun saveBooksToFile(database: BukuDatabase) {
        withContext(Dispatchers.IO) {
            val jsonString = json.encodeToString(database)
            context.openFileOutput("buku.json", Context.MODE_PRIVATE).use { output ->
                output.write(jsonString.toByteArray())
            }
        }
    }
}
