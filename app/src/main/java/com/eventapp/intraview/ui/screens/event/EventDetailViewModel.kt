package com.eventapp.intraview.ui.screens.event

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.eventapp.intraview.data.model.Event
import com.eventapp.intraview.data.model.Invitation
import com.eventapp.intraview.data.model.PendingGuest
import com.eventapp.intraview.data.model.Photo
import com.eventapp.intraview.data.model.User
import com.eventapp.intraview.data.repository.AuthRepository
import com.eventapp.intraview.data.repository.EventRepository
import com.eventapp.intraview.data.repository.InvitationRepository
import com.eventapp.intraview.data.repository.PendingGuestRepository
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
    private val authRepository: AuthRepository,
    private val pendingGuestRepository: PendingGuestRepository
) : ViewModel() {
    
    companion object {
        private const val TAG = "EventDetailViewModel"
    }
    
    private val _event = MutableStateFlow<Event?>(null)
    val event: StateFlow<Event?> = _event.asStateFlow()
    
    private val _invitations = MutableStateFlow<List<Invitation>>(emptyList())
    val invitations: StateFlow<List<Invitation>> = _invitations.asStateFlow()
    
    private val _pendingGuests = MutableStateFlow<List<PendingGuest>>(emptyList())
    val pendingGuests: StateFlow<List<PendingGuest>> = _pendingGuests.asStateFlow()
    
    private val _recentPhotos = MutableStateFlow<List<Photo>>(emptyList())
    val recentPhotos: StateFlow<List<Photo>> = _recentPhotos.asStateFlow()
    
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()
    
    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()
    
    private val _currentUser = MutableStateFlow<User?>(null)
    val currentUser: StateFlow<User?> = _currentUser.asStateFlow()
    
    private val _isHost = MutableStateFlow<Boolean>(false)
    val isHost: StateFlow<Boolean> = _isHost.asStateFlow()
    
    fun loadEvent(eventId: String) {
        Log.d(TAG, "Loading event details for eventId: $eventId")
        
        viewModelScope.launch {
            _isLoading.value = true
            
            // Load current user
            _currentUser.value = authRepository.getCurrentUser()
            Log.d(TAG, "Current user loaded: ${_currentUser.value?.email}")
            
            // Observe event
            eventRepository.observeEvent(eventId)
                .catch { e ->
                    Log.e(TAG, "Error observing event: ${e.message}", e)
                    _error.value = e.message
                    _isLoading.value = false
                }
                .collect { event ->
                    _event.value = event
                    Log.d(TAG, "Event updated: ${event?.name}, guestIds count: ${event?.guestIds?.size}")
                    Log.d(TAG, "Event sharedAlbumUrl: '${event?.sharedAlbumUrl}' (null: ${event?.sharedAlbumUrl == null})")
                    Log.d(TAG, "Event musicPlaylistUrl: '${event?.musicPlaylistUrl}' (null: ${event?.musicPlaylistUrl == null})")
                    _isHost.value = event?.hostId == authRepository.currentUserId
                    Log.d(TAG, "Host status updated: ${_isHost.value}")
                    _isLoading.value = false
                }
        }
        
        viewModelScope.launch {
            // Observe invitations
            invitationRepository.observeEventInvitations(eventId)
                .catch { e ->
                    Log.e(TAG, "Error observing invitations: ${e.message}", e)
                    _error.value = e.message
                }
                .collect { invitations ->
                    _invitations.value = invitations
                    Log.d(TAG, "Invitations updated: ${invitations.size} invitation(s) found")
                    invitations.forEachIndexed { index, invitation ->
                        Log.d(TAG, "  Invitation $index: userId=${invitation.userId}, userName=${invitation.userName}, userEmail=${invitation.userEmail}, userPhotoUrl=${invitation.userPhotoUrl}, checkedIn=${invitation.checkedIn}")
                    }
                }
        }
        
        viewModelScope.launch {
            // Observe pending guests
            pendingGuestRepository.observePendingGuests(eventId)
                .catch { e ->
                    Log.e(TAG, "Error observing pending guests: ${e.message}", e)
                    _error.value = e.message
                }
                .collect { pendingGuests ->
                    _pendingGuests.value = pendingGuests
                    Log.d(TAG, "Pending guests updated: ${pendingGuests.size} pending request(s) found")
                    pendingGuests.forEachIndexed { index, guest ->
                        Log.d(TAG, "  Pending guest $index: userId=${guest.userId}, userName=${guest.userName}, userEmail=${guest.userEmail}")
                    }
                }
        }
        
        viewModelScope.launch {
            // Observe recent photos (limited to 6)
            photoRepository.observeEventPhotos(eventId)
                .catch { e ->
                    Log.e(TAG, "Error observing photos: ${e.message}", e)
                    _error.value = e.message
                }
                .collect { photos ->
                    _recentPhotos.value = photos.take(6)
                    Log.d(TAG, "Photos updated: ${photos.size} photo(s) found")
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
    
    fun updateEventField(field: String, value: Any?) {
        viewModelScope.launch {
            val event = _event.value ?: return@launch
            try {
                val updates: Map<String, Any> = if (value == null) {
                    Log.d(TAG, "Deleting field: $field")
                    mapOf(field to com.google.firebase.firestore.FieldValue.delete())
                } else {
                    Log.d(TAG, "Updating field: $field = $value")
                    mapOf(field to value)
                }
                val result = eventRepository.updateEvent(event.eventId, updates)
                Log.d(TAG, "Update result: $result")
            } catch (e: Exception) {
                Log.e(TAG, "Error updating event field: ${e.message}", e)
                _error.value = e.message
            }
        }
    }
    
    fun deleteEvent() {
        viewModelScope.launch {
            val event = _event.value ?: return@launch
            try {
                eventRepository.deleteEvent(event.eventId)
            } catch (e: Exception) {
                Log.e(TAG, "Error deleting event: ${e.message}", e)
                _error.value = e.message
            }
        }
    }
    
    fun approveGuest(pendingGuest: PendingGuest) {
        viewModelScope.launch {
            val event = _event.value ?: return@launch
            try {
                Log.d(TAG, "Approving guest: ${pendingGuest.userName}")
                
                // Approve the guest (moves from pending to approved in Event)
                val approveResult = eventRepository.approveGuest(event.eventId, pendingGuest.userId)
                
                if (approveResult is com.eventapp.intraview.util.Result.Error) {
                    Log.e(TAG, "Failed to approve guest: ${approveResult.message}")
                    _error.value = approveResult.message
                    return@launch
                }
                
                // Create invitation with QR code for the approved guest
                val invitationResult = invitationRepository.createInvitation(event.eventId, pendingGuest.userId)
                
                if (invitationResult is com.eventapp.intraview.util.Result.Error) {
                    Log.e(TAG, "Failed to create invitation: ${invitationResult.message}")
                    _error.value = "Guest approved but failed to create invitation: ${invitationResult.message}"
                    return@launch
                }
                
                // Delete pending guest record
                pendingGuestRepository.deletePendingGuest(event.eventId, pendingGuest.userId)
                
                Log.d(TAG, "Successfully approved guest: ${pendingGuest.userName}")
            } catch (e: Exception) {
                Log.e(TAG, "Error approving guest: ${e.message}", e)
                _error.value = e.message
            }
        }
    }
    
    fun rejectGuest(pendingGuest: PendingGuest) {
        viewModelScope.launch {
            val event = _event.value ?: return@launch
            try {
                Log.d(TAG, "Rejecting guest: ${pendingGuest.userName}")
                
                // Reject the guest (removes from pending list in Event)
                val rejectResult = eventRepository.rejectGuest(event.eventId, pendingGuest.userId)
                
                if (rejectResult is com.eventapp.intraview.util.Result.Error) {
                    Log.e(TAG, "Failed to reject guest: ${rejectResult.message}")
                    _error.value = rejectResult.message
                    return@launch
                }
                
                // Delete pending guest record
                pendingGuestRepository.deletePendingGuest(event.eventId, pendingGuest.userId)
                
                Log.d(TAG, "Successfully rejected guest: ${pendingGuest.userName}")
            } catch (e: Exception) {
                Log.e(TAG, "Error rejecting guest: ${e.message}", e)
                _error.value = e.message
            }
        }
    }
}


