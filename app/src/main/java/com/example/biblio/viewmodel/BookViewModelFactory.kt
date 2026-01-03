package com.example.biblio.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.biblio.data.repository.FirebaseBookRepository
import com.example.biblio.data.repository.FirebaseFavoriteRepository

class BookViewModelFactory(
    private val bookRepository: FirebaseBookRepository,
    private val favoriteRepository: FirebaseFavoriteRepository
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(BookViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return BookViewModel(
                bookRepository = bookRepository,
                favoriteRepository = favoriteRepository
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
