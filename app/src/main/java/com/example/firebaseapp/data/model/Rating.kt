package com.example.firebaseapp.data.model

import android.text.TextUtils
import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.ServerTimestamp
import java.util.Date

data class Rating(
    @DocumentId
    var id: String? = null,
    var userId: String? = null,
    var userName: String? = null,
    var rating: Double = 0.toDouble(),
    var text: String? = null,
    @ServerTimestamp var timestamp: Date? = null
) {

    constructor(
        id: String,
        userId: String,
        userDisplayName: String,
        userEmail: String,
        rating: Double,
        text: String
    ) : this() {
        this.id = id
        this.userId = userId
        this.userName = userDisplayName
        if (TextUtils.isEmpty(this.userName)) {
            this.userName = userEmail
        }

        this.rating = rating
        this.text = text
    }
}
