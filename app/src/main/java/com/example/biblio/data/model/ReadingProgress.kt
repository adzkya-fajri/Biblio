package com.example.biblio.data.model

data class ReadingProgress(
    val userId: String,
    val bookId: String,
    val bookTitle: String,
    val bookAuthor: String,
    val bookCover: String,
    val currentChapter: Int = 0,
    val currentPage: Int = 0,
    val totalPages: Int,
    val lastReadAt: Long = System.currentTimeMillis(),
    val progress: Float = 0f // 0.0 - 1.0
)