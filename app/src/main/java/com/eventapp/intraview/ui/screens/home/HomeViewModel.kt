package com.eventapp.intraview.ui.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.eventapp.intraview.data.model.Event
import com.eventapp.intraview.data.repository.AuthRepository
import com.eventapp.intraview.data.repository.EventRepository
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
    private val authRepository: AuthRepository
) : ViewModel() {
    
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
    
    suspend fun joinEventWithCode(): Event? {
        val code = _inviteCode.value
        if (code.isBlank()) {
            _error.value = "Please enter an invite code"
            return null
        }
        
        _isJoiningEvent.value = true
        _error.value = null
        
        val event = eventRepository.findEventByInviteCode(code)
        
        if (event == null) {
            _error.value = "Invalid invite code"
            _isJoiningEvent.value = false
            return null
        }
        
        // Add the current user as a guest to the event
        val userId = authRepository.currentUserId
        if (userId == null) {
            _error.value = "User not authenticated"
            _isJoiningEvent.value = false
            return null
        }
        
        // Check if user is already a guest or host
        if (event.hostId == userId) {
            _error.value = "You are the host of this event"
            _isJoiningEvent.value = false
            return null
        }
        
        if (event.guestIds.contains(userId)) {
            // Already a guest, just navigate to the event
            _isJoiningEvent.value = false
            return event
        }
        
        val result = eventRepository.addGuestToEvent(event.eventId, userId)
        
        _isJoiningEvent.value = false
        
        if (result is com.eventapp.intraview.util.Result.Error) {
            _error.value = result.message
            return null
        }
        
        return event
    }
    
    suspend fun signOut(context: android.content.Context) {
        authRepository.signOut(context)
    }
}


