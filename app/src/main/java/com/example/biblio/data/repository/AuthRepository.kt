package com.example.biblio.data.repository

import android.content.Context
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.example.biblio.data.model.User
import com.example.biblio.data.preferences.TokenPreferences
import com.example.biblio.data.remote.apis.AuthApi
import com.example.biblio.data.remote.dto.FirebaseLogin
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.UserProfileChangeRequest
import kotlinx.coroutines.tasks.await
import coil.imageLoader
import coil.annotation.ExperimentalCoilApi
import com.example.biblio.data.local.BiblioDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class AuthRepository(
    private val context: Context,
    private val authApi: AuthApi,
    private val tokenPreferences: TokenPreferences,
    private val database: BiblioDatabase
) {
    private val auth = FirebaseAuth.getInstance()
    val currentUser: FirebaseUser? get() = auth.currentUser

    suspend fun login(email: String, password: String): Result<User> {
        return try {
            val result = auth.signInWithEmailAndPassword(email, password).await()
            val firebaseUser = result.user ?: return Result.failure(Exception("Login failed"))
            exchangeFirebaseCredentials(firebaseUser, firebaseUser.displayName)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun googleLogin(idToken: String): Result<User> {
        return try {
            val credential = GoogleAuthProvider.getCredential(idToken, null)
            val result = auth.signInWithCredential(credential).await()
            val firebaseUser = result.user ?: return Result.failure(Exception("Google login failed"))
            exchangeFirebaseCredentials(firebaseUser, firebaseUser.displayName)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Ambil Firebase ID token → kirim ke Laravel → simpan Sanctum token
    private suspend fun exchangeFirebaseCredentials(firebaseUser: FirebaseUser, displayName: String?): Result<User> {
        val firebaseToken = firebaseUser.getIdToken(false).await().token
            ?: return Result.failure(Exception("Gagal ambil Firebase token"))

        val response = authApi.authFirebase(FirebaseLogin(token = firebaseToken, displayName = displayName))

        return if (response.isSuccessful) {
            val sanctumToken = response.body()?.token
                ?: return Result.failure(Exception("Token kosong"))
            tokenPreferences.saveToken(sanctumToken)
            com.example.biblio.di.AppModule.setToken(sanctumToken)
            val user = User(
                firebaseUser.uid,
                firebaseUser.email ?: "",
                firebaseUser.displayName,
                firebaseUser.photoUrl?.toString()
            )
            Result.success(user)
        } else {
            Result.failure(Exception("API error: ${response.code()}"))
        }
    }

    suspend fun register(email: String, password: String, displayName: String): Result<User> {
        return try {
            val result = auth.createUserWithEmailAndPassword(email, password).await()
            val firebaseUser = result.user ?: throw Exception("Register failed")
            firebaseUser.updateProfile(
                UserProfileChangeRequest.Builder()
                    .setDisplayName(displayName)
                    .build()
            ).await()
            exchangeFirebaseCredentials(firebaseUser, displayName = displayName)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    @OptIn(ExperimentalCoilApi::class)
    suspend fun logout() {
        withContext(Dispatchers.IO) {
            auth.signOut()
            tokenPreferences.clearToken()
            try {
                authApi.authLogout()
            } catch (_: Exception) {
                // Ignore logout error if user already unauthenticated
            }
            context.imageLoader.diskCache?.clear()
            context.imageLoader.memoryCache?.clear()

            // Hapus file buku (.epub dan .pdf) dari cache
            context.cacheDir.listFiles()?.forEach { file ->
                if (file.name.endsWith(".epub") || file.name.endsWith(".pdf")) {
                    file.delete()
                }
            }

            // Hapus metadata buku
            context.deleteFile("buku.json")

            database.clearAllTables()
        }
    }
}