package com.eventapp.intraview.ui.screens.photo

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.eventapp.intraview.data.model.Photo
import com.eventapp.intraview.data.repository.AuthRepository
import com.eventapp.intraview.data.repository.EventRepository
import com.eventapp.intraview.data.repository.PhotoRepository
import com.eventapp.intraview.util.Result
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PhotoViewModel @Inject constructor(
    private val photoRepository: PhotoRepository,
    private val authRepository: AuthRepository,
    private val eventRepository: EventRepository
) : ViewModel() {
    
    private val _photos = MutableStateFlow<List<Photo>>(emptyList())
    val photos: StateFlow<List<Photo>> = _photos.asStateFlow()
    
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()
    
    private val _uploadState = MutableStateFlow<UploadState>(UploadState.Idle)
    val uploadState: StateFlow<UploadState> = _uploadState.asStateFlow()
    
    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()
    
    fun loadPhotos(eventId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            
            photoRepository.observeEventPhotos(eventId)
                .catch { e ->
                    _error.value = e.message
                    _isLoading.value = false
                }
                .collect { photos ->
                    _photos.value = photos
                    _isLoading.value = false
                }
        }
    }
    
    fun uploadPhoto(context: Context, eventId: String, imageUri: Uri) {
        viewModelScope.launch {
            _uploadState.value = UploadState.Loading
            
            val currentUser = authRepository.getCurrentUser()
            if (currentUser == null) {
                _uploadState.value = UploadState.Error("User not authenticated")
                return@launch
            }
            
            when (val result = photoRepository.uploadPhoto(eventId, imageUri, currentUser)) {
                is Result.Success -> {
                    _uploadState.value = UploadState.Success
                    // Increment photo count
                    eventRepository.incrementPhotoCount(eventId)
                }
                is Result.Error -> {
                    _uploadState.value = UploadState.Error(result.message)
                }
                else -> {}
            }
        }
    }
    
    fun deletePhoto(eventId: String, photoId: String) {
        viewModelScope.launch {
            when (val result = photoRepository.deletePhoto(photoId, eventId)) {
                is Result.Success -> {
                    // Photo will be automatically removed from list via Flow
                }
                is Result.Error -> {
                    _error.value = result.message
                }
                else -> {}
            }
        }
    }
    
    fun resetUploadState() {
        _uploadState.value = UploadState.Idle
    }
}

sealed class UploadState {
    object Idle : UploadState()
    object Loading : UploadState()
    object Success : UploadState()
    data class Error(val message: String) : UploadState()
}


