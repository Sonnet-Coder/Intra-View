package com.eventapp.intraview.ui.screens.auth

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.eventapp.intraview.data.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {
    
    val isAuthenticated: StateFlow<Boolean> = authRepository.observeAuthState()
        .onEach { user -> 
            Log.d("AuthViewModel", "Auth state changed: user=${user?.email ?: "null"}")
        }
        .map { it != null }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = authRepository.isUserAuthenticated().also { 
                Log.d("AuthViewModel", "Initial auth state: $it")
            }
        )
    
    fun isUserAuthenticated(): Boolean {
        val isAuth = authRepository.isUserAuthenticated()
        Log.d("AuthViewModel", "isUserAuthenticated called: $isAuth")
        return isAuth
    }
}


