package com.example.biblio.viewmodel

import android.app.Application
import android.util.Log
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.biblio.data.model.Buku
import com.example.biblio.data.preferences.dataStore
import com.example.biblio.data.preferences.readingDataStore
import com.example.biblio.data.repository.ReadingProgressRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

// ReadingProgressViewModel.kt
class ReadingProgressViewModel(
    private val context: Application,
    private val repository: ReadingProgressRepository
) : AndroidViewModel(context) {

    private val dataStore = context.readingDataStore
    private val json = Json { ignoreUnknownKeys = true }

    val currentBook = MutableStateFlow<Buku?>(null)
    val currentPage = MutableStateFlow(0)

    init {
        viewModelScope.launch {
            // Priority 1: DataStore (Last active session in current device)
            dataStore.data.first().let { prefs ->
                try {
                    val bookJson = prefs[stringPreferencesKey("book_json")] ?: return@let
                    val page = prefs[intPreferencesKey("current_page")] ?: 0
                    currentBook.value = json.decodeFromString<Buku>(bookJson)
                    currentPage.value = page
                } catch (e: Exception) {
                    Log.e("ReadingProgressVM", "Error decoding cached book", e)
                }
            }

            // Priority 2: If DataStore empty, try local DB for latest non-dismissed progress
            if (currentBook.value == null) {
                repository.getLatestActiveLocalProgress()?.let { localProgress ->
                    // We need the Book object. Usually this would be fetched from BukuRepository.
                    // For now, if currentBook is null, we wait for Profile sync in MainScreen.
                }
            }
        }
    }

    fun updateProgress(book: Buku, page: Int) {
        currentBook.value = book
        currentPage.value = page

        viewModelScope.launch {
            dataStore.edit { prefs ->
                prefs[stringPreferencesKey("book_json")] = json.encodeToString(book)
                prefs[intPreferencesKey("current_page")] = page
            }
        }
    }

    /**
     * Sembunyikan bar dengan menandai sebagai dismissed di DB.
     */
    fun hideBar() {
        val bookId = currentBook.value?.id
        currentBook.value = null
        currentPage.value = 0

        viewModelScope.launch {
            dataStore.edit { it.clear() }
            bookId?.let { repository.dismissProgress(it) }
        }
    }

    /**
     * Sinkronisasi ke server (Panggil saat keluar dari reader)
     */
    fun syncWithRemote() {
        val book = currentBook.value ?: return
        val page = currentPage.value

        viewModelScope.launch(Dispatchers.IO) {
            repository.upsertRemoteProgress(book.id, page).onSuccess {
                // Success: Profile is updated via repository
            }.onFailure {
                // Log error
            }
        }
    }


    /**
     * Hapus progress secara permanen (Lokal & Remote)
     */
//    fun deletePermanently() {
//        val bookId = currentBook.value?.id ?: return
//        hideBar()
//        viewModelScope.launch(Dispatchers.IO) {
//            repository.deleteRemoteProgress()
//        }
//    }

    fun clear() {
        hideBar()
    }
}
