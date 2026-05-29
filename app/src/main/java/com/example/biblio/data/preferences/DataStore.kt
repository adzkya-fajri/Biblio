package com.example.biblio.data.preferences

import android.content.Context
import androidx.datastore.preferences.preferencesDataStore

val Context.readingDataStore by preferencesDataStore(name = "reading_state")