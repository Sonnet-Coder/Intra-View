package com.eventapp.intraview.ui.screens.home

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.eventapp.intraview.data.model.Event
import com.eventapp.intraview.data.repository.AuthRepository
import com.eventapp.intraview.data.repository.EventRepository
import com.eventapp.intraview.data.repository.InvitationRepository
import com.eventapp.intraview.data.repository.PendingGuestRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val eventRepository: EventRepository,
    private val authRepository: AuthRepository,
    private val invitationRepository: InvitationRepository,
    private val pendingGuestRepository: PendingGuestRepository
) : ViewModel() {
    
    companion object {
        private const val TAG = "HomeViewModel"
    }
    
    private val _myEvents = MutableStateFlow<List<Event>>(emptyList())
    val myEvents: StateFlow<List<Event>> = _myEvents.asStateFlow()
    
    private val _invitedEvents = MutableStateFlow<List<Event>>(emptyList())
    val invitedEvents: StateFlow<List<Event>> = _invitedEvents.asStateFlow()
    
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()
    
    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()
    
    private val _inviteCode = MutableStateFlow("")
    val inviteCode: StateFlow<String> = _inviteCode.asStateFlow()
    
    private val _showInviteDialog = MutableStateFlow(false)
    val showInviteDialog: StateFlow<Boolean> = _showInviteDialog.asStateFlow()
    
    private val _isJoiningEvent = MutableStateFlow(false)
    val isJoiningEvent: StateFlow<Boolean> = _isJoiningEvent.asStateFlow()
    
    private val _showLogoutDialog = MutableStateFlow(false)
    val showLogoutDialog: StateFlow<Boolean> = _showLogoutDialog.asStateFlow()
    
    init {
        loadEvents()
    }
    
    private fun loadEvents() {
        viewModelScope.launch {
            _isLoading.value = true
            
            // Observe my events (hosting)
            eventRepository.observeMyEvents()
                .catch { e ->
                    _error.value = e.message
                }
                .collect { events ->
                    _myEvents.value = events
                    _isLoading.value = false
                }
        }
        
        viewModelScope.launch {
            // Observe invited events
            eventRepository.observeInvitedEvents()
                .catch { e ->
                    _error.value = e.message
                }
                .collect { events ->
                    _invitedEvents.value = events
                }
        }
    }
    
    fun setInviteCode(code: String) {
        _inviteCode.value = code.uppercase()
    }
    
    fun showInviteDialog() {
        _showInviteDialog.value = true
    }
    
    fun hideInviteDialog() {
        _showInviteDialog.value = false
        _inviteCode.value = ""
    }
    
    fun showLogoutDialog() {
        _showLogoutDialog.value = true
    }
    
    fun hideLogoutDialog() {
        _showLogoutDialog.value = false
    }
    
    suspend fun joinEventWithCode(): Event? {
        val code = _inviteCode.value
        if (code.isBlank()) {
            _error.value = "Please enter an invite code"
            return null
        }
        
        _isJoiningEvent.value = true
        _error.value = null
        
        Log.d(TAG, "Attempting to join event with code: $code")
        val event = eventRepository.findEventByInviteCode(code)
        
        if (event == null) {
            Log.w(TAG, "Event not found for invite code: $code")
            _error.value = "Invalid invite code"
            _isJoiningEvent.value = false
            return null
        }
        
        Log.d(TAG, "Found event: ${event.name} (${event.eventId})")
        
        // Add the current user as a guest to the event
        val userId = authRepository.currentUserId
        if (userId == null) {
            Log.e(TAG, "User not authenticated")
            _error.value = "User not authenticated"
            _isJoiningEvent.value = false
            return null
        }
        
        Log.d(TAG, "Current user ID: $userId")
        
        // Check if user is already a guest or host
        if (event.hostId == userId) {
            Log.w(TAG, "User is the host of this event")
            _error.value = "You are the host of this event"
            _isJoiningEvent.value = false
            return null
        }
        
        // Check if user already has an invitation (already approved)
        val existingInvitation = invitationRepository.getInvitationForUserAndEvent(userId, event.eventId)
        
        if (existingInvitation != null) {
            Log.d(TAG, "User already has an invitation for this event")
            // Already a guest, just navigate to the event
            _isJoiningEvent.value = false
            return event
        }
        
        // Check if user already has a pending request
        val existingPendingGuest = pendingGuestRepository.getPendingGuest(event.eventId, userId)
        
        if (existingPendingGuest != null) {
            Log.d(TAG, "User already has a pending request for this event")
            _error.value = "Your request to join is pending host approval"
            _isJoiningEvent.value = false
            return null
        }
        
        // Check guest limit before allowing new guest to request
        val currentGuestCount = event.guestIds.size
        val maxGuests = event.maxGuests
        
        if (maxGuests != null && currentGuestCount >= maxGuests) {
            Log.w(TAG, "Event is full: $currentGuestCount/$maxGuests guests")
            _error.value = "Event is full! Maximum capacity of $maxGuests guests reached."
            _isJoiningEvent.value = false
            return null
        }
        
        Log.d(TAG, "Guest limit check passed: $currentGuestCount/${maxGuests ?: "No Limit"} guests")
        
        // Add user to pending guest list instead of directly adding to event
        Log.d(TAG, "Adding user to pending guest list for approval")
        val addPendingResult = eventRepository.addPendingGuest(event.eventId, userId)
        
        if (addPendingResult is com.eventapp.intraview.util.Result.Error) {
            Log.e(TAG, "Failed to add user to pending list: ${addPendingResult.message}")
            _error.value = addPendingResult.message
            _isJoiningEvent.value = false
            return null
        }
        
        // Create pending guest record with user info
        Log.d(TAG, "Creating pending guest record")
        val pendingGuestResult = pendingGuestRepository.createPendingGuest(event.eventId, userId)
        
        if (pendingGuestResult is com.eventapp.intraview.util.Result.Error) {
            Log.e(TAG, "Failed to create pending guest: ${pendingGuestResult.message}")
            _error.value = "Failed to create join request: ${pendingGuestResult.message}"
            _isJoiningEvent.value = false
            return null
        }
        
        if (pendingGuestResult is com.eventapp.intraview.util.Result.Success) {
            Log.d(TAG, "Successfully created pending guest request")
        }
        
        _isJoiningEvent.value = false
        _error.value = "Join request sent! Waiting for host approval"
        Log.d(TAG, "Successfully sent join request for approval")
        
        return null // Don't navigate to event yet, user must wait for approval
    }
    
    suspend fun signOut(context: android.content.Context) {
        authRepository.signOut(context)
    }
}


