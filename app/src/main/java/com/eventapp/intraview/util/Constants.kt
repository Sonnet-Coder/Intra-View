package com.eventapp.intraview.util

object Constants {
    // Firestore Collections
    const val COLLECTION_USERS = "users"
    const val COLLECTION_EVENTS = "events"
    const val COLLECTION_INVITATIONS = "invitations"
    const val COLLECTION_PHOTOS = "photos"
    
    // Storage Paths
    const val STORAGE_EVENTS = "events"
    
    // Image Settings
    const val MAX_IMAGE_WIDTH = 1920
    const val MAX_IMAGE_HEIGHT = 1920
    const val IMAGE_QUALITY = 85
    const val THUMBNAIL_SIZE = 320
    
    // Invite Code
    const val INVITE_CODE_LENGTH = 6
    
    // Background Images (preset URLs)
    val BACKGROUND_IMAGES = listOf(
        "https://images.unsplash.com/photo-1492684223066-81342ee5ff30",
        "https://images.unsplash.com/photo-1530103862676-de8c9debad1d",
        "https://images.unsplash.com/photo-1464366400600-7168b8af9bc3",
        "https://images.unsplash.com/photo-1511578314322-379afb476865",
        "https://images.unsplash.com/photo-1505236858219-8359eb29e329",
        "https://images.unsplash.com/photo-1519225421980-715cb0215aed"
    )
    
    // Pagination
    const val PHOTOS_PAGE_SIZE = 20
    const val EVENTS_PAGE_SIZE = 10
}


