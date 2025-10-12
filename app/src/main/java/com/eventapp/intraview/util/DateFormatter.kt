package com.eventapp.intraview.util

import com.google.firebase.Timestamp
import java.text.SimpleDateFormat
import java.util.*

object DateFormatter {
    private val dateFormat = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
    private val timeFormat = SimpleDateFormat("h:mm a", Locale.getDefault())
    private val dateTimeFormat = SimpleDateFormat("MMM dd, yyyy 'at' h:mm a", Locale.getDefault())
    private val fullDateFormat = SimpleDateFormat("EEEE, MMMM dd, yyyy", Locale.getDefault())
    
    fun formatDate(timestamp: Timestamp): String {
        return dateFormat.format(timestamp.toDate())
    }
    
    fun formatTime(timestamp: Timestamp): String {
        return timeFormat.format(timestamp.toDate())
    }
    
    fun formatDateTime(timestamp: Timestamp): String {
        return dateTimeFormat.format(timestamp.toDate())
    }
    
    fun formatFullDate(timestamp: Timestamp): String {
        return fullDateFormat.format(timestamp.toDate())
    }
    
    fun formatRelativeTime(timestamp: Timestamp): String {
        val now = System.currentTimeMillis()
        val diff = now - timestamp.toDate().time
        
        return when {
            diff < 60_000 -> "Just now"
            diff < 3600_000 -> "${diff / 60_000}m ago"
            diff < 86400_000 -> "${diff / 3600_000}h ago"
            diff < 604800_000 -> "${diff / 86400_000}d ago"
            else -> formatDate(timestamp)
        }
    }
}


