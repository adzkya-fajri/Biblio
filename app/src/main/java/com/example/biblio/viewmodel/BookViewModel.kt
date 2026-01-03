package com.example.biblio.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.biblio.data.model.Book
import com.example.biblio.data.model.Section
import com.example.biblio.data.model.SectionWithBooks
import com.example.biblio.data.repository.FirebaseBookRepository
import com.example.biblio.data.repository.FirebaseFavoriteRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class BookViewModel(
    private val bookRepository: FirebaseBookRepository,
    private val favoriteRepository: FirebaseFavoriteRepository
) : ViewModel() {

    // ✅ Ganti jadi Section (tanpa books)
    private val _sections = MutableStateFlow<List<Section>>(emptyList())
    val sections = _sections.asStateFlow()

    // ✅ Track loaded books per section
    private val _sectionBooks = MutableStateFlow<Map<String, List<Book>>>(emptyMap())
    val sectionBooks = _sectionBooks.asStateFlow()

    private val _loadingSections = MutableStateFlow<Set<String>>(emptySet())
    val loadingSections = _loadingSections.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    val favoriteIds = favoriteRepository.favoriteIds

    private val _selectedBook = MutableStateFlow<Book?>(null)
    val selectedBook = _selectedBook.asStateFlow()

    // Search
    private val searchQuery = MutableStateFlow("")
    private val _searchResults = MutableStateFlow<List<Book>>(emptyList())
    val searchResults = _searchResults.asStateFlow()
    private val _isSearching = MutableStateFlow(false)
    val isSearching = _isSearching.asStateFlow()

    val favoriteBooks: StateFlow<List<Book>> =
        combine(sectionBooks, favoriteIds) { booksMap, ids ->
            booksMap.values.flatten().filter { it.id in ids }
        }.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5_000),
            emptyList()
        )

    init {
        loadSections()
        setupSearch()
    }

    // ✅ Load sections only
    fun loadSections(forceRefresh: Boolean = false) {
        viewModelScope.launch {
            _isLoading.value = true
            _sections.value = bookRepository.getSections()
            _isLoading.value = false
        }
    }

    // ✅ Lazy load books for specific section
    fun loadSectionBooks(sectionId: String, bookIds: List<String>) {
        if (_sectionBooks.value.containsKey(sectionId)) return // sudah load
        if (_loadingSections.value.contains(sectionId)) return // sedang load

        viewModelScope.launch {
            _loadingSections.value = _loadingSections.value + sectionId

            val books = bookRepository.loadSectionBooks(bookIds)
            _sectionBooks.value = _sectionBooks.value + (sectionId to books)

            _loadingSections.value = _loadingSections.value - sectionId
        }
    }

    fun loadBook(bookId: String) {
        viewModelScope.launch {
            _selectedBook.value = bookRepository.getBookById(bookId)
        }
    }

    private fun setupSearch() {
        searchQuery
            .debounce(300)
            .distinctUntilChanged()
            .onEach { query ->
                if (query.isBlank()) {
                    _searchResults.value = emptyList()
                } else {
                    _isSearching.value = true
                    _searchResults.value = bookRepository.searchBooks(query)
                    _isSearching.value = false
                }
            }
            .launchIn(viewModelScope)
    }

    fun searchBooks(query: String) {
        searchQuery.value = query
    }

    fun toggleFavorite(bookId: String) {
        viewModelScope.launch { favoriteRepository.toggleFavorite(bookId) }
    }

    fun clearAllFavorites() {
        viewModelScope.launch { favoriteRepository.clearAllFavorites() }
    }
}



