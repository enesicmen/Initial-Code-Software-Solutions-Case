package com.icmen.codecase.ui.fragment.splash

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SplashPageViewModel @Inject constructor() : ViewModel() {

    private val _isUserAuthenticatedLiveData = MutableLiveData<Boolean>()
    val isUserAuthenticatedLiveData: LiveData<Boolean> = _isUserAuthenticatedLiveData

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    fun checkUserAuthentication() {
        val currentUser = auth.currentUser
        _isUserAuthenticatedLiveData.value = currentUser != null
    }
}
