package com.eventapp.intraview.data.model

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId

data class Photo(
    @DocumentId
    val photoId: String = "",
    val eventId: String = "",
    val userId: String = "",
    val userName: String = "",
    val userPhotoUrl: String = "",
    val imageUrl: String = "",
    val thumbnailUrl: String = "",
    val uploadedAt: Timestamp = Timestamp.now()
)


