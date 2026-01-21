package com.example.biblio.data.model

import kotlinx.serialization.Serializable

@Serializable
data class Buku(
    val id: String,
    val isbn: String,
    val judul: String,
    val penulis: String,
    val cover: String,
    val price: Int = 0,
    val description: String? = null,
    val format: String = "",
    val pageCount: Int? = null
) {
    val isFree: Boolean get() = price == 0
}