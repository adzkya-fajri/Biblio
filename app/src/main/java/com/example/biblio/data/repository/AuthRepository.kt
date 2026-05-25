package com.example.biblio.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.example.biblio.data.model.User
import com.example.biblio.data.preferences.TokenPreferences
import com.example.biblio.data.remote.apis.AuthApi
import com.example.biblio.models.FirebaseLogin
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.auth.userProfileChangeRequest
import kotlinx.coroutines.tasks.await

class AuthRepository(
    private val authApi: AuthApi,
    private val tokenPreferences: TokenPreferences
) {
    private val auth = FirebaseAuth.getInstance()
    val currentUser: FirebaseUser? get() = auth.currentUser

    suspend fun login(email: String, password: String): Result<User> {
        return try {
            val result = auth.signInWithEmailAndPassword(email, password).await()
            val firebaseUser = result.user ?: return Result.failure(Exception("Login failed"))
            exchangeFirebaseToken(firebaseUser)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun googleLogin(idToken: String): Result<User> {
        return try {
            val credential = GoogleAuthProvider.getCredential(idToken, null)
            val result = auth.signInWithCredential(credential).await()
            val firebaseUser = result.user ?: return Result.failure(Exception("Google login failed"))
            exchangeFirebaseToken(firebaseUser)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Ambil Firebase ID token → kirim ke Laravel → simpan Sanctum token
    private suspend fun exchangeFirebaseToken(firebaseUser: FirebaseUser): Result<User> {
        val firebaseToken = firebaseUser.getIdToken(false).await().token
            ?: return Result.failure(Exception("Gagal ambil Firebase token"))

        val response = authApi.authFirebase(FirebaseLogin(token = firebaseToken))

        return if (response.isSuccessful) {
            val sanctumToken = response.body()?.token
                ?: return Result.failure(Exception("Token kosong"))
            tokenPreferences.saveToken(sanctumToken)
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
            exchangeFirebaseToken(firebaseUser)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun logout() {
        auth.signOut()
        tokenPreferences.clearToken()
        authApi.authLogout()
    }
}