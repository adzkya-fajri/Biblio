package com.example.biblio.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.biblio.data.model.BookContent
import com.example.biblio.data.model.Buku
import com.example.biblio.data.model.Chapter
import com.example.biblio.data.repository.BookContentRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class ReaderSettings(
    val isDarkMode: Boolean = false,
    val fontSize: Int = 16,
    val lineSpacing: Float = 1.5f
)

class BookReaderViewModel(
    private val contentRepository: BookContentRepository
) : ViewModel() {

    private val _bookInfo = MutableStateFlow<Buku?>(null)
    val bookInfo: StateFlow<Buku?> = _bookInfo.asStateFlow()

    private val _bookContent = MutableStateFlow<BookContent?>(null)
    val bookContent: StateFlow<BookContent?> = _bookContent.asStateFlow()

    private val _currentChapterIndex = MutableStateFlow(0)
    val currentChapterIndex: StateFlow<Int> = _currentChapterIndex.asStateFlow()

    private val _settings = MutableStateFlow(ReaderSettings())
    val settings: StateFlow<ReaderSettings> = _settings.asStateFlow()

    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    fun loadBook(book: Buku) {
        viewModelScope.launch {
            _isLoading.value = true
            _bookInfo.value = book

            val content = contentRepository.loadBookContent(book.id)
            _bookContent.value = content

            _isLoading.value = false
        }
    }

    fun goToChapter(chapterIndex: Int) {
        val content = _bookContent.value ?: return
        if (chapterIndex in content.chapters.indices) {
            _currentChapterIndex.value = chapterIndex
        }
    }

    fun nextChapter() {
        val content = _bookContent.value ?: return
        if (_currentChapterIndex.value < content.chapters.size - 1) {
            _currentChapterIndex.value++
        }
    }

    fun previousChapter() {
        if (_currentChapterIndex.value > 0) {
            _currentChapterIndex.value--
        }
    }

    fun toggleDarkMode() {
        _settings.value = _settings.value.copy(
            isDarkMode = !_settings.value.isDarkMode
        )
    }

    fun updateFontSize(size: Int) {
        _settings.value = _settings.value.copy(fontSize = size)
    }

    fun updateLineSpacing(spacing: Float) {
        _settings.value = _settings.value.copy(lineSpacing = spacing)
    }
}
