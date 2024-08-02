package com.icmen.codecase.ui.fragment.register

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.icmen.codecase.data.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class RegisterPageViewModel @Inject constructor(
    private val auth: FirebaseAuth,
    private val db: FirebaseFirestore,
    private val storage: FirebaseStorage
) : ViewModel() {

    private val _registrationResult = MutableLiveData<Resource<FirebaseUser>>()
    val registrationResult: LiveData<Resource<FirebaseUser>> = _registrationResult

    private val _progressVisibility = MutableLiveData<Boolean>()
    val progressVisibility: LiveData<Boolean> = _progressVisibility

    fun registerUser(name: String, surname: String, email: String, address: String, password: String, selectedImageUri: Uri?) {
        if (name.isEmpty() || surname.isEmpty() || address.isEmpty() || email.isEmpty() || password.isEmpty()) {
            _registrationResult.value = Resource.Error("Please fill in all fields")
            return
        }

        if (selectedImageUri == null) {
            _registrationResult.value = Resource.Error("Please select an image")
            return
        }

        _progressVisibility.value = true
        _registrationResult.value = Resource.Loading()

        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val user = auth.currentUser
                    user?.let {
                        uploadProfileImage(it.uid, name, surname, email, address, selectedImageUri)
                    }
                } else {
                    _progressVisibility.value = false
                    _registrationResult.value = Resource.Error(task.exception?.message ?: "Registration failed")
                }
            }
    }

    private fun uploadProfileImage(userId: String, name: String, surname: String, email: String, address: String, selectedImageUri: Uri) {
        val storageRef = storage.reference.child("images/profileImages/$userId.jpg")
        storageRef.putFile(selectedImageUri)
            .addOnSuccessListener {
                storageRef.downloadUrl.addOnSuccessListener { uri ->
                    saveUserInfo(userId, name, surname, email, address, uri.toString())
                }
            }.addOnFailureListener { e ->
                _progressVisibility.value = false
                _registrationResult.value = Resource.Error(e.message ?: "Image upload failed")
            }
    }

    private fun saveUserInfo(userId: String, name: String, surname: String, email: String, address: String, imageUrl: String?) {
        val userInfo = hashMapOf(
            "userId" to userId,
            "name" to name,
            "surname" to surname,
            "email" to email,
            "address" to address,
            "profileImageUrl" to imageUrl
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
