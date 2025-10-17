package com.eventapp.intraview.data.repository

import com.eventapp.intraview.data.model.Event
import com.eventapp.intraview.util.Constants
import com.eventapp.intraview.util.InviteCodeGenerator
import com.eventapp.intraview.util.Result
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class EventRepository @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth
) {
    private val currentUserId: String
        get() = auth.currentUser?.uid ?: throw IllegalStateException("User not authenticated")
    
    suspend fun createEvent(
        name: String,
        description: String,
        date: Timestamp,
        location: String,
        latitude: Double?,
        longitude: Double?,
        durationMinutes: Int,
        backgroundImageUrl: String,
        musicPlaylistUrl: String?,
        sharedAlbumUrl: String?,
        maxGuests: Int?,
        isPublic: Boolean
    ): Result<Event> {
        return try {
            val inviteCode = InviteCodeGenerator.generate()
            val eventRef = firestore.collection(Constants.COLLECTION_EVENTS).document()
            
            val event = Event(
                eventId = eventRef.id,
                hostId = currentUserId,
                name = name,
                description = description,
                date = date,
                location = location,
                latitude = latitude,
                longitude = longitude,
                durationMinutes = durationMinutes,
                backgroundImageUrl = backgroundImageUrl,
                inviteCode = inviteCode,
                guestIds = emptyList(),
                photoCount = 0,
                playlistUrls = emptyList(),
                musicPlaylistUrl = musicPlaylistUrl,
                sharedAlbumUrl = sharedAlbumUrl,
                maxGuests = maxGuests,
                isPublic = isPublic,
                createdAt = Timestamp.now(),
                updatedAt = Timestamp.now()
            )
            
            eventRef.set(event).await()
            Result.Success(event)
        } catch (e: Exception) {
            Result.Error(e.message ?: "Failed to create event")
        }
    }
    
    suspend fun updateEvent(eventId: String, updates: Map<String, Any>): Result<Unit> {
        return try {
            val updatesWithTimestamp = updates.toMutableMap().apply {
                put("updatedAt", FieldValue.serverTimestamp())
            }
            firestore.collection(Constants.COLLECTION_EVENTS)
                .document(eventId)
                .update(updatesWithTimestamp)
                .await()
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e.message ?: "Failed to update event")
        }
    }
    
    suspend fun deleteEvent(eventId: String): Result<Unit> {
        return try {
            firestore.collection(Constants.COLLECTION_EVENTS)
                .document(eventId)
                .delete()
                .await()
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e.message ?: "Failed to delete event")
        }
    }
    
    suspend fun getEvent(eventId: String): Event? {
        return try {
            firestore.collection(Constants.COLLECTION_EVENTS)
                .document(eventId)
                .get()
                .await()
                .toObject(Event::class.java)
        } catch (e: Exception) {
            null
        }
    }
    
    fun observeEvent(eventId: String): Flow<Event?> = callbackFlow {
        val listener = firestore.collection(Constants.COLLECTION_EVENTS)
            .document(eventId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                trySend(snapshot?.toObject(Event::class.java))
            }
        awaitClose { listener.remove() }
    }
    
    fun observeMyEvents(): Flow<List<Event>> = callbackFlow {
        val listener = firestore.collection(Constants.COLLECTION_EVENTS)
            .whereEqualTo("hostId", currentUserId)
            .orderBy("date", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                val events = snapshot?.documents?.mapNotNull { it.toObject(Event::class.java) } ?: emptyList()
                trySend(events)
            }
        awaitClose { listener.remove() }
    }
    
    fun observeInvitedEvents(): Flow<List<Event>> = callbackFlow {
        val listener = firestore.collection(Constants.COLLECTION_EVENTS)
            .whereArrayContains("guestIds", currentUserId)
            .orderBy("date", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                val events = snapshot?.documents?.mapNotNull { it.toObject(Event::class.java) } ?: emptyList()
                trySend(events)
            }
        awaitClose { listener.remove() }
    }
    
    suspend fun findEventByInviteCode(inviteCode: String): Event? {
        return try {
            val result = firestore.collection(Constants.COLLECTION_EVENTS)
                .whereEqualTo("inviteCode", inviteCode.uppercase())
                .limit(1)
                .get()
                .await()
            
            result.documents.firstOrNull()?.toObject(Event::class.java)
        } catch (e: Exception) {
            null
        }
    }
    
    suspend fun addGuestToEvent(eventId: String, guestId: String): Result<Unit> {
        return try {
            firestore.collection(Constants.COLLECTION_EVENTS)
                .document(eventId)
                .update("guestIds", FieldValue.arrayUnion(guestId))
                .await()
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e.message ?: "Failed to add guest")
        }
    }
    
    suspend fun removeGuestFromEvent(eventId: String, guestId: String): Result<Unit> {
        return try {
            firestore.collection(Constants.COLLECTION_EVENTS)
                .document(eventId)
                .update("guestIds", FieldValue.arrayRemove(guestId))
                .await()
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e.message ?: "Failed to remove guest")
        }
    }
    
    suspend fun addPendingGuest(eventId: String, guestId: String): Result<Unit> {
        return try {
            firestore.collection(Constants.COLLECTION_EVENTS)
                .document(eventId)
                .update("pendingGuestIds", FieldValue.arrayUnion(guestId))
                .await()
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e.message ?: "Failed to add pending guest")
        }
    }
    
    suspend fun removePendingGuest(eventId: String, guestId: String): Result<Unit> {
        return try {
            firestore.collection(Constants.COLLECTION_EVENTS)
                .document(eventId)
                .update("pendingGuestIds", FieldValue.arrayRemove(guestId))
                .await()
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e.message ?: "Failed to remove pending guest")
        }
    }
    
    suspend fun approveGuest(eventId: String, guestId: String): Result<Unit> {
        return try {
            // Use a batch write to ensure atomicity
            val batch = firestore.batch()
            val eventRef = firestore.collection(Constants.COLLECTION_EVENTS).document(eventId)
            
            // Remove from pending and add to approved guests
            batch.update(eventRef, "pendingGuestIds", FieldValue.arrayRemove(guestId))
            batch.update(eventRef, "guestIds", FieldValue.arrayUnion(guestId))
            
            batch.commit().await()
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e.message ?: "Failed to approve guest")
        }
    }
    
    suspend fun rejectGuest(eventId: String, guestId: String): Result<Unit> {
        return try {
            firestore.collection(Constants.COLLECTION_EVENTS)
                .document(eventId)
                .update("pendingGuestIds", FieldValue.arrayRemove(guestId))
                .await()
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e.message ?: "Failed to reject guest")
        }
    }
    
    suspend fun addPlaylistUrl(eventId: String, playlistUrl: String): Result<Unit> {
        return try {
            firestore.collection(Constants.COLLECTION_EVENTS)
                .document(eventId)
                .update("playlistUrls", FieldValue.arrayUnion(playlistUrl))
                .await()
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e.message ?: "Failed to add playlist")
        }
    }
    
    suspend fun removePlaylistUrl(eventId: String, playlistUrl: String): Result<Unit> {
        return try {
            firestore.collection(Constants.COLLECTION_EVENTS)
                .document(eventId)
                .update("playlistUrls", FieldValue.arrayRemove(playlistUrl))
                .await()
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e.message ?: "Failed to remove playlist")
        }
    }
    
    suspend fun incrementPhotoCount(eventId: String): Result<Unit> {
        return try {
            firestore.collection(Constants.COLLECTION_EVENTS)
                .document(eventId)
                .update("photoCount", FieldValue.increment(1))
                .await()
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e.message ?: "Failed to update photo count")
        }
    }
}


