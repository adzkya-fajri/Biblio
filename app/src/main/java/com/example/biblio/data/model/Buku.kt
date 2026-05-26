package com.example.biblio.data.model

import kotlinx.serialization.Serializable

@Serializable
data class Buku(
    val id: String,
    val isbn: String,
    val title: String,
    val description: String,
    val author: String,
    val page: Int,
    val cover: String
)