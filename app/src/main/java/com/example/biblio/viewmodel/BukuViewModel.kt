package com.example.biblio.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.biblio.data.model.Buku
import com.example.biblio.data.model.BukuDatabase
import com.example.biblio.data.model.Section
import com.example.biblio.data.repository.BukuRepository
import com.example.biblio.data.repository.FavoriteRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.CreationExtras
import com.example.biblio.di.AppModule

import kotlinx.coroutines.withTimeout

class BukuViewModel(
    private val repository: BukuRepository,
    private val favoriteRepository: FavoriteRepository
) : ViewModel() {

    companion object {
        val Factory: ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {
                val application = checkNotNull(extras[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY])
                return BukuViewModel(
                    AppModule.provideBukuRepository(application),
                    AppModule.provideFavoriteRepository(application)
                ) as T
            }
        }
    }

    private val _bookDatabase = MutableStateFlow<BukuDatabase?>(null)
    val bookDatabase: StateFlow<BukuDatabase?> = _bookDatabase.asStateFlow()

    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    private val _currentBook = MutableStateFlow<Buku?>(null)
    val currentBook: StateFlow<Buku?> = _currentBook.asStateFlow()

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
            _errorMessage.value = null
            
            try {
                // Timeout 20 detik
                val result = withTimeout(20_000) {
                    repository.getGenreWithBooks()
                }

                result.onSuccess {
                    _bookDatabase.value = it
                    _errorMessage.value = null
                }.onFailure { e ->
                    _errorMessage.value = "Gagal memuat buku: ${e.localizedMessage}"
                }
            } catch (e: Exception) {
                _errorMessage.value = when (e) {
                    is kotlinx.coroutines.TimeoutCancellationException -> "Koneksi lambat (Timeout 20 detik). Silakan coba lagi."
                    else -> "Terjadi kesalahan: ${e.localizedMessage}"
                }
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun fetchBookDetails(bookId: String) {
        viewModelScope.launch {
            _currentBook.value = null // Bersihkan data lama agar tidak muncul saat transisi ke buku baru
            _isLoading.value = true
            _errorMessage.value = null
            try {
                val result = withTimeout(20_000) {
                    repository.getBookDetails(bookId)
                }
                _currentBook.value = result.getOrNull()
                if (result.isFailure) {
                    _errorMessage.value = "Gagal memuat detail buku."
                }
            } catch (e: Exception) {
                _errorMessage.value = if (e is kotlinx.coroutines.TimeoutCancellationException) {
                    "Koneksi lambat (Timeout 20 detik)."
                } else {
                    "Terjadi kesalahan: ${e.localizedMessage}"
                }
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun searchBooks(query: String): List<Pair<Section, Buku>> {
        val db = _bookDatabase.value ?: return emptyList()
        return db.sections.flatMap { section ->
            section.books
                .filter {
                    it.title.contains(query, ignoreCase = true) ||
                            it.author.contains(query, ignoreCase = true) ||
                            it.isbn.contains(query, ignoreCase = true)
                }
                .map { section to it }
        }
    }
}
