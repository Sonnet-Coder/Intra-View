package com.eventapp.intraview.ui.screens.playlist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.eventapp.intraview.data.model.Event
import com.eventapp.intraview.data.repository.EventRepository
import com.eventapp.intraview.util.Result
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PlaylistViewModel @Inject constructor(
    private val eventRepository: EventRepository
) : ViewModel() {
    
    private val _event = MutableStateFlow<Event?>(null)
    val event: StateFlow<Event?> = _event.asStateFlow()
    
    private val _playlistUrl = MutableStateFlow("")
    val playlistUrl: StateFlow<String> = _playlistUrl.asStateFlow()
    
    private val _showAddDialog = MutableStateFlow(false)
    val showAddDialog: StateFlow<Boolean> = _showAddDialog.asStateFlow()
    
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()
    
    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()
    
    fun loadEvent(eventId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            
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
    }
    
    fun setPlaylistUrl(url: String) {
        _playlistUrl.value = url
    }
    
    fun showAddDialog() {
        _showAddDialog.value = true
    }
    
    fun hideAddDialog() {
        _showAddDialog.value = false
        _playlistUrl.value = ""
    }
    
    fun addPlaylist() {
        viewModelScope.launch {
            val event = _event.value ?: return@launch
            val url = _playlistUrl.value
            
            if (url.isBlank()) {
                _error.value = "Please enter a playlist URL"
                return@launch
            }
            
            if (!isValidYoutubeUrl(url)) {
                _error.value = "Please enter a valid YouTube URL"
                return@launch
            }
            
            when (val result = eventRepository.addPlaylistUrl(event.eventId, url)) {
                is Result.Success -> {
                    hideAddDialog()
                }
                is Result.Error -> {
                    _error.value = result.message
                }
                else -> {}
            }
        }
    }
    
    fun removePlaylist(url: String) {
        viewModelScope.launch {
            val event = _event.value ?: return@launch
            
            when (val result = eventRepository.removePlaylistUrl(event.eventId, url)) {
                is Result.Error -> {
                    _error.value = result.message
                }
                else -> {}
            }
        }
    }
    
    private fun isValidYoutubeUrl(url: String): Boolean {
        return url.contains("youtube.com") || url.contains("youtu.be")
    }
    
    fun extractPlaylistId(url: String): String? {
        return try {
            when {
                url.contains("list=") -> {
                    val listParam = url.substringAfter("list=")
                    listParam.substringBefore("&")
                }
                else -> null
            }
        } catch (e: Exception) {
            null
        }
    }
}


