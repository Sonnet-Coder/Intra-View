package com.eventapp.intraview.ui.screens.guest

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.eventapp.intraview.data.model.User
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

@HiltViewModel
class GuestDetailViewModel @Inject constructor(
    private val firestore: FirebaseFirestore
) : ViewModel() {

    private val _user = MutableStateFlow<User?>(null)
    val user: StateFlow<User?> = _user.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    fun loadUser(userId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val userDoc = firestore.collection("users")
                    .document(userId)
                    .get()
                    .await()

                _user.value = userDoc.toObject(User::class.java)
            } catch (e: Exception) {
                Log.e("GuestDetailViewModel", "Error loading user", e)
            } finally {
                _isLoading.value = false
            }
        }
    }
}
