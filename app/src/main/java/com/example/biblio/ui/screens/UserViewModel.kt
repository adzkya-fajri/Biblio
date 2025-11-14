package com.example.biblio.viewmodel

import androidx.lifecycle.ViewModel
import com.example.biblio.data.repository.UserProfile
import com.example.biblio.data.repository.UserRepository
import kotlinx.coroutines.flow.StateFlow

class UserViewModel(
    private val userRepository: UserRepository
) : ViewModel() {

    val userProfile: StateFlow<UserProfile> = userRepository.userProfile

    fun updateName(name: String) {
        userRepository.updateName(name)
    }

    fun updatePhoto(photoUrl: String) {
        userRepository.updatePhoto(photoUrl)
    }

    fun updateFontStyle(fontStyle: String) {
        userRepository.updateFontStyle(fontStyle)
    }

    fun updatePassword(oldPassword: String, newPassword: String): Boolean {
        return userRepository.updatePassword(oldPassword, newPassword)
    }
}
