//package com.example.biblio.data.repository
//
//import android.content.Context
//import com.example.biblio.data.model.ReadingProgress
//import com.google.firebase.Firebase
//import kotlinx.coroutines.flow.Flow
//import kotlinx.coroutines.flow.callbackFlow
//
//class ReadingProgressRepository(private val context: Context) {
//    private val db = Firebase.firestore
//
//    // Get last read book untuk user
//    fun getLastReadBook(userId: String): Flow<ReadingProgress?> = callbackFlow {
//        val listener = db.collection("users")
//            .document(userId)
//            .collection("reading_progress")
//            .orderBy("lastReadAt", Query.Direction.DESCENDING)
//            .limit(1)
//            .addSnapshotListener { snapshot, error ->
//                if (error != null) {
//                    trySend(null)
//                    return@addSnapshotListener
//                }
//                val progress = snapshot?.documents?.firstOrNull()?.toObject(ReadingProgress::class.java)
//                trySend(progress)
//            }
//
//        awaitClose { listener.remove() }
//    }
//
//    // Get reading history (semua buku yang pernah dibaca)
//    fun getReadingHistory(userId: String): Flow<List<ReadingProgress>> = callbackFlow {
//        val listener = db.collection("users")
//            .document(userId)
//            .collection("reading_progress")
//            .orderBy("lastReadAt", Query.Direction.DESCENDING)
//            .addSnapshotListener { snapshot, error ->
//                if (error != null) {
//                    trySend(emptyList())
//                    return@addSnapshotListener
//                }
//                val history = snapshot?.documents?.mapNotNull {
//                    it.toObject(ReadingProgress::class.java)
//                } ?: emptyList()
//                trySend(history)
//            }
//
//        awaitClose { listener.remove() }
//    }
//
//    // Update progress
//    suspend fun updateProgress(progress: ReadingProgress) {
//        db.collection("users")
//            .document(progress.userId)
//            .collection("reading_progress")
//            .document(progress.bookId)
//            .set(progress)
//            .await()
//    }
//
//    // Delete progress (stop reading)
//    suspend fun deleteProgress(userId: String, bookId: String) {
//        db.collection("users")
//            .document(userId)
//            .collection("reading_progress")
//            .document(bookId)
//            .delete()
//            .await()
//    }
//}