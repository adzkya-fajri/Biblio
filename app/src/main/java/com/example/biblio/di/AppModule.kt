package com.example.biblio.di

import android.content.Context
import com.example.biblio.BuildConfig
import com.example.biblio.data.preferences.TokenPreferences
import com.example.biblio.data.remote.apis.AuthApi
import com.example.biblio.data.remote.apis.BooksApi
import com.example.biblio.data.remote.apis.GenresApi
import com.example.biblio.data.repository.AuthRepository
import com.example.biblio.data.repository.BukuRepository
import com.example.biblio.data.remote.infrastructure.ApiClient
import com.example.biblio.data.repository.FavoriteRepository

object AppModule {
    private var authRepository: AuthRepository? = null
    private var bukuRepository: BukuRepository? = null
    private var favoriteRepository: FavoriteRepository? = null

    private val apiClient by lazy {
        ApiClient(baseUrl = BuildConfig.BASE_URL, authNames = arrayOf("sanctum"))
    }

    fun setToken(token: String) {
        apiClient.setBearerToken(token)
    }

    fun provideAuthRepository(context: Context): AuthRepository {
        return authRepository ?: synchronized(this) {
            val tokenPreferences = TokenPreferences(context)
            val instance = AuthRepository(
                authApi = provideAuthApi(),
                tokenPreferences = tokenPreferences
            )
            authRepository = instance
            instance
        }
    }

    fun provideBukuRepository(context: Context): BukuRepository {
        return bukuRepository ?: synchronized(this) {
            val tokenPreferences = TokenPreferences(context)
            val instance = BukuRepository(
                context = context,
                booksApi = provideBooksApi(),
                genresApi = provideGenresApi(),
                tokenPreferences = tokenPreferences
            )
            bukuRepository = instance
            instance
        }
    }

    fun provideFavoriteRepository(context: Context): FavoriteRepository {
        return favoriteRepository ?: synchronized(this) {
            val instance = FavoriteRepository(context)
            favoriteRepository = instance
            instance
        }
    }

    private fun provideAuthApi(): AuthApi {
        return apiClient.createService(AuthApi::class.java)
    }

    private fun provideBooksApi(): BooksApi {
        return apiClient.createService(BooksApi::class.java)
    }

    private fun provideGenresApi(): GenresApi {
        return apiClient.createService(GenresApi::class.java)
    }
}
