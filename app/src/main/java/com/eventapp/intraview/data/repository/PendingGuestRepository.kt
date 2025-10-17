package com.eventapp.intraview.data.repository

import com.eventapp.intraview.data.model.PendingGuest
import com.eventapp.intraview.util.Constants
import com.eventapp.intraview.util.Result
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PendingGuestRepository @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth
) {
    private val currentUserId: String
        get() = auth.currentUser?.uid ?: throw IllegalStateException("User not authenticated")
    
    suspend fun createPendingGuest(eventId: String, userId: String): Result<PendingGuest> {
        return try {
            // Fetch user information
            val userDoc = firestore.collection(Constants.COLLECTION_USERS)
                .document(userId)
                .get()
                .await()
            
            val userName = userDoc.getString("displayName") ?: ""
            val userEmail = userDoc.getString("email") ?: ""
            val userPhotoUrl = userDoc.getString("photoUrl") ?: ""
            
            val pendingGuestRef = firestore.collection(Constants.COLLECTION_PENDING_GUESTS).document()
            
            val pendingGuest = PendingGuest(
                pendingGuestId = pendingGuestRef.id,
                eventId = eventId,
                userId = userId,
                userName = userName,
                userEmail = userEmail,
                userPhotoUrl = userPhotoUrl,
                requestedAt = Timestamp.now()
            )
            
            pendingGuestRef.set(pendingGuest).await()
            Result.Success(pendingGuest)
        } catch (e: Exception) {
            Result.Error(e.message ?: "Failed to create pending guest request")
        }
    }
    
    suspend fun getPendingGuest(eventId: String, userId: String): PendingGuest? {
        return try {
            val result = firestore.collection(Constants.COLLECTION_PENDING_GUESTS)
                .whereEqualTo("eventId", eventId)
                .whereEqualTo("userId", userId)
                .limit(1)
                .get()
                .await()
            
            result.documents.firstOrNull()?.toObject(PendingGuest::class.java)
        } catch (e: Exception) {
            null
        }
    }
    
    fun observePendingGuests(eventId: String): Flow<List<PendingGuest>> = callbackFlow {
        val listener = firestore.collection(Constants.COLLECTION_PENDING_GUESTS)
            .whereEqualTo("eventId", eventId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                val pendingGuests = snapshot?.documents?.mapNotNull {
                    it.toObject(PendingGuest::class.java)
                } ?: emptyList()
                trySend(pendingGuests)
            }
        awaitClose { listener.remove() }
    }
    
    suspend fun deletePendingGuest(eventId: String, userId: String): Result<Unit> {
        return try {
            val result = firestore.collection(Constants.COLLECTION_PENDING_GUESTS)
                .whereEqualTo("eventId", eventId)
                .whereEqualTo("userId", userId)
                .get()
                .await()
            
            val batch = firestore.batch()
            result.documents.forEach { doc ->
                batch.delete(doc.reference)
            }
            batch.commit().await()
            
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e.message ?: "Failed to delete pending guest")
        }
    }
}
