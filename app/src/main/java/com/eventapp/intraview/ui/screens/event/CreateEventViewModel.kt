package com.eventapp.intraview.ui.screens.event

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.eventapp.intraview.data.repository.EventRepository
import com.eventapp.intraview.util.Constants
import com.eventapp.intraview.util.Result
import com.google.firebase.Timestamp
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.Date
import javax.inject.Inject

@HiltViewModel
class CreateEventViewModel @Inject constructor(
    private val eventRepository: EventRepository
) : ViewModel() {
    
    private val _name = MutableStateFlow("")
    val name: StateFlow<String> = _name.asStateFlow()
    
    private val _description = MutableStateFlow("")
    val description: StateFlow<String> = _description.asStateFlow()
    
    private val _date = MutableStateFlow(Date())
    val date: StateFlow<Date> = _date.asStateFlow()
    
    private val _location = MutableStateFlow("")
    val location: StateFlow<String> = _location.asStateFlow()
    
    private val _durationMinutes = MutableStateFlow(120) // Default 2 hours
    val durationMinutes: StateFlow<Int> = _durationMinutes.asStateFlow()
    
    private val _selectedBackgroundIndex = MutableStateFlow(0)
    val selectedBackgroundIndex: StateFlow<Int> = _selectedBackgroundIndex.asStateFlow()
    
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()
    
    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()
    
    private val _createdEventId = MutableStateFlow<String?>(null)
    val createdEventId: StateFlow<String?> = _createdEventId.asStateFlow()
    
    val backgroundImages = Constants.BACKGROUND_IMAGES
    
    fun setName(value: String) {
        _name.value = value
    }
    
    fun setDescription(value: String) {
        _description.value = value
    }
    
    fun setDate(value: Date) {
        _date.value = value
    }
    
    fun setLocation(value: String) {
        _location.value = value
    }
    
    fun setDurationMinutes(value: Int) {
        _durationMinutes.value = value
    }
    
    fun setSelectedBackgroundIndex(index: Int) {
        _selectedBackgroundIndex.value = index
    }
    
    fun createEvent() {
        viewModelScope.launch {
            if (!validateInputs()) return@launch
            
            _isLoading.value = true
            _error.value = null
            
            val result = eventRepository.createEvent(
                name = _name.value,
                description = _description.value,
                date = Timestamp(_date.value),
                location = _location.value,
                durationMinutes = _durationMinutes.value,
                backgroundImageUrl = backgroundImages[_selectedBackgroundIndex.value]
            )
            
            _isLoading.value = false
            
            when (result) {
                is Result.Success -> {
                    _createdEventId.value = result.data.eventId
                }
                is Result.Error -> {
                    _error.value = result.message
                }
                else -> {}
            }
        }
    }
    
    private fun validateInputs(): Boolean {
        return when {
            _name.value.isBlank() -> {
                _error.value = "Event name is required"
                false
            }
            _location.value.isBlank() -> {
                _error.value = "Location is required"
                false
            }
            _date.value.before(Date()) -> {
                _error.value = "Date must be in the future"
                false
            }
            else -> true
        }
    }
}


