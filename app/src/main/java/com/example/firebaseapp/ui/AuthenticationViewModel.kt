package com.example.firebaseapp.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.firebaseapp.data.AuthenticationRepository
import com.example.firebaseapp.data.model.User

class AuthenticationViewModel(private val authenticationRepository: AuthenticationRepository) :
    ViewModel() {
    private var signingStatus: Boolean = false

    fun isSigningIn(): Boolean = signingStatus
    fun updateSigningStatus(value: Boolean) {
        signingStatus = value
    }

    fun getCurrentUser(): User? = authenticationRepository.getCurrentUser()
}

class AuthenticationViewModelFactory(private val authenticationRepository: AuthenticationRepository) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AuthenticationViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return AuthenticationViewModel(authenticationRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
