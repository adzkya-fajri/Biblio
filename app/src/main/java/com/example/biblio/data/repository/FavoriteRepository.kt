package com.example.biblio.data.repository

import android.content.Context
import android.content.SharedPreferences
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class FavoriteRepository(context: Context) {
    private val prefs: SharedPreferences =
        context.getSharedPreferences("biblio_favorites", Context.MODE_PRIVATE)

    private val _favoriteIds = MutableStateFlow<Set<String>>(loadFavorites())
    val favoriteIds: StateFlow<Set<String>> = _favoriteIds.asStateFlow()

    // Load favorites dari SharedPreferences
    private fun loadFavorites(): Set<String> {
        return prefs.getStringSet("favorite_book_ids", emptySet()) ?: emptySet()
    }

    // Save favorites ke SharedPreferences
    private fun saveFavorites(ids: Set<String>) {
        prefs.edit().putStringSet("favorite_book_ids", ids).apply()
    }

    // Toggle favorite (add jika belum ada, remove jika sudah ada)
    fun toggleFavorite(bookId: String) {
        val currentFavorites = _favoriteIds.value.toMutableSet()
        if (currentFavorites.contains(bookId)) {
            currentFavorites.remove(bookId)
        } else {
            currentFavorites.add(bookId)
        }
        _favoriteIds.value = currentFavorites
        saveFavorites(currentFavorites)
    }

    // Cek apakah buku ini favorit
    fun isFavorite(bookId: String): Boolean {
        return _favoriteIds.value.contains(bookId)
    }

    // Hapus semua favorit
    fun clearAllFavorites() {
        _favoriteIds.value = emptySet()
        saveFavorites(emptySet())
    }
}