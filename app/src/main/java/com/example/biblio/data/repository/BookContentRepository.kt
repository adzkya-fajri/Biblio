package com.example.biblio.data.repository

import android.content.Context
import android.util.Log
import com.example.biblio.data.model.BookContent
import com.example.biblio.data.model.Buku
import com.example.biblio.data.model.Chapter
import com.example.biblio.data.remote.ApiClient
import com.example.biblio.data.remote.dto.toBuku
import kotlinx.serialization.json.Json
import retrofit2.HttpException

class BookContentRepository(private val context: Context) {
    private val json = Json { ignoreUnknownKeys = true }
    private val api = ApiClient.api

    data class ReaderLoadResult(
        val book: Buku,
        val content: BookContent?,
        val fileUrl: String? = null,
        val errorMessage: String? = null
    )

    /** Muat buku dari API + URL file (preview atau penuh). */
    suspend fun loadFromApi(bookId: String, preview: Boolean): ReaderLoadResult {
        return try {
            val bookDto = api.getBook(bookId)
            val buku = bookDto.toBuku()

            try {
                val download = api.getDownloadUrl(bookId, preview)
                val url = download.url
                if (!url.isNullOrBlank()) {
                    return ReaderLoadResult(
                        book = buku,
                        content = buildFileChapter(buku, url, preview),
                        fileUrl = url
                    )
                }
            } catch (e: HttpException) {
                if (e.code() == 403 && preview) {
                    return ReaderLoadResult(
                        book = buku,
                        content = buildPreviewOnlyContent(buku),
                        errorMessage = "Buku berbayar. Gunakan Beli & Baca untuk akses penuh."
                    )
                }
                throw e
            }

            ReaderLoadResult(
                book = buku,
                content = buildPreviewOnlyContent(buku),
                errorMessage = "File buku tidak tersedia."
            )
        } catch (e: Exception) {
            Log.e(TAG, "loadFromApi gagal: ${e.message}", e)
            val fallback = loadBookContentLocal(bookId)
            ReaderLoadResult(
                book = Buku(
                    id = bookId,
                    isbn = "",
                    judul = "Buku",
                    penulis = "",
                    cover = ""
                ),
                content = fallback,
                errorMessage = e.message ?: "Gagal memuat dari server"
            )
        }
    }

    private fun buildFileChapter(buku: Buku, url: String, preview: Boolean): BookContent {
        val label = if (preview && buku.isFree) "Baca Gratis" else if (preview) "Preview" else "Baca Penuh"
        val intro = buku.description?.takeIf { it.isNotBlank() }
            ?: "Membuka file buku (${buku.format.ifBlank { "digital" }}) dari server Biblio."
        return BookContent(
            bookId = buku.id,
            chapters = listOf(
                Chapter(
                    id = 1,
                    title = "$label — ${buku.judul}",
                    content = "$intro\n\nFile akan ditampilkan di bawah."
                )
            )
        )
    }

    private fun buildPreviewOnlyContent(buku: Buku): BookContent {
        val text = buku.description?.takeIf { it.isNotBlank() }
            ?: "Sinopsis belum tersedia."
        return BookContent(
            bookId = buku.id,
            chapters = listOf(
                Chapter(
                    id = 1,
                    title = "Sinopsis — ${buku.judul}",
                    content = text
                )
            )
        )
    }

    fun loadBookContentLocal(bookId: String): BookContent? {
        return try {
            val fileName = "book_content_$bookId.json"
            val jsonString = context.assets.open(fileName)
                .bufferedReader()
                .use { it.readText() }
            json.decodeFromString<BookContent>(jsonString)
        } catch (e: Exception) {
            generateDummyContent(bookId)
        }
    }

    private fun generateDummyContent(bookId: String): BookContent {
        val chapters = List(3) { chapterIndex ->
            Chapter(
                id = chapterIndex + 1,
                title = "Bab ${chapterIndex + 1}",
                content = "Konten offline untuk buku $bookId. Hubungkan ke API untuk membaca file asli."
            )
        }
        return BookContent(bookId, chapters)
    }

    companion object {
        private const val TAG = "BookContentRepository"
    }
}
