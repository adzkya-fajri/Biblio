package com.example.biblio.data.repository

import android.content.Context
import android.util.Log
import com.example.biblio.data.model.User
import com.example.biblio.data.remote.ApiClient
import com.example.biblio.data.remote.TokenManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.UserProfileChangeRequest
import kotlinx.coroutines.tasks.await

class AuthRepository(context: Context) {
    private val auth = FirebaseAuth.getInstance()
    private val tokenManager = TokenManager(context)
    private val api = ApiClient.api

    val currentUser: FirebaseUser? get() = auth.currentUser

    suspend fun ensureTokenLoaded() {
        if (ApiClient.sanctumToken == null) {
            ApiClient.sanctumToken = tokenManager.getToken()
        }
    }

    /** Tukar Firebase ID token → Sanctum token (Laravel API). */
    suspend fun syncSanctumToken(): Result<Unit> {
        ensureTokenLoaded()
        val firebaseUser = auth.currentUser
            ?: return Result.failure(Exception("Belum login Firebase"))

        return try {
            val idToken = firebaseUser.getIdToken(true).await().token
                ?: return Result.failure(Exception("Token Firebase kosong"))

            val response = api.firebaseLogin(mapOf("token" to idToken))
            val token = response.token
                ?: return Result.failure(Exception("Token Sanctum tidak diterima"))

            tokenManager.saveToken(token)
            ApiClient.sanctumToken = token
            Log.d(TAG, "Sanctum token disimpan")
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "Gagal sync Sanctum: ${e.message}", e)
            Result.failure(e)
        }
    }

    suspend fun login(email: String, password: String): Result<User> {
        return try {
            val result = auth.signInWithEmailAndPassword(email, password).await()
            val user = result.user?.toUser()
                ?: return Result.failure(Exception("Login failed"))
            syncSanctumToken()
            Result.success(user)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun googleLogin(idToken: String): Result<User> {
        return try {
            val credential = GoogleAuthProvider.getCredential(idToken, null)
            val result = auth.signInWithCredential(credential).await()
            val user = result.user?.toUser()
                ?: return Result.failure(Exception("Google login failed"))
            syncSanctumToken()
            Result.success(user)
        } catch (e: Exception) {
            Result.failure(e)
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

            val user = User(
                firebaseUser.uid,
                firebaseUser.email ?: "",
                displayName,
                firebaseUser.photoUrl?.toString()
            )
            syncSanctumToken()
            Result.success(user)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun logout() {
        auth.signOut()
        kotlinx.coroutines.runBlocking {
            tokenManager.clearToken()
        }
        ApiClient.sanctumToken = null
    }

    private fun FirebaseUser.toUser() = User(
        uid,
        email ?: "",
        displayName,
        photoUrl?.toString()
    )

    companion object {
        private const val TAG = "AuthRepository"
    }
}
