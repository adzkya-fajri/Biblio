package com.example.biblio.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.example.biblio.data.model.User
import kotlinx.coroutines.tasks.await

class AuthRepository {
    private val auth = FirebaseAuth.getInstance()

    val currentUser: FirebaseUser? get() = auth.currentUser

    suspend fun login(email: String, password: String): Result<User> {
        return try {
            val result = auth.signInWithEmailAndPassword(email, password).await()
            val user = result.user?.let {
                User(it.uid, it.email ?: "", it.displayName, it.photoUrl?.toString())
            }
            if (user != null) Result.success(user) else Result.failure(Exception("Login failed"))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun register(email: String, password: String): Result<User> {
        return try {
            val result = auth.createUserWithEmailAndPassword(email, password).await()
            val user = result.user?.let {
                User(it.uid, it.email ?: "", it.displayName, it.photoUrl?.toString())
            }
            if (user != null) Result.success(user) else Result.failure(Exception("Register failed"))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun logout() = auth.signOut()
}