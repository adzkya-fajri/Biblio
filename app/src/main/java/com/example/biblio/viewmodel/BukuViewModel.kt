package com.example.biblio.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.biblio.data.model.Buku
import com.example.biblio.data.model.BukuDatabase
import com.example.biblio.data.model.Section
import com.example.biblio.data.repository.BukuRepository
import com.example.biblio.data.repository.FavoriteRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class BukuViewModel(
    private val repository: BukuRepository,
    private val favoriteRepository: FavoriteRepository
) : ViewModel() {

    private val _bookDatabase = MutableStateFlow<BukuDatabase?>(null)
    val bookDatabase: StateFlow<BukuDatabase?> = _bookDatabase.asStateFlow()

    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    val favoriteIds: StateFlow<Set<String>> = favoriteRepository.favoriteIds

    val favoriteBooks: StateFlow<List<Buku>> =
        combine(bookDatabase, favoriteIds) { db, ids ->
            db?.sections?.flatMap { it.books }?.filter { ids.contains(it.id) }.orEmpty()
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = emptyList()
        )

    /* ---------------------------------- */
    /*  Fungsi favorit – versi UI-ready   */
    /* ---------------------------------- */
    fun toggleFavorite(bookId: String) {
        viewModelScope.launch { favoriteRepository.toggleFavorite(bookId) }
    }

    fun clearAllFavorites() {
        viewModelScope.launch { favoriteRepository.clearAllFavorites() }
    }

    init {
        loadBooks()
    }

    fun loadBooks(forceRefresh: Boolean = false) {
        viewModelScope.launch {
            _isLoading.value = true
            delay(300)
            _bookDatabase.value = try {
                repository.loadBooksFromAssets()
            } catch (e: Exception) {
                repository.generateDummyData()
            }
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