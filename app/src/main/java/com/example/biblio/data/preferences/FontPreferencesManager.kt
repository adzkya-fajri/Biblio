package com.example.biblio.data.preferences

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(
    name = "font_preferences"
)

class FontPreferencesManager(private val context: Context) {
    companion object {
        private val FONT_STYLE_KEY = stringPreferencesKey("font_style")
        private val FONT_SIZE_KEY = stringPreferencesKey("font_size")
    }

    suspend fun setFontStyle(style: String) {
        context.dataStore.edit { preferences ->
            preferences[FONT_STYLE_KEY] = style
        }
    }

    suspend fun setFontSize(size: String) {
        context.dataStore.edit { preferences ->
            preferences[FONT_SIZE_KEY] = size
        }
    }

    fun getFontStyle(): Flow<String> = context.dataStore.data.map { preferences ->
        preferences[FONT_STYLE_KEY] ?: "default"
    }

    fun getFontSize(): Flow<String> = context.dataStore.data.map { preferences ->
        preferences[FONT_SIZE_KEY] ?: "medium"
    }
}