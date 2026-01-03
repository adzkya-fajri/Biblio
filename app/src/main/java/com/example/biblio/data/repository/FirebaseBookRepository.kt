package com.example.biblio.data.repository

import android.util.Log
import com.example.biblio.data.model.Book
import com.example.biblio.data.model.Section
import com.example.biblio.data.model.SectionWithBooks
import com.google.firebase.firestore.FieldPath
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.tasks.await

class FirebaseBookRepository(
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
) {

    private val booksCol = firestore.collection("books")
    private val sectionsCol = firestore.collection("sections")
    private val bookCache = mutableMapOf<String, Book>()

    // ✅ Fetch sections aja (tanpa books)
    suspend fun getSections(): List<Section> {
        return try {
            sectionsCol
                .orderBy("order", Query.Direction.ASCENDING)
                .get()
                .await()
                .toObjects(Section::class.java)
        } catch (e: Exception) {
            Log.e("FirebaseRepo", "Error loading sections", e)
            emptyList()
        }
    }

    // ✅ Lazy load books untuk 1 section
    suspend fun loadSectionBooks(
        bookIds: List<String>,
        forceRefresh: Boolean = false
    ): List<Book> {
        if (bookIds.isEmpty()) return emptyList()

        val missingIds = if (forceRefresh) {
            bookIds
        } else {
            bookIds.filterNot { it in bookCache }
        }

        if (missingIds.isNotEmpty()) {
            try {
                missingIds.chunked(10).forEach { chunk ->
                    val books = booksCol
                        .whereIn(FieldPath.documentId(), chunk)
                        .get()
                        .await()
                        .toObjects(Book::class.java)

                    books.forEach { bookCache[it.id] = it }
                }
            } catch (e: Exception) {
                Log.e("FirebaseRepo", "Error loading books", e)
            }
        }

        return bookIds.mapNotNull { bookCache[it] }
    }

    suspend fun getAllSections(
        forceRefresh: Boolean = false
    ): List<SectionWithBooks> = coroutineScope {

        val sections = sectionsCol
            .orderBy("order", Query.Direction.ASCENDING)
            .get()
            .await()
            .toObjects(Section::class.java)

        Log.d("FirebaseRepo", "Sections loaded: ${sections.size}")

        sections.map { section ->
            async {
                Log.d("FirebaseRepo", "Loading books for ${section.title}: ${section.bookIds}") // ← tambah
                val books = loadBooksByIds(
                    ids = section.bookIds,
                    forceRefresh = forceRefresh
                )
                Log.d("FirebaseRepo", "Books loaded: ${books.size}") // ← tambah
                SectionWithBooks(section, books)
            }
        }.awaitAll()
    }

    private suspend fun loadBooksByIds(
        ids: List<String>,
        forceRefresh: Boolean
    ): List<Book> {
        if (ids.isEmpty()) return emptyList()

        val missingIds =
            if (forceRefresh) ids
            else ids.filterNot { it in bookCache }

        if (missingIds.isNotEmpty()) {
            missingIds
                .chunked(10)
                .forEach { chunk ->
                    val books = booksCol
                        .whereIn(FieldPath.documentId(), chunk)
                        .get()
                        .await()
                        .toObjects(Book::class.java)

                    books.forEach { bookCache[it.id] = it }
                }
        }

        return ids.mapNotNull { bookCache[it] }
    }

    suspend fun getBookById(
        id: String,
        forceRefresh: Boolean = false
    ): Book? {
        if (!forceRefresh && bookCache.containsKey(id)) {
            return bookCache[id]
        }

        val book = booksCol.document(id)
            .get()
            .await()
            .toObject(Book::class.java)

        book?.let { bookCache[id] = it }
        return book
    }

    suspend fun searchBooks(query: String): List<Book> {
        if (query.isBlank()) return emptyList()

        return try {
            val allBooks = booksCol.get().await().toObjects(Book::class.java)
            allBooks.filter {
                it.title.contains(query, ignoreCase = true) ||
                        it.author.contains(query, ignoreCase = true) ||
                        it.isbn.contains(query, ignoreCase = true)
            }
        } catch (e: Exception) {
            Log.e("FirebaseRepo", "Error searching", e)
            emptyList()
        }
    }

    fun clearCache() {
        bookCache.clear()
    }
}
