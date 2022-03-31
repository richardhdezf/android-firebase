package com.example.firebaseapp

import android.app.Application
import com.example.firebaseapp.data.AuthenticationRepository
import com.example.firebaseapp.data.RestaurantsRepository
import com.google.firebase.ktx.Firebase
import com.google.firebase.auth.ktx.auth
import com.firebase.ui.auth.AuthUI
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore

class MainApplication : Application() {
    companion object {
        lateinit var authUI: AuthUI
        lateinit var authenticationRepository: AuthenticationRepository
        lateinit var restaurantsRepository: RestaurantsRepository
    }

    override fun onCreate() {
        super.onCreate()
        val firebaseAuthentication = Firebase.auth
        authenticationRepository = AuthenticationRepository(firebaseAuthentication)
        authUI = AuthUI.getInstance()

        // Enable Firestore logging
        FirebaseFirestore.setLoggingEnabled(true)
        val firestore = Firebase.firestore
        restaurantsRepository = RestaurantsRepository(firestore)
    }
}
