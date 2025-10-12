package com.eventapp.intraview.data.repository

import com.eventapp.intraview.data.model.Invitation
import com.eventapp.intraview.data.model.InvitationStatus
import com.eventapp.intraview.util.Constants
import com.eventapp.intraview.util.InviteCodeGenerator
import com.eventapp.intraview.util.Result
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class InvitationRepository @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth
) {
    private val currentUserId: String
        get() = auth.currentUser?.uid ?: throw IllegalStateException("User not authenticated")
    
    suspend fun createInvitation(eventId: String, userId: String): Result<Invitation> {
        return try {
            val invitationRef = firestore.collection(Constants.COLLECTION_INVITATIONS).document()
            
            val invitation = Invitation(
                invitationId = invitationRef.id,
                eventId = eventId,
                userId = userId,
                status = InvitationStatus.PENDING,
                qrToken = InviteCodeGenerator.generateQRToken(),
                checkedIn = false,
                checkedInAt = null,
                createdAt = Timestamp.now()
            )
            
            invitationRef.set(invitation).await()
            Result.Success(invitation)
        } catch (e: Exception) {
            Result.Error(e.message ?: "Failed to create invitation")
        }
    }
    
    suspend fun updateInvitationStatus(
        invitationId: String,
        status: InvitationStatus
    ): Result<Unit> {
        return try {
            firestore.collection(Constants.COLLECTION_INVITATIONS)
                .document(invitationId)
                .update("status", status.name)
                .await()
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e.message ?: "Failed to update invitation")
        }
    }
    
    suspend fun acceptInvitation(invitationId: String): Result<Unit> {
        return updateInvitationStatus(invitationId, InvitationStatus.ACCEPTED)
    }
    
    suspend fun declineInvitation(invitationId: String): Result<Unit> {
        return updateInvitationStatus(invitationId, InvitationStatus.DECLINED)
    }
    
    suspend fun getInvitation(invitationId: String): Invitation? {
        return try {
            firestore.collection(Constants.COLLECTION_INVITATIONS)
                .document(invitationId)
                .get()
                .await()
                .toObject(Invitation::class.java)
        } catch (e: Exception) {
            null
        }
    }
    
    suspend fun getInvitationForUserAndEvent(userId: String, eventId: String): Invitation? {
        return try {
            val result = firestore.collection(Constants.COLLECTION_INVITATIONS)
                .whereEqualTo("userId", userId)
                .whereEqualTo("eventId", eventId)
                .limit(1)
                .get()
                .await()
            
            result.documents.firstOrNull()?.toObject(Invitation::class.java)
        } catch (e: Exception) {
            null
        }
    }
    
    fun observeEventInvitations(eventId: String): Flow<List<Invitation>> = callbackFlow {
        val listener = firestore.collection(Constants.COLLECTION_INVITATIONS)
            .whereEqualTo("eventId", eventId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                val invitations = snapshot?.documents?.mapNotNull {
                    it.toObject(Invitation::class.java)
                } ?: emptyList()
                trySend(invitations)
            }
        awaitClose { listener.remove() }
    }
    
    suspend fun checkInGuest(qrToken: String, eventId: String): Result<Invitation> {
        return try {
            // Find invitation by QR token
            val result = firestore.collection(Constants.COLLECTION_INVITATIONS)
                .whereEqualTo("qrToken", qrToken)
                .whereEqualTo("eventId", eventId)
                .limit(1)
                .get()
                .await()
            
            val invitation = result.documents.firstOrNull()?.toObject(Invitation::class.java)
                ?: return Result.Error("Invitation not found")
            
            if (invitation.checkedIn) {
                return Result.Error("Guest already checked in")
            }
            
            // Update check-in status
            firestore.collection(Constants.COLLECTION_INVITATIONS)
                .document(invitation.invitationId)
                .update(
                    mapOf(
                        "checkedIn" to true,
                        "checkedInAt" to FieldValue.serverTimestamp()
                    )
                )
                .await()
            
            Result.Success(invitation.copy(checkedIn = true, checkedInAt = Timestamp.now()))
        } catch (e: Exception) {
            Result.Error(e.message ?: "Failed to check in guest")
        }
    }
    
    suspend fun getMyInvitationForEvent(eventId: String): Invitation? {
        return getInvitationForUserAndEvent(currentUserId, eventId)
    }
}


