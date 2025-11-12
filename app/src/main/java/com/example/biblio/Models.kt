package com.example.biblio

// MODEL UNTUK SECTION
data class Section(
    val title: String,
    val books: List<Book>
)

// MODEL UNTUK BOOK - SATU VERSI SAJA!
data class Book(
    val cover: Int,              // ID resource drawable (R.drawable.xxx)
    val title: String = "",      // Judul buku (opsional, default "")
    val author: String = "",     // Nama penulis (opsional, default "")
    val category: String = ""    // Kategori buku (opsional, default "")
)