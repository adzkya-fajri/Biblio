package com.example.biblio.data.repository

import android.content.Context
import android.util.Log
import com.example.biblio.data.model.Buku
import com.example.biblio.data.model.BukuDatabase
import com.example.biblio.data.model.Section
import com.example.biblio.data.remote.ApiClient
import com.example.biblio.data.remote.dto.toBuku
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class BukuRepository(private val context: Context) {
    private val json = Json {
        ignoreUnknownKeys = true
        prettyPrint = true
    }
    private val api = ApiClient.api

    /** Muat katalog dari Laravel API (data Oracle Cloud via Filament). */
    suspend fun loadBooksFromApi(): BukuDatabase {
        val genres = api.getGenresWithBooks()
        val sections = genres.map { genre ->
            Section(
                id = genre.id,
                title = genre.name,
                books = genre.books.map { it.toBuku() }
            )
        }
        return BukuDatabase(sections)
    }

    fun loadBooksFromAssets(): BukuDatabase {
        return try {
            val jsonString = context.assets.open("buku.json")
                .bufferedReader()
                .use { reader -> reader.readText() }
            json.decodeFromString<BukuDatabase>(jsonString)
        } catch (e: Exception) {
            Log.e(TAG, "Error loading books from assets", e)
            generateDummyData()
        }
    }

    fun generateDummyData(): BukuDatabase {
        val sections = listOf(
            Section(
                id = 1,
                title = "Fiksi",
                books = List(20) { i ->
                    Buku(
                        id = "fiction_$i",
                        isbn = "978-0${String.format("%09d", i)}",
                        judul = "Novel Fiksi ${i + 1}",
                        penulis = "Penulis ${(i % 5) + 1}",
                        cover = "https://picsum.photos/seed/fiction$i/300/450",
                        price = if (i % 3 == 0) 0 else 25000
                    )
                }
            ),
            Section(
                id = 2,
                title = "Non-Fiksi",
                books = List(15) { i ->
                    Buku(
                        id = "nonfiction_$i",
                        isbn = "978-1${String.format("%09d", i)}",
                        judul = "Buku Non-Fiksi ${i + 1}",
                        penulis = "Ahli ${(i % 3) + 1}",
                        cover = "https://picsum.photos/seed/nonfiction$i/300/450",
                        price = 15000
                    )
                }
            )
        )
        return BukuDatabase(sections)
    }

    suspend fun saveBooksToFile(database: BukuDatabase) {
        withContext(Dispatchers.IO) {
            val jsonString = json.encodeToString(database)
            context.openFileOutput("buku.json", Context.MODE_PRIVATE).use { output ->
                output.write(jsonString.toByteArray())
            }
        }
    }

    companion object {
        private const val TAG = "BukuRepository"
    }
}
