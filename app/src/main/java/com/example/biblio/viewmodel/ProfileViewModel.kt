package com.example.biblio.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import com.example.biblio.data.remote.dto.User
import com.example.biblio.data.repository.ProfileRepository
import com.example.biblio.di.AppModule
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import java.io.File

sealed class ProfileState {
    object Idle : ProfileState()
    object Loading : ProfileState()
    data class Success(val user: User) : ProfileState()
    data class Error(val message: String) : ProfileState()
}

sealed class AvatarUpdateState {
    object Idle : AvatarUpdateState()
    object Loading : AvatarUpdateState()
    object Success : AvatarUpdateState()
    data class Error(val message: String) : AvatarUpdateState()
}

class ProfileViewModel(private val repository: ProfileRepository) : ViewModel() {

    private val _profileState = MutableStateFlow<ProfileState>(ProfileState.Idle)
    val profileState: StateFlow<ProfileState> = _profileState.asStateFlow()

    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing: StateFlow<Boolean> = _isRefreshing.asStateFlow()

    private val _avatarUpdateState = MutableStateFlow<AvatarUpdateState>(AvatarUpdateState.Idle)
    val avatarUpdateState: StateFlow<AvatarUpdateState> = _avatarUpdateState.asStateFlow()

    // Digunakan untuk memaksa Coil merefresh gambar jika URL-nya tetap sama (Cache Buster)
    private val _avatarTimestamp = MutableStateFlow(System.currentTimeMillis())
    val avatarTimestamp: StateFlow<Long> = _avatarTimestamp.asStateFlow()

    init {
        repository.profileFlow.onEach { user ->
            if (user != null) {
                _profileState.value = ProfileState.Success(user)
            }
        }.launchIn(viewModelScope)

        fetchProfile()
    }

    fun fetchProfile(forceRefresh: Boolean = false) {
        viewModelScope.launch {
            if (forceRefresh) _isRefreshing.value = true else _profileState.value = ProfileState.Loading
            
            // Step 1: Muat dari cache
            repository.getCachedProfile()?.let {
                _profileState.value = ProfileState.Success(it)
            }

            // Step 2: Muat dari network
            repository.getProfile().fold(
                onSuccess = { _profileState.value = ProfileState.Success(it) },
                onFailure = { 
                    if (_profileState.value !is ProfileState.Success) {
                        _profileState.value = ProfileState.Error(it.message ?: "Unknown error") 
                    }
                }
            )
            _isRefreshing.value = false
        }
    }

    fun uploadAvatar(file: File) {
        viewModelScope.launch {
            _avatarUpdateState.value = AvatarUpdateState.Loading
            repository.uploadAvatar(file).fold(
                onSuccess = {
                    _avatarUpdateState.value = AvatarUpdateState.Success
                    _avatarTimestamp.value = System.currentTimeMillis() // Update timestamp
                    fetchProfile() // Refresh profile data
                },
                onFailure = { _avatarUpdateState.value = AvatarUpdateState.Error(it.message ?: "Upload failed") }
            )
        }
    }

    fun deleteAvatar() {
        viewModelScope.launch {
            _avatarUpdateState.value = AvatarUpdateState.Loading
            repository.deleteAvatar().fold(
                onSuccess = {
                    _avatarUpdateState.value = AvatarUpdateState.Success
                    fetchProfile()
                },
                onFailure = { _avatarUpdateState.value = AvatarUpdateState.Error(it.message ?: "Delete failed") }
            )
        }
    }

    fun resetAvatarUpdateState() {
        _avatarUpdateState.value = AvatarUpdateState.Idle
    }

    companion object {
        val Factory: ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {
                val application = checkNotNull(extras[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY])
                return ProfileViewModel(AppModule.provideProfileRepository(application)) as T
            }
        }
    }
}
