package com.eventapp.intraview.data.repository

import android.content.Context
import android.net.Uri
import com.eventapp.intraview.data.model.Photo
import com.eventapp.intraview.data.model.User
import com.eventapp.intraview.util.Constants
import com.eventapp.intraview.util.ImageCompressor
import com.eventapp.intraview.util.Result
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PhotoRepository @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val storage: FirebaseStorage,
    private val auth: FirebaseAuth,
    private val context: Context
) {
    private val currentUserId: String
        get() = auth.currentUser?.uid ?: throw IllegalStateException("User not authenticated")
    
    suspend fun uploadPhoto(
        eventId: String,
        imageUri: Uri,
        currentUser: User
    ): Result<Photo> {
        return try {
            // Compress image
            val compressedFile = ImageCompressor.compressImage(context, imageUri)
            
            // Upload to Storage
            val photoId = UUID.randomUUID().toString()
            val storageRef = storage.reference
                .child("${Constants.STORAGE_EVENTS}/$eventId/$photoId.jpg")
            
            val uploadTask = storageRef.putFile(Uri.fromFile(compressedFile)).await()
            val downloadUrl = uploadTask.storage.downloadUrl.await()
            
            // Create Firestore document
            val photo = Photo(
                photoId = photoId,
                eventId = eventId,
                userId = currentUserId,
                userName = currentUser.displayName,
                userPhotoUrl = currentUser.photoUrl,
                imageUrl = downloadUrl.toString(),
                thumbnailUrl = downloadUrl.toString(), // Same URL for simplicity
                uploadedAt = Timestamp.now()
            )
            
            firestore.collection(Constants.COLLECTION_PHOTOS)
                .document(photoId)
                .set(photo)
                .await()
            
            // Clean up temp file
            compressedFile.delete()
            
            Result.Success(photo)
        } catch (e: Exception) {
            Result.Error(e.message ?: "Failed to upload photo")
        }
    }
    
    suspend fun deletePhoto(photoId: String, eventId: String): Result<Unit> {
        return try {
            // Delete from Firestore
            firestore.collection(Constants.COLLECTION_PHOTOS)
                .document(photoId)
                .delete()
                .await()
            
            // Delete from Storage
            val storageRef = storage.reference
                .child("${Constants.STORAGE_EVENTS}/$eventId/$photoId.jpg")
            storageRef.delete().await()
            
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e.message ?: "Failed to delete photo")
        }
    }
    
    fun observeEventPhotos(eventId: String): Flow<List<Photo>> = callbackFlow {
        val listener = firestore.collection(Constants.COLLECTION_PHOTOS)
            .whereEqualTo("eventId", eventId)
            .orderBy("uploadedAt", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                val photos = snapshot?.documents?.mapNotNull {
                    it.toObject(Photo::class.java)
                } ?: emptyList()
                trySend(photos)
            }
        awaitClose { listener.remove() }
    }
    
    suspend fun getEventPhotos(eventId: String, limit: Int = Constants.PHOTOS_PAGE_SIZE): List<Photo> {
        return try {
            val result = firestore.collection(Constants.COLLECTION_PHOTOS)
                .whereEqualTo("eventId", eventId)
                .orderBy("uploadedAt", Query.Direction.DESCENDING)
                .limit(limit.toLong())
                .get()
                .await()
            
            result.documents.mapNotNull { it.toObject(Photo::class.java) }
        } catch (e: Exception) {
            emptyList()
        }
    }
    
    suspend fun getPhoto(photoId: String): Photo? {
        return try {
            firestore.collection(Constants.COLLECTION_PHOTOS)
                .document(photoId)
                .get()
                .await()
                .toObject(Photo::class.java)
        } catch (e: Exception) {
            null
        }
    }
}


