package com.example.biblio.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.biblio.data.repository.BookContentRepository

class BookReaderViewModelFactory(
    private val contentRepository: BookContentRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(BookReaderViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return BookReaderViewModel(contentRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}