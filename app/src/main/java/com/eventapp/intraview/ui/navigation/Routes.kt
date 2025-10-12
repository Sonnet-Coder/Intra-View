package com.eventapp.intraview.ui.navigation

sealed class Routes(val route: String) {
    object Login : Routes("login")
    object Home : Routes("home")
    object CreateEvent : Routes("create_event")
    object EventDetail : Routes("event_detail/{eventId}") {
        fun createRoute(eventId: String) = "event_detail/$eventId"
    }
    object InvitationPreview : Routes("invitation_preview/{eventId}") {
        fun createRoute(eventId: String) = "invitation_preview/$eventId"
    }
    object PhotoGallery : Routes("photo_gallery/{eventId}") {
        fun createRoute(eventId: String) = "photo_gallery/$eventId"
    }
    object QRDisplay : Routes("qr_display/{eventId}") {
        fun createRoute(eventId: String) = "qr_display/$eventId"
    }
    object QRScanner : Routes("qr_scanner/{eventId}") {
        fun createRoute(eventId: String) = "qr_scanner/$eventId"
    }
    object Playlist : Routes("playlist/{eventId}") {
        fun createRoute(eventId: String) = "playlist/$eventId"
    }
    object Profile : Routes("profile")
}


