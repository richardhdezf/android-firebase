package com.example.firebaseapp.data.model

import com.google.firebase.firestore.DocumentId

data class User(
    @DocumentId
    var id: String,
    var displayName: String,
    var email: String
)
