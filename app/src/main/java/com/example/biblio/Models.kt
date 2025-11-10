package com.example.biblio
data class Book(val imageRes: Int)
data class Section(val title: String, val books: List<Book>)