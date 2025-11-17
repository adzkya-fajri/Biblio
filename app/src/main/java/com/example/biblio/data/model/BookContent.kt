package com.example.biblio.data.model

import kotlinx.serialization.Serializable

@Serializable
data class BookContent(
    val bookId: String,
    val chapters: List<Chapter>
)

@Serializable
data class Chapter(
    val id: Int,
    val title: String,
    val content: String
)