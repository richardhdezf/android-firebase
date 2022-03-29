package com.example.firebaseapp.viewmodel

import androidx.lifecycle.ViewModel
import com.example.firebaseapp.model.Filters

class MainActivityViewModel : ViewModel() {
    var isSigningIn: Boolean = false
    var filters: Filters = Filters.default
}
