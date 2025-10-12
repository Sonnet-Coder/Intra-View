package com.eventapp.intraview.ui.screens.auth

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.eventapp.intraview.data.repository.AuthRepository
import com.eventapp.intraview.util.Result
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {
    
    private val _loginState = MutableStateFlow<LoginState>(LoginState.Idle)
    val loginState: StateFlow<LoginState> = _loginState.asStateFlow()
    
    fun getGoogleSignInClient(context: Context): GoogleSignInClient {
        Log.d("LoginViewModel", "Getting Google Sign-In client")
        return authRepository.getGoogleSignInClient(context)
    }
    
    fun signInWithGoogle(account: GoogleSignInAccount) {
        viewModelScope.launch {
            Log.d("LoginViewModel", "Starting sign-in with Google for account: ${account.email}")
            _loginState.value = LoginState.Loading
            when (val result = authRepository.signInWithGoogle(account)) {
                is Result.Success -> {
                    Log.d("LoginViewModel", "Sign-in SUCCESS! User: ${result.data.email}")
                    _loginState.value = LoginState.Success
                }
                is Result.Error -> {
                    Log.e("LoginViewModel", "Sign-in ERROR: ${result.message}")
                    _loginState.value = LoginState.Error(result.message)
                }
                else -> {
                    Log.w("LoginViewModel", "Sign-in returned unexpected result")
                }
            }
        }
    }
    
    fun resetState() {
        Log.d("LoginViewModel", "Resetting login state")
        _loginState.value = LoginState.Idle
    }
}

sealed class LoginState {
    object Idle : LoginState()
    object Loading : LoginState()
    object Success : LoginState()
    data class Error(val message: String) : LoginState()
}


