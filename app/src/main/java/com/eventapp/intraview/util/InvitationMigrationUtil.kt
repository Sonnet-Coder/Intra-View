package com.eventapp.intraview.util

import android.util.Log
import com.eventapp.intraview.data.model.Invitation
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Utility class to migrate existing invitations and populate missing user information.
 * This is needed for invitations created before the userName, userEmail, and userPhotoUrl
 * fields were added to the Invitation model.
 */
@Singleton
class InvitationMigrationUtil @Inject constructor(
    private val firestore: FirebaseFirestore
) {
    companion object {
        private const val TAG = "InvitationMigration"
    }

    /**
     * Migrates all existing invitations to populate userName, userEmail, and userPhotoUrl
     * from the users collection.
     * 
     * This should be called once after deploying the update that adds these fields.
     */
    suspend fun migrateAllInvitations(): MigrationResult {
        return try {
            Log.d(TAG, "Starting invitation migration...")
            
            val invitationsSnapshot = firestore.collection("invitations")
                .get()
                .await()
            
            val totalInvitations = invitationsSnapshot.documents.size
            var migratedCount = 0
            var skippedCount = 0
            var errorCount = 0
            
            Log.d(TAG, "Found $totalInvitations invitations to check")
            
            invitationsSnapshot.documents.forEach { doc ->
                try {
                    val invitation = doc.toObject(Invitation::class.java)
                    
                    if (invitation == null) {
                        Log.w(TAG, "Could not parse invitation: ${doc.id}")
                        errorCount++
                        return@forEach
                    }
                    
                    // Check if invitation already has user info
                    if (invitation.userName.isNotEmpty() && 
                        invitation.userEmail.isNotEmpty()) {
                        Log.d(TAG, "Skipping invitation ${doc.id} - already has user info")
                        skippedCount++
                        return@forEach
                    }
                    
                    // Fetch user data
                    val userDoc = firestore.collection("users")
                        .document(invitation.userId)
                        .get()
                        .await()
                    
                    if (!userDoc.exists()) {
                        Log.w(TAG, "User not found for invitation ${doc.id}: userId=${invitation.userId}")
                        errorCount++
                        return@forEach
                    }
                    
                    val userName = userDoc.getString("displayName") ?: ""
                    val userEmail = userDoc.getString("email") ?: ""
                    val userPhotoUrl = userDoc.getString("photoUrl") ?: ""
                    
                    // Update invitation
                    doc.reference.update(mapOf(
                        "userName" to userName,
                        "userEmail" to userEmail,
                        "userPhotoUrl" to userPhotoUrl
                    )).await()
                    
                    Log.d(TAG, "Migrated invitation ${doc.id} for user $userName")
                    migratedCount++
                    
                } catch (e: Exception) {
                    Log.e(TAG, "Error migrating invitation ${doc.id}: ${e.message}", e)
                    errorCount++
                }
            }
            
            val result = MigrationResult(
                totalInvitations = totalInvitations,
                migratedCount = migratedCount,
                skippedCount = skippedCount,
                errorCount = errorCount,
                success = errorCount == 0
            )
            
            Log.d(TAG, "Migration complete: $result")
            result
            
        } catch (e: Exception) {
            Log.e(TAG, "Fatal error during migration: ${e.message}", e)
            MigrationResult(
                totalInvitations = 0,
                migratedCount = 0,
                skippedCount = 0,
                errorCount = 1,
                success = false,
                errorMessage = e.message
            )
        }
    }

    /**
     * Migrates invitations for a specific event.
     * Useful for fixing invitations for a single event.
     */
    suspend fun migrateEventInvitations(eventId: String): MigrationResult {
        return try {
            Log.d(TAG, "Starting migration for event: $eventId")
            
            val invitationsSnapshot = firestore.collection("invitations")
                .whereEqualTo("eventId", eventId)
                .get()
                .await()
            
            val totalInvitations = invitationsSnapshot.documents.size
            var migratedCount = 0
            var skippedCount = 0
            var errorCount = 0
            
            Log.d(TAG, "Found $totalInvitations invitations for event $eventId")
            
            invitationsSnapshot.documents.forEach { doc ->
                try {
                    val invitation = doc.toObject(Invitation::class.java)
                    
                    if (invitation == null) {
                        errorCount++
                        return@forEach
                    }
                    
                    if (invitation.userName.isNotEmpty() && invitation.userEmail.isNotEmpty()) {
                        skippedCount++
                        return@forEach
                    }
                    
                    val userDoc = firestore.collection("users")
                        .document(invitation.userId)
                        .get()
                        .await()
                    
                    if (!userDoc.exists()) {
                        errorCount++
                        return@forEach
                    }
                    
                    doc.reference.update(mapOf(
                        "userName" to (userDoc.getString("displayName") ?: ""),
                        "userEmail" to (userDoc.getString("email") ?: ""),
                        "userPhotoUrl" to (userDoc.getString("photoUrl") ?: "")
                    )).await()
                    
                    migratedCount++
                    
                } catch (e: Exception) {
                    Log.e(TAG, "Error migrating invitation ${doc.id}: ${e.message}", e)
                    errorCount++
                }
            }
            
            MigrationResult(
                totalInvitations = totalInvitations,
                migratedCount = migratedCount,
                skippedCount = skippedCount,
                errorCount = errorCount,
                success = errorCount == 0
            )
            
        } catch (e: Exception) {
            Log.e(TAG, "Error migrating event invitations: ${e.message}", e)
            MigrationResult(
                totalInvitations = 0,
                migratedCount = 0,
                skippedCount = 0,
                errorCount = 1,
                success = false,
                errorMessage = e.message
            )
        }
    }
}

data class MigrationResult(
    val totalInvitations: Int,
    val migratedCount: Int,
    val skippedCount: Int,
    val errorCount: Int,
    val success: Boolean,
    val errorMessage: String? = null
) {
    override fun toString(): String {
        return """
            Migration Result:
            - Total Invitations: $totalInvitations
            - Migrated: $migratedCount
            - Skipped: $skippedCount
            - Errors: $errorCount
            - Success: $success
            ${errorMessage?.let { "- Error: $it" } ?: ""}
        """.trimIndent()
    }
}
