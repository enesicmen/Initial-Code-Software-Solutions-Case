package com.icmen.codecase.ui.fragment.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class LoginPageViewModel @Inject constructor(
    private val auth: FirebaseAuth
) : ViewModel() {

    private val _loginResult = MutableLiveData<Result<Boolean>>()
    val loginResult: LiveData<Result<Boolean>> = _loginResult

    private val _progressVisibility = MutableLiveData<Boolean>()
    val progressVisibility: LiveData<Boolean> = _progressVisibility

    fun loginUser(email: String, password: String) {
        if (email.isEmpty() || password.isEmpty()) {
            _loginResult.value = Result.failure(Exception("Please fill in all fields"))
            return
        }

        _progressVisibility.value = true

        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                _progressVisibility.value = false
                if (task.isSuccessful) {
                    _loginResult.value = Result.success(true)
                } else {
                    _loginResult.value = Result.failure(task.exception ?: Exception("Login failed"))
                }
            }
    }
}
