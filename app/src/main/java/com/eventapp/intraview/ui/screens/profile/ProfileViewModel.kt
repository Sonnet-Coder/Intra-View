package com.eventapp.intraview.ui.screens.profile

import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.eventapp.intraview.data.model.User
import com.eventapp.intraview.data.repository.AuthRepository
import com.eventapp.intraview.data.repository.UserRepository
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val userRepository: UserRepository,
    private val firestore: FirebaseFirestore,
    private val storage: FirebaseStorage
) : ViewModel() {

    private val _user = MutableStateFlow<User?>(null)
    val user: StateFlow<User?> = _user.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _isSaving = MutableStateFlow(false)
    val isSaving: StateFlow<Boolean> = _isSaving.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    private val _isEditMode = MutableStateFlow(false)
    val isEditMode: StateFlow<Boolean> = _isEditMode.asStateFlow()

    init {
        loadUserProfile()
    }

    private fun loadUserProfile() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val currentUser = authRepository.currentUser
                if (currentUser != null) {
                    val userDoc = firestore.collection("users")
                        .document(currentUser.uid)
                        .get()
                        .await()

                    if (userDoc.exists()) {
                        _user.value = userDoc.toObject(User::class.java)
                    } else {
                        // Create new user profile from Google account
                        val newUser = User(
                            userId = currentUser.uid,
                            email = currentUser.email ?: "",
                            displayName = currentUser.displayName ?: "",
                            photoUrl = currentUser.photoUrl?.toString() ?: ""
                        )
                        firestore.collection("users")
                            .document(currentUser.uid)
                            .set(newUser)
                            .await()
                        _user.value = newUser
                    }
                }
            } catch (e: Exception) {
                Log.e("ProfileViewModel", "Error loading profile", e)
                _error.value = "Failed to load profile: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun toggleEditMode() {
        _isEditMode.value = !_isEditMode.value
    }

    fun updateUser(updatedUser: User) {
        _user.value = updatedUser
    }

    fun saveProfile() {
        viewModelScope.launch {
            _isSaving.value = true
            _error.value = null
            try {
                val currentUser = _user.value
                if (currentUser != null) {
                    firestore.collection("users")
                        .document(currentUser.userId)
                        .set(currentUser)
                        .await()
                    _isEditMode.value = false
                }
            } catch (e: Exception) {
                Log.e("ProfileViewModel", "Error saving profile", e)
                _error.value = "Failed to save profile: ${e.message}"
            } finally {
                _isSaving.value = false
            }
        }
    }

    fun uploadProfileImage(uri: Uri) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val currentUser = authRepository.currentUser
                if (currentUser != null) {
                    val imageRef = storage.reference
                        .child("profile_images/${currentUser.uid}/${System.currentTimeMillis()}.jpg")
                    
                    imageRef.putFile(uri).await()
                    val downloadUrl = imageRef.downloadUrl.await().toString()
                    
                    val updatedUser = _user.value?.copy(photoUrl = downloadUrl)
                    if (updatedUser != null) {
                        firestore.collection("users")
                            .document(currentUser.uid)
                            .set(updatedUser)
                            .await()
                        _user.value = updatedUser
                    }
                }
            } catch (e: Exception) {
                Log.e("ProfileViewModel", "Error uploading image", e)
                _error.value = "Failed to upload image: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun clearError() {
        _error.value = null
    }
}
