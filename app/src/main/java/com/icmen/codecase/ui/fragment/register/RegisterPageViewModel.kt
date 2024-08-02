package com.icmen.codecase.ui.fragment.register

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.icmen.codecase.data.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class RegisterPageViewModel @Inject constructor(
    private val auth: FirebaseAuth,
    private val db: FirebaseFirestore
) : ViewModel() {

    private val _registrationResult = MutableLiveData<Resource<FirebaseUser>>()
    val registrationResult: LiveData<Resource<FirebaseUser>> = _registrationResult

    private val _progressVisibility = MutableLiveData<Boolean>()
    val progressVisibility: LiveData<Boolean> = _progressVisibility

    fun registerUser(name: String, surname: String, email: String, address: String, password: String) {
        if (name.isEmpty() || surname.isEmpty() || address.isEmpty() || email.isEmpty() || password.isEmpty()) {
            _registrationResult.value = Resource.Error("Please fill in all fields")
            return
        }

        if (password.length < 6) {
            _registrationResult.value = Resource.Error("Password must be at least 6 characters long")
            return
        }

        _progressVisibility.value = true
        _registrationResult.value = Resource.Loading()

        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val user = auth.currentUser
                    user?.let {
                        saveUserInfo(it.uid, name, surname, email, address)
                    }
                } else {
                    _progressVisibility.value = false
                    _registrationResult.value = Resource.Error(task.exception?.message ?: "Registration failed")
                }
            }
    }

    private fun saveUserInfo(userId: String, name: String, surname: String, email: String, address: String) {
        val userInfo = hashMapOf(
            "userId" to userId,
            "name" to name,
            "surname" to surname,
            "email" to email,
            "address" to address
        )
        db.collection("users").document(userId).set(userInfo)
            .addOnSuccessListener {
                _progressVisibility.value = false
                _registrationResult.value = Resource.Success(auth.currentUser!!)
            }
            .addOnFailureListener { e ->
                _progressVisibility.value = false
                _registrationResult.value = Resource.Error(e.message ?: "Failed to save user info")
            }
    }
}
