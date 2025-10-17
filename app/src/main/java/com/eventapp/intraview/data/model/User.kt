package com.eventapp.intraview.data.model

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId

data class User(
    @DocumentId
    val userId: String = "",
    val email: String = "",
    val displayName: String = "",
    val photoUrl: String = "",
    val bio: String = "",
    val instagramHandle: String = "",
    val twitterHandle: String = "",
    val pinterestHandle: String = "",
    val tiktokHandle: String = "",
    val youtubeHandle: String = "",
    val createdAt: Timestamp = Timestamp.now()
)


