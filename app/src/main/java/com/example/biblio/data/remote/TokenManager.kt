package com.example.biblio.data.remote

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

private val Context.tokenDataStore by preferencesDataStore("biblio_auth")

class TokenManager(private val context: Context) {

    private val tokenKey = stringPreferencesKey("sanctum_token")

    val tokenFlow: Flow<String?> = context.tokenDataStore.data.map { prefs ->
        prefs[tokenKey]
    }

    suspend fun getToken(): String? = tokenFlow.first()

    suspend fun saveToken(token: String) {
        context.tokenDataStore.edit { prefs ->
            prefs[tokenKey] = token
        }
    }

    suspend fun clearToken() {
        context.tokenDataStore.edit { prefs ->
            prefs.remove(tokenKey)
        }
    }
}
