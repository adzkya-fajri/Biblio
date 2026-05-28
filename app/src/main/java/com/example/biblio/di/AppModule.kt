package com.example.biblio.di

import android.content.Context
import androidx.room.Room
import com.example.biblio.BuildConfig
import com.example.biblio.data.local.BiblioDatabase
import com.example.biblio.data.preferences.TokenPreferences
import com.example.biblio.data.remote.apis.AuthApi
import com.example.biblio.data.remote.apis.BooksApi
import com.example.biblio.data.remote.apis.GenresApi
import com.example.biblio.data.remote.apis.ProfileApi
import com.example.biblio.data.repository.AuthRepository
import com.example.biblio.data.repository.BukuRepository
import com.example.biblio.data.repository.ProfileRepository
import com.example.biblio.data.remote.infrastructure.ApiClient
import com.example.biblio.data.repository.FavoriteRepository

object AppModule {
    private var authRepository: AuthRepository? = null
    private var bukuRepository: BukuRepository? = null
    private var favoriteRepository: FavoriteRepository? = null
    private var profileRepository: ProfileRepository? = null
    private var database: BiblioDatabase? = null

    private val apiClient by lazy {
        ApiClient(baseUrl = BuildConfig.BASE_URL, authNames = arrayOf("sanctum"))
    }

    private fun provideDatabase(context: Context): BiblioDatabase {
        return database ?: synchronized(this) {
            database ?: Room.databaseBuilder(
                context.applicationContext,
                BiblioDatabase::class.java,
                BiblioDatabase.DATABASE_NAME
            ).build().also { database = it }
        }
    }

    fun setToken(token: String) {
        apiClient.setBearerToken(token)
    }

    fun provideAuthRepository(context: Context): AuthRepository {
        return authRepository ?: synchronized(this) {
            authRepository ?: AuthRepository(
                context = context.applicationContext,
                authApi = provideAuthApi(),
                tokenPreferences = TokenPreferences(context),
                database = provideDatabase(context)
            ).also { authRepository = it }
        }
    }

    fun provideBukuRepository(context: Context): BukuRepository {
        return bukuRepository ?: synchronized(this) {
            val db = provideDatabase(context)
            bukuRepository ?: BukuRepository(
                context = context,
                booksApi = provideBooksApi(),
                genresApi = provideGenresApi(),
                tokenPreferences = TokenPreferences(context),
                bookDao = db.bookDao()
            ).also { bukuRepository = it }
        }
    }

    fun provideFavoriteRepository(context: Context): FavoriteRepository {
        return favoriteRepository ?: synchronized(this) {
            favoriteRepository ?: FavoriteRepository(context).also { favoriteRepository = it }
        }
    }

    fun provideProfileRepository(context: Context): ProfileRepository {
        return profileRepository ?: synchronized(this) {
            val db = provideDatabase(context)
            profileRepository ?: ProfileRepository(
                profileApi = provideProfileApi(),
                tokenPreferences = TokenPreferences(context),
                profileDao = db.profileDao()
            ).also { profileRepository = it }
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

    private fun provideProfileApi(): ProfileApi {
        return apiClient.createService(ProfileApi::class.java)
    }
}
