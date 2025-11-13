package com.example.biblio.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.biblio.data.model.Buku
import com.example.biblio.data.model.BukuDatabase
import com.example.biblio.data.model.Section
import com.example.biblio.data.repository.BukuRepository
import com.example.biblio.data.repository.FavoriteRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlin.collections.filter

class BukuViewModel(
    private val repository: BukuRepository,
    private val favoriteRepository: FavoriteRepository  // ✅ TAMBAHAN: Parameter wajib
) : ViewModel() {
    private val _bookDatabase = MutableStateFlow<BukuDatabase?>(null)
    val bookDatabase: StateFlow<BukuDatabase?> = _bookDatabase.asStateFlow()

    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    val favoriteIds: StateFlow<Set<String>> = favoriteRepository.favoriteIds

    val favoriteBooks: StateFlow<List<Buku>> = combine(
        bookDatabase,
        favoriteIds
    ) { database, favIds ->
        database?.sections
            ?.flatMap { it.books }
            ?.filter { book -> favIds.contains(book.id) }
            ?: emptyList()
    }.stateIn(
        scope = viewModelScope,          // ✅ TAMBAHAN
        started = SharingStarted.WhileSubscribed(5000),  // ✅ TAMBAHAN
        initialValue = emptyList()       // ✅ TAMBAHAN
    )

    fun toggleFavorite(bookId: String) {
        favoriteRepository.toggleFavorite(bookId)
    }

    init {
        loadBooks()
    }

    fun loadBooks(forceRefresh: Boolean = false) {
        viewModelScope.launch {
            _isLoading.value = true
            delay(300)

            val data = try {
                repository.loadBooksFromAssets()
            } catch (e: Exception) {
                repository.generateDummyData()
            }

            _bookDatabase.value = data
            _isLoading.value = false
        }
    }

    fun searchBooks(query: String): List<Pair<Section, Buku>> {
        val db = _bookDatabase.value ?: return emptyList()

        return db.sections.flatMap { section ->
            section.books
                .filter {
                    it.judul.contains(query, ignoreCase = true) ||
                            it.penulis.contains(query, ignoreCase = true) ||
                            it.isbn.contains(query, ignoreCase = true)
                }
                .map { section to it }
        }
    }
}