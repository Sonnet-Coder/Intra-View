package com.eventapp.intraview.data.model

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId

data class Invitation(
    @DocumentId
    val invitationId: String = "",
    val eventId: String = "",
    val userId: String = "",
    val status: InvitationStatus = InvitationStatus.PENDING,
    val qrToken: String = "",
    val checkedIn: Boolean = false,
    val checkedInAt: Timestamp? = null,
    val createdAt: Timestamp = Timestamp.now()
)

enum class InvitationStatus {
    PENDING,
    ACCEPTED,
    DECLINED
}


