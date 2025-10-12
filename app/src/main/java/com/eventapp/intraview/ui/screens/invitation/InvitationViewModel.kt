package com.eventapp.intraview.ui.screens.invitation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.eventapp.intraview.data.model.Event
import com.eventapp.intraview.data.model.Invitation
import com.eventapp.intraview.data.repository.AuthRepository
import com.eventapp.intraview.data.repository.EventRepository
import com.eventapp.intraview.data.repository.InvitationRepository
import com.eventapp.intraview.util.Result
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class InvitationViewModel @Inject constructor(
    private val eventRepository: EventRepository,
    private val invitationRepository: InvitationRepository,
    private val authRepository: AuthRepository
) : ViewModel() {
    
    private val _event = MutableStateFlow<Event?>(null)
    val event: StateFlow<Event?> = _event.asStateFlow()
    
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()
    
    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()
    
    private val _accepted = MutableStateFlow(false)
    val accepted: StateFlow<Boolean> = _accepted.asStateFlow()
    
    fun loadEvent(eventId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _event.value = eventRepository.getEvent(eventId)
            _isLoading.value = false
        }
    }
    
    fun acceptInvitation() {
        viewModelScope.launch {
            val currentEvent = _event.value ?: return@launch
            val userId = authRepository.currentUserId ?: return@launch
            
            _isLoading.value = true
            
            // Create invitation
            val invitationResult = invitationRepository.createInvitation(
                eventId = currentEvent.eventId,
                userId = userId
            )
            
            if (invitationResult is Result.Success) {
                // Accept invitation
                val acceptResult = invitationRepository.acceptInvitation(
                    invitationResult.data.invitationId
                )
                
                if (acceptResult is Result.Success) {
                    // Add user to event guest list
                    val addGuestResult = eventRepository.addGuestToEvent(
                        currentEvent.eventId,
                        userId
                    )
                    
                    if (addGuestResult is Result.Success) {
                        _accepted.value = true
                    } else if (addGuestResult is Result.Error) {
                        _error.value = addGuestResult.message
                    }
                } else if (acceptResult is Result.Error) {
                    _error.value = acceptResult.message
                }
            } else if (invitationResult is Result.Error) {
                _error.value = invitationResult.message
            }
            
            _isLoading.value = false
        }
    }
    
    fun declineInvitation() {
        // For simplicity, we just navigate back
        // In a full implementation, we'd create and decline the invitation
    }
}


