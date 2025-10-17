package com.eventapp.intraview.ui.screens.guest

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.eventapp.intraview.data.model.Invitation
import com.eventapp.intraview.util.Constants
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

@HiltViewModel
class GuestListViewModel @Inject constructor(
    private val firestore: FirebaseFirestore
) : ViewModel() {

    private val _guests = MutableStateFlow<List<Invitation>>(emptyList())
    val guests: StateFlow<List<Invitation>> = _guests.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    fun loadGuests(eventId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                Log.d("GuestListViewModel", "Loading guests for eventId: $eventId")
                
                // Use the global invitations collection and filter by eventId
                val invitationsSnapshot = firestore.collection(Constants.COLLECTION_INVITATIONS)
                    .whereEqualTo("eventId", eventId)
                    .get()
                    .await()

                Log.d("GuestListViewModel", "Found ${invitationsSnapshot.documents.size} invitation documents")
                
                val guestsList = invitationsSnapshot.documents.mapNotNull { doc ->
                    val invitation = doc.toObject(Invitation::class.java)
                    Log.d("GuestListViewModel", "Invitation: userId=${invitation?.userId}, userName=${invitation?.userName}, userEmail=${invitation?.userEmail}, photoUrl=${invitation?.userPhotoUrl}")
                    invitation
                }
                
                Log.d("GuestListViewModel", "Loaded ${guestsList.size} valid guests")
                _guests.value = guestsList
            } catch (e: Exception) {
                Log.e("GuestListViewModel", "Error loading guests", e)
            } finally {
                _isLoading.value = false
            }
        }
    }
}
