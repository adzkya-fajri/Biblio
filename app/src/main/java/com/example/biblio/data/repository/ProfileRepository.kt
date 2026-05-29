package com.example.biblio.data.repository

import com.example.biblio.data.local.dao.ProfileDao
import com.example.biblio.data.local.entities.toEntity
import com.example.biblio.data.local.entities.toUser
import com.example.biblio.data.preferences.TokenPreferences
import com.example.biblio.data.remote.apis.ProfileApi
import com.example.biblio.data.remote.dto.ApiMessageResponse
import com.example.biblio.data.remote.dto.AvatarResponse
import com.example.biblio.data.remote.dto.User
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File

class ProfileRepository(
    private val profileApi: ProfileApi,
    private val tokenPreferences: TokenPreferences,
    private val profileDao: ProfileDao
) {
    val profileFlow: Flow<User?> = profileDao.getProfileFlow().map { it?.toUser() }

    suspend fun getCachedProfile(): User? = withContext(Dispatchers.IO) {
        profileDao.getProfile()?.toUser()
    }

    suspend fun getProfile(): Result<User> = withContext(Dispatchers.IO) {
        try {
            ensureToken()
            val response = profileApi.getProfile().execute()
            if (response.isSuccessful) {
                val user = response.body()!!
                profileDao.insertProfile(user.toEntity())
                Result.success(user)
            } else {
                val cached = profileDao.getProfile()
                if (cached != null) {
                    Result.success(cached.toUser())
                } else {
                    Result.failure(Exception("Error getting profile: ${response.code()}"))
                }
            }
        } catch (e: Exception) {
            val cached = profileDao.getProfile()
            if (cached != null) {
                Result.success(cached.toUser())
            } else {
                Result.failure(e)
            }
        }
    }

    suspend fun uploadAvatar(file: File): Result<AvatarResponse> = withContext(Dispatchers.IO) {
        try {
            ensureToken()
            val requestFile = file.asRequestBody("image/*".toMediaTypeOrNull())
            val body = MultipartBody.Part.createFormData("avatar", file.name, requestFile)
            val response = profileApi.uploadAvatar(body).execute()
            if (response.isSuccessful) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Error uploading avatar: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun deleteAvatar(): Result<ApiMessageResponse> = withContext(Dispatchers.IO) {
        try {
            ensureToken()
            val response = profileApi.deleteAvatar().execute()
            if (response.isSuccessful) {
                // Clear local cache immediately so UI doesn't show old avatar URL
                profileDao.getProfile()?.let {
                    profileDao.insertProfile(it.copy(avatar = null, avatarUrl = null))
                }
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Error deleting avatar: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private suspend fun ensureToken() {
        tokenPreferences.token.firstOrNull()?.let {
            com.example.biblio.di.AppModule.setToken(it)
        }
    }
}
