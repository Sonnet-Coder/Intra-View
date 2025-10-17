package com.eventapp.intraview.data.model

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.PropertyName

data class Event(
    @DocumentId
    val eventId: String = "",
    val hostId: String = "",
    val name: String = "",
    val description: String = "",
    val date: Timestamp = Timestamp.now(),
    val location: String = "",
    val latitude: Double? = null,
    val longitude: Double? = null,
    val durationMinutes: Int = 120, // Default 2 hours
    val backgroundImageUrl: String = "",
    val inviteCode: String = "",
    val guestIds: List<String> = emptyList(),
    val pendingGuestIds: List<String> = emptyList(), // Users awaiting host approval
    val photoCount: Int = 0,
    val playlistUrls: List<String> = emptyList(),
    val musicPlaylistUrl: String? = null, // Optional external music playlist link
    val sharedAlbumUrl: String? = null, // Optional external shared album link
    val maxGuests: Int? = null, // null means no limit
    @get:PropertyName("isPublic")
    val isPublic: Boolean = false, // Public events appear on discover page
    @get:PropertyName("isCancelled")
    val isCancelled: Boolean = false, // Event is cancelled but still visible
    @get:PropertyName("showPhotosToGuests")
    val showPhotosToGuests: Boolean = true, // Show collaborative photos section to guests
    @get:PropertyName("showPlaylistsToGuests")
    val showPlaylistsToGuests: Boolean = true, // Show collaborative playlists section to guests
    val createdAt: Timestamp = Timestamp.now(),
    val updatedAt: Timestamp = Timestamp.now()
)


