//package com.example.biblio.viewmodel
//
//import androidx.lifecycle.ViewModel
//import androidx.lifecycle.viewModelScope
//import com.example.biblio.data.model.ReadingProgress
//import com.example.biblio.data.repository.ReadingProgressRepository
//import com.google.firebase.Firebase
//import com.google.firebase.auth.auth
//import kotlinx.coroutines.flow.Flow
//import kotlinx.coroutines.flow.SharingStarted
//import kotlinx.coroutines.flow.StateFlow
//import kotlinx.coroutines.flow.stateIn
//import kotlinx.coroutines.launch
//
//class ReadingProgressViewModel(
//    private val repository: ReadingProgressRepository
//) : ViewModel() {
//
//    private val userId = Firebase.auth.currentUser?.uid ?: ""
//
//    // Last read book untuk NowReadingBar
//    val lastReadBook: StateFlow<ReadingProgress?> = repository
//        .getLastReadBook(userId)
//        .stateIn(
//            scope = viewModelScope,
//            started = SharingStarted.WhileSubscribed(5000),
//            initialValue = null
//        )
//
//    // Reading history untuk KoleksiScreen
//    val readingHistory: StateFlow<List<ReadingProgress>> = repository
//        .getReadingHistory(userId)
//        .stateIn(
//            scope = viewModelScope,
//            started = SharingStarted.WhileSubscribed(5000),
//            initialValue = emptyList()
//        )
//
//    // Update progress (dipanggil dari BookReaderScreen)
//    fun updateProgress(
//        bookId: String,
//        bookTitle: String,
//        bookAuthor: String,
//        bookCover: String,
//        currentChapter: Int,
//        currentPage: Int,
//        totalPages: Int
//    ) {
//        viewModelScope.launch {
//            val progress = ReadingProgress(
//                userId = userId,
//                bookId = bookId,
//                bookTitle = bookTitle,
//                bookAuthor = bookAuthor,
//                bookCover = bookCover,
//                currentChapter = currentChapter,
//                currentPage = currentPage,
//                totalPages = totalPages,
//                lastReadAt = System.currentTimeMillis(),
//                progress = currentPage.toFloat() / totalPages.toFloat()
//            )
//            repository.updateProgress(progress)
//        }
//    }
//
//    // Remove dari reading list
//    fun removeFromReadingList(bookId: String) {
//        viewModelScope.launch {
//            repository.deleteProgress(userId, bookId)
//        }
//    }
//}