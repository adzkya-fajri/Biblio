package com.example.biblio.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.biblio.data.repository.BukuRepository
import com.example.biblio.data.repository.FavoriteRepository

class BukuViewModelFactory(
    private val repository: BukuRepository,
    private val favoriteRepository: FavoriteRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(BukuViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return BukuViewModel(repository, favoriteRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}