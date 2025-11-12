package com.example.biblio.data.model

import kotlinx.serialization.Serializable

@Serializable
data class Section(
    val id: Int,
    val title: String,
    val books: List<Buku>
)