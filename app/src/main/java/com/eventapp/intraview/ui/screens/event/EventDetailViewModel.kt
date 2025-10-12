package com.eventapp.intraview.ui.screens.event

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.eventapp.intraview.data.model.Event
import com.eventapp.intraview.data.model.Invitation
import com.eventapp.intraview.data.model.Photo
import com.eventapp.intraview.data.model.User
import com.eventapp.intraview.data.repository.AuthRepository
import com.eventapp.intraview.data.repository.EventRepository
import com.eventapp.intraview.data.repository.InvitationRepository
import com.eventapp.intraview.data.repository.PhotoRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EventDetailViewModel @Inject constructor(
    private val eventRepository: EventRepository,
    private val invitationRepository: InvitationRepository,
    private val photoRepository: PhotoRepository,
    private val authRepository: AuthRepository
) : ViewModel() {
    
    private val _event = MutableStateFlow<Event?>(null)
    val event: StateFlow<Event?> = _event.asStateFlow()
    
    private val _invitations = MutableStateFlow<List<Invitation>>(emptyList())
    val invitations: StateFlow<List<Invitation>> = _invitations.asStateFlow()
    
    private val _recentPhotos = MutableStateFlow<List<Photo>>(emptyList())
    val recentPhotos: StateFlow<List<Photo>> = _recentPhotos.asStateFlow()
    
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()
    
    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()
    
    private val _currentUser = MutableStateFlow<User?>(null)
    val currentUser: StateFlow<User?> = _currentUser.asStateFlow()
    
    val isHost: Boolean
        get() = _event.value?.hostId == authRepository.currentUserId
    
    fun loadEvent(eventId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            
            // Load current user
            _currentUser.value = authRepository.getCurrentUser()
            
            // Observe event
            eventRepository.observeEvent(eventId)
                .catch { e ->
                    _error.value = e.message
                    _isLoading.value = false
                }
                .collect { event ->
                    _event.value = event
                    _isLoading.value = false
                }
        }
        
        viewModelScope.launch {
            // Observe invitations
            invitationRepository.observeEventInvitations(eventId)
                .catch { e ->
                    _error.value = e.message
                }
                .collect { invitations ->
                    _invitations.value = invitations
                }
        }
        
        viewModelScope.launch {
            // Observe recent photos (limited to 6)
            photoRepository.observeEventPhotos(eventId)
                .catch { e ->
                    _error.value = e.message
                }
                .collect { photos ->
                    _recentPhotos.value = photos.take(6)
                }
        }
    }
    
    fun shareInvite() {
        val event = _event.value ?: return
        // Share functionality would be implemented here
        // For now, we'll just expose the invite code
    }
    
    fun getInviteCode(): String? {
        return _event.value?.inviteCode
    }
}


