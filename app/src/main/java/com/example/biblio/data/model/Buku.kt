package com.example.biblio.data.model

import kotlinx.serialization.Serializable

@Serializable
data class Buku(
    val id: String,
    val isbn: String,
    val judul: String,
    val penulis: String,
    val cover: String
)