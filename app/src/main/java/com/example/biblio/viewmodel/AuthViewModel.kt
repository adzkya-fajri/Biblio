package com.example.biblio.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AuthViewModel : ViewModel() {
    private val _authState = MutableStateFlow<AuthState>(AuthState.Idle)
    val authState: StateFlow<AuthState> = _authState.asStateFlow()

    fun login(email: String, password: String) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            delay(1500) // Simulasi jaringan

            // Logic sederhana (ganti dengan API call nyata)
            if (email.isNotBlank() && password.length >= 6) {
                _authState.value = AuthState.Success
            } else {
                _authState.value = AuthState.Error("Email atau password salah")
            }
        }
    }
    fun logout() {
        _authState.value = AuthState.Idle // Reset state
    }
}