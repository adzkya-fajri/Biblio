package com.example.biblio.data.model

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.ServerTimestamp

data class Book(
    @DocumentId
    val id: String = "",
    val title: String = "",
    val author: String = "",
    val isbn: String = "",
    val cover: String = "",
    val publisher: String = "",
    val publishedDate: String = "",
    val description: String = "",
    val pages: Int = 0,
    @ServerTimestamp
    val createdAt: Timestamp? = null
)

data class Section(
    @DocumentId
    val id: String = "",
    val title: String = "",
    val order: Int = 0,
    val bookIds: List<String> = emptyList()
)

data class SectionWithBooks(
    val section: Section,
    val books: List<Book>
)