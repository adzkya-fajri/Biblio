package com.example.biblio.data.preferences

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.dataStore by preferencesDataStore("auth_prefs")

class TokenPreferences(private val context: Context) {
    companion object {
        val SANCTUM_TOKEN = stringPreferencesKey("sanctum_token")
    }

    val token: Flow<String?> = context.dataStore.data
        .map { it[SANCTUM_TOKEN] }

    suspend fun saveToken(token: String) {
        context.dataStore.edit { it[SANCTUM_TOKEN] = token }
    }

    suspend fun clearToken() {
        context.dataStore.edit { it.remove(SANCTUM_TOKEN) }
    }
}