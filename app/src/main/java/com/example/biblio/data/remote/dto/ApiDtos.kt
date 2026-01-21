package com.example.biblio.data.remote.dto

import com.example.biblio.data.model.Buku
import com.google.gson.annotations.SerializedName

data class AuthResponse(
    val user: ApiUser?,
    val token: String?
)

data class ApiUser(
    val id: Int?,
    val name: String?,
    val email: String?
)

data class GenreWithBooksDto(
    val id: Int,
    val name: String,
    val books: List<BookDto> = emptyList()
)

data class BookDto(
    val id: String,
    val title: String,
    val author: String,
    @SerializedName("cover_url") val coverUrl: String? = null,
    val isbn: String? = null,
    val description: String? = null,
    val price: Int = 0,
    val format: String? = null,
    @SerializedName("page_count") val pageCount: Int? = null,
    val lang: String? = null
)

data class DownloadResponse(
    val url: String?
)

data class ApiErrorResponse(
    val message: String? = null
)

fun BookDto.toBuku(): Buku = Buku(
    id = id,
    isbn = isbn.orEmpty(),
    judul = title,
    penulis = author,
    cover = coverUrl.orEmpty(),
    price = price,
    description = description,
    format = format.orEmpty(),
    pageCount = pageCount
)
