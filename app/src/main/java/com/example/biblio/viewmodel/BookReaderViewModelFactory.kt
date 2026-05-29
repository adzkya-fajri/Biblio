package com.example.biblio.viewmodel

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.biblio.data.repository.BukuRepository
import com.example.biblio.data.repository.ProfileRepository
import com.example.biblio.data.repository.ReadingProgressRepository
import com.example.biblio.data.preferences.ReaderPreferencesManager

class BookReaderViewModelFactory(
    private val application: Application,
    private val bukuRepository: BukuRepository,
    private val readingProgress: ReadingProgressRepository,
    private val profileRepository: ProfileRepository,
    private val readerPreferences: ReaderPreferencesManager
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(BookReaderViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return BookReaderViewModel(
                application,
                bukuRepository,
                readingProgress,
                profileRepository,
                readerPreferences
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

