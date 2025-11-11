package com.example.biblio

data class Section(val title: String, val books: List<Book>)
data class Book(val title: String, val cover: Int, val author: String)