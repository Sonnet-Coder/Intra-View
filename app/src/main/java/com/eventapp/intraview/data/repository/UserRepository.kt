package com.eventapp.intraview.data.repository

import com.eventapp.intraview.data.model.User
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserRepository @Inject constructor(
    private val firestore: FirebaseFirestore
) {
    suspend fun getUser(userId: String): User? {
        return try {
            val doc = firestore.collection("users")
                .document(userId)
                .get()
                .await()
            doc.toObject(User::class.java)
        } catch (e: Exception) {
            null
        }
    }

    suspend fun updateUser(user: User) {
        firestore.collection("users")
            .document(user.userId)
            .set(user)
            .await()
    }
}
