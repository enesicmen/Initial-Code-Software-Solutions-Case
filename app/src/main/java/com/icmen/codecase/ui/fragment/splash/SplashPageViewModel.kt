package com.icmen.codecase.ui.fragment.splash

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.icmen.codecase.data.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SplashPageViewModel @Inject constructor(
    private val auth: FirebaseAuth
) : ViewModel() {

    private val _isUserAuthenticatedLiveData = MutableLiveData<Resource<Boolean>>()
    val isUserAuthenticatedLiveData: LiveData<Resource<Boolean>> = _isUserAuthenticatedLiveData

    fun checkUserAuthentication() {
        _isUserAuthenticatedLiveData.value = Resource.Loading()
        val currentUser = auth.currentUser
        if (currentUser != null) {
            _isUserAuthenticatedLiveData.value = Resource.Success(true)
        } else {
            _isUserAuthenticatedLiveData.value = Resource.Error("User not authenticated")
        }
    }
}
