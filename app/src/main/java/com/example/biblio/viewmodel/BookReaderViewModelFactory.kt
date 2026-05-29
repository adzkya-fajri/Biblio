package com.example.biblio.viewmodel

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.biblio.data.repository.BukuRepository
import com.example.biblio.data.repository.ProfileRepository
import com.example.biblio.data.repository.ReadingProgressRepository

class BookReaderViewModelFactory(
    private val application: Application,
    private val bukuRepository: BukuRepository,
    private val readingProgress: ReadingProgressRepository,
    private val profileRepository: ProfileRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(BookReaderViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return BookReaderViewModel(
                application,
                bukuRepository,
                readingProgress,
                profileRepository
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
