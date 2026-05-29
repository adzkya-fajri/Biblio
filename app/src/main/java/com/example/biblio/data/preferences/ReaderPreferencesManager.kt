package com.example.biblio.data.preferences

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import com.example.biblio.viewmodel.ReaderTheme
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.readerDataStore: DataStore<Preferences> by preferencesDataStore(
    name = "reader_settings"
)

class ReaderPreferencesManager(private val context: Context) {
    companion object {
        private val THEME_KEY = stringPreferencesKey("reader_theme")
        private val FONT_SIZE_KEY = doublePreferencesKey("reader_font_size")
    }

    val theme: Flow<ReaderTheme> = context.readerDataStore.data.map { preferences ->
        val themeName = preferences[THEME_KEY] ?: ReaderTheme.AUTO.name
        try {
            ReaderTheme.valueOf(themeName)
        } catch (e: Exception) {
            ReaderTheme.AUTO
        }
    }

    val fontSize: Flow<Double> = context.readerDataStore.data.map { preferences ->
        preferences[FONT_SIZE_KEY] ?: 1.0
    }

    suspend fun setTheme(theme: ReaderTheme) {
        context.readerDataStore.edit { preferences ->
            preferences[THEME_KEY] = theme.name
        }
    }

    suspend fun setFontSize(size: Double) {
        context.readerDataStore.edit { preferences ->
            preferences[FONT_SIZE_KEY] = size
        }
    }
}
