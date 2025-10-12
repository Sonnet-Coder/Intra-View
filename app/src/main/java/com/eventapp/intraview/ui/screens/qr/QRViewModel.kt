package com.eventapp.intraview.ui.screens.qr

import android.graphics.Bitmap
import android.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.eventapp.intraview.data.model.Event
import com.eventapp.intraview.data.model.Invitation
import com.eventapp.intraview.data.repository.AuthRepository
import com.eventapp.intraview.data.repository.EventRepository
import com.eventapp.intraview.data.repository.InvitationRepository
import com.eventapp.intraview.util.Result
import com.google.zxing.BarcodeFormat
import com.google.zxing.MultiFormatWriter
import com.google.zxing.common.BitMatrix
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class QRViewModel @Inject constructor(
    private val eventRepository: EventRepository,
    private val invitationRepository: InvitationRepository,
    private val authRepository: AuthRepository
) : ViewModel() {
    
    private val _event = MutableStateFlow<Event?>(null)
    val event: StateFlow<Event?> = _event.asStateFlow()
    
    private val _myInvitation = MutableStateFlow<Invitation?>(null)
    val myInvitation: StateFlow<Invitation?> = _myInvitation.asStateFlow()
    
    private val _qrBitmap = MutableStateFlow<Bitmap?>(null)
    val qrBitmap: StateFlow<Bitmap?> = _qrBitmap.asStateFlow()
    
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()
    
    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()
    
    private val _scanResult = MutableStateFlow<ScanResult>(ScanResult.Idle)
    val scanResult: StateFlow<ScanResult> = _scanResult.asStateFlow()
    
    fun loadEventAndInvitation(eventId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            
            // Load event
            _event.value = eventRepository.getEvent(eventId)
            
            // Load my invitation
            _myInvitation.value = invitationRepository.getMyInvitationForEvent(eventId)
            
            // Generate QR code
            _myInvitation.value?.let { invitation ->
                _qrBitmap.value = generateQRCode(invitation.qrToken)
            }
            
            _isLoading.value = false
        }
    }
    
    private fun generateQRCode(content: String, size: Int = 512): Bitmap {
        val bitMatrix: BitMatrix = MultiFormatWriter().encode(
            content,
            BarcodeFormat.QR_CODE,
            size,
            size
        )
        
        val width = bitMatrix.width
        val height = bitMatrix.height
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565)
        
        for (x in 0 until width) {
            for (y in 0 until height) {
                bitmap.setPixel(x, y, if (bitMatrix[x, y]) Color.BLACK else Color.WHITE)
            }
        }
        
        return bitmap
    }
    
    fun checkInGuest(qrToken: String, eventId: String) {
        viewModelScope.launch {
            _scanResult.value = ScanResult.Scanning
            
            when (val result = invitationRepository.checkInGuest(qrToken, eventId)) {
                is Result.Success -> {
                    _scanResult.value = ScanResult.Success(result.data)
                }
                is Result.Error -> {
                    _scanResult.value = ScanResult.Error(result.message)
                }
                else -> {}
            }
        }
    }
    
    fun resetScanResult() {
        _scanResult.value = ScanResult.Idle
    }
}

sealed class ScanResult {
    object Idle : ScanResult()
    object Scanning : ScanResult()
    data class Success(val invitation: Invitation) : ScanResult()
    data class Error(val message: String) : ScanResult()
}


