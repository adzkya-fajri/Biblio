package com.example.biblio.viewmodel

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.biblio.di.AppModule

class ReadingProgressViewModelFactory(
    private val application: Application
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ReadingProgressViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ReadingProgressViewModel(
                application,
                AppModule.provideReadingProgressRepository(application)
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
