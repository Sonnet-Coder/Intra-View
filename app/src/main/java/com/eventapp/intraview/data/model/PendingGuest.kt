package com.eventapp.intraview.data.model

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId

/**
 * Represents a user who has requested to join an event and is awaiting host approval
 */
data class PendingGuest(
    @DocumentId
    val pendingGuestId: String = "",
    val eventId: String = "",
    val userId: String = "",
    val userName: String = "",
    val userEmail: String = "",
    val userPhotoUrl: String = "",
    val requestedAt: Timestamp = Timestamp.now()
)
