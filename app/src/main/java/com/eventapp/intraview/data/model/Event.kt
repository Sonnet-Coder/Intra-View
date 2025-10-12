package com.eventapp.intraview.data.model

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId

data class Event(
    @DocumentId
    val eventId: String = "",
    val hostId: String = "",
    val name: String = "",
    val description: String = "",
    val date: Timestamp = Timestamp.now(),
    val location: String = "",
    val durationMinutes: Int = 120, // Default 2 hours
    val backgroundImageUrl: String = "",
    val inviteCode: String = "",
    val guestIds: List<String> = emptyList(),
    val photoCount: Int = 0,
    val playlistUrls: List<String> = emptyList(),
    val createdAt: Timestamp = Timestamp.now(),
    val updatedAt: Timestamp = Timestamp.now()
)


