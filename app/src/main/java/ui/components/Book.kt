package com.example.biblio

data class Book(
    val coverResId: Int,
    val title: String = "",
    val author: String = "",
    val category: String = ""
)

data class Section(
    val title: String,
    val books: List<Book>
)