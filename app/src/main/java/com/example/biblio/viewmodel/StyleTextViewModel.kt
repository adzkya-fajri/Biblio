package com.example.biblio.ui.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.biblio.data.preferences.FontPreferencesManager
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.SharingStarted

class StyleTextViewModel(
    private val fontPreferencesManager: FontPreferencesManager
) : ViewModel() {

    val fontStyle: StateFlow<String> = fontPreferencesManager.getFontStyle()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = "default"
        )

    val fontSize: StateFlow<String> = fontPreferencesManager.getFontSize()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = "medium"
        )

    fun updateFontStyle(style: String) {
        viewModelScope.launch {
            fontPreferencesManager.setFontStyle(style)
        }
    }

    fun updateFontSize(size: String) {
        viewModelScope.launch {
            fontPreferencesManager.setFontSize(size)
        }
    }
}

class StyleTextViewModelFactory(private val context: Context) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return StyleTextViewModel(
            FontPreferencesManager(context)
        ) as T
    }
}