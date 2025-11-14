package com.example.biblio.data.repository

import android.content.Context
import android.content.SharedPreferences
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class UserProfile(
    val name: String = "Andi",
    val email: String = "andi@example.com",
    val photoUrl: String = "",
    val fontStyle: String = "default"  // default, serif, monospace
)

class UserRepository(context: Context) {
    private val prefs: SharedPreferences =
        context.getSharedPreferences("biblio_user", Context.MODE_PRIVATE)

    private val _userProfile = MutableStateFlow(loadUserProfile())
    val userProfile: StateFlow<UserProfile> = _userProfile.asStateFlow()

    private fun loadUserProfile(): UserProfile {
        return UserProfile(
            name = prefs.getString("user_name", "Andi") ?: "Andi",
            email = prefs.getString("user_email", "andi@example.com") ?: "",
            photoUrl = prefs.getString("user_photo", "") ?: "",
            fontStyle = prefs.getString("font_style", "default") ?: "default"
        )
    }

    fun updateName(name: String) {
        val current = _userProfile.value
        _userProfile.value = current.copy(name = name)
        prefs.edit().putString("user_name", name).apply()
    }

    fun updatePhoto(photoUrl: String) {
        val current = _userProfile.value
        _userProfile.value = current.copy(photoUrl = photoUrl)
        prefs.edit().putString("user_photo", photoUrl).apply()
    }

    fun updateFontStyle(fontStyle: String) {
        val current = _userProfile.value
        _userProfile.value = current.copy(fontStyle = fontStyle)
        prefs.edit().putString("font_style", fontStyle).apply()
    }

    fun updatePassword(oldPassword: String, newPassword: String): Boolean {
        val currentPassword = prefs.getString("user_password", "") ?: ""
        return if (currentPassword.isEmpty() || currentPassword == oldPassword) {
            prefs.edit().putString("user_password", newPassword).apply()
            true
        } else {
            false
        }
    }
}
