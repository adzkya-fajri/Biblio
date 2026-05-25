package com.example.biblio.di

import android.content.Context
import com.example.biblio.BuildConfig
import com.example.biblio.data.preferences.TokenPreferences
import com.example.biblio.data.remote.apis.AuthApi
import com.example.biblio.data.repository.AuthRepository
import com.example.biblio.infrastructure.ApiClient

object AppModule {
    private var authRepository: AuthRepository? = null

    fun provideAuthRepository(context: Context): AuthRepository {
        return authRepository ?: synchronized(this) {
            val instance = AuthRepository(
                authApi = provideAuthApi(),
                tokenPreferences = TokenPreferences(context)
            )
            authRepository = instance
            instance
        }
    }

    private fun provideAuthApi(): AuthApi {
        // Use the BASE_URL from local.properties via BuildConfig
        val apiClient = ApiClient(baseUrl = BuildConfig.BASE_URL)
        return apiClient.createService(AuthApi::class.java)
    }
}