package com.example.firebaseapp.data

import com.example.firebaseapp.data.model.User
import com.google.firebase.auth.FirebaseAuth

class AuthenticationRepository(private val firebaseAuthentication: FirebaseAuth) {
    fun getCurrentUser(): User? {
        val firebaseUser = firebaseAuthentication.currentUser
        return if (firebaseUser == null) null else User(
            firebaseUser.uid,
            firebaseUser.displayName ?: "",
            firebaseUser.email ?: ""
        )
    }
}