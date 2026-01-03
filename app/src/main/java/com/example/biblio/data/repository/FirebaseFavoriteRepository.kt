package com.example.biblio.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.tasks.await

class FirebaseFavoriteRepository(
    private val auth: FirebaseAuth,
    private val firestore: FirebaseFirestore
) {

    private val _favoriteIds = MutableStateFlow<Set<String>>(emptySet())
    val favoriteIds: StateFlow<Set<String>> = _favoriteIds.asStateFlow()

    private val userId: String?
        get() = auth.currentUser?.uid

    private val favoritesDoc
        get() = firestore
            .collection("user_favorites")
            .document(userId ?: "anonymous")

    init {
        auth.addAuthStateListener { auth ->
            if (auth.currentUser != null) {
                observeFavorites()
            } else {
                _favoriteIds.value = emptySet()
            }
        }
    }

    private fun observeFavorites() {
        val uid = userId ?: return

        favoritesDoc.addSnapshotListener { snapshot, error ->
            if (error != null || snapshot == null || !snapshot.exists()) {
                _favoriteIds.value = emptySet()
                return@addSnapshotListener
            }

            val ids = snapshot.get("bookIds") as? List<String> ?: emptyList()
            _favoriteIds.value = ids.toSet()
        }
    }

    suspend fun toggleFavorite(bookId: String) {
        val uid = userId ?: return

        firestore.runTransaction { transaction ->
            val snapshot = transaction.get(favoritesDoc)

            val current =
                snapshot.get("bookIds") as? List<String> ?: emptyList()

            val updated =
                if (current.contains(bookId)) {
                    current - bookId
                } else {
                    current + bookId
                }

            transaction.set(
                favoritesDoc,
                mapOf("bookIds" to updated)
            )
        }.await()
    }

    suspend fun clearAllFavorites() {
        userId ?: return

        favoritesDoc.set(
            mapOf("bookIds" to emptyList<String>())
        ).await()
    }

    fun isFavorite(bookId: String): Boolean {
        return _favoriteIds.value.contains(bookId)
    }
}
