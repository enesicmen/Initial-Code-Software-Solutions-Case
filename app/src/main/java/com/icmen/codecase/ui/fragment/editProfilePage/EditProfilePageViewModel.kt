package com.icmen.codecase.ui.fragment.editProfilePage

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.icmen.codecase.data.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class EditProfilePageViewModel @Inject constructor(
    private val auth: FirebaseAuth,
    private val db: FirebaseFirestore,
    private val storage: FirebaseStorage
) : ViewModel() {

    private var currentUserInfo: MutableMap<String, Any> = mutableMapOf()

    private val _userProfileLiveData = MutableLiveData<Resource<Map<String, Any>>>()
    val userProfileLiveData: LiveData<Resource<Map<String, Any>>> = _userProfileLiveData

    private val _uploadImageLiveData = MutableLiveData<Resource<String>>()
    val uploadImageLiveData: LiveData<Resource<String>> = _uploadImageLiveData

    private val _updateProfileLiveData = MutableLiveData<Resource<Void>>()
    val updateProfileLiveData: LiveData<Resource<Void>> = _updateProfileLiveData

    fun loadUserProfile() {
        val user = auth.currentUser
        user?.let {
            _userProfileLiveData.value = Resource.Loading()

            db.collection("users").document(it.uid).get()
                .addOnSuccessListener { document ->
                    if (document != null) {
                        val name = document.getString("name") ?: ""
                        val surname = document.getString("surname") ?: ""
                        val address = document.getString("address") ?: ""
                        val profileImageUrl = document.getString("profileImageUrl") ?: ""

                        currentUserInfo["name"] = name
                        currentUserInfo["surname"] = surname
                        currentUserInfo["address"] = address
                        currentUserInfo["profileImageUrl"] = profileImageUrl

                        _userProfileLiveData.value = Resource.Success(currentUserInfo)
                    }
                }
                .addOnFailureListener { e ->
                    _userProfileLiveData.value = Resource.Error("Failed to load profile: ${e.message}")
                }
        }
    }

    fun uploadProfileImage(selectedImageUri: Uri) {
        val user = auth.currentUser
        user?.let {
            _uploadImageLiveData.value = Resource.Loading()

            val userId = it.uid
            val storageRef = storage.reference.child("images/profileImages/$userId.jpg")
            storageRef.putFile(selectedImageUri).addOnSuccessListener {
                storageRef.downloadUrl.addOnSuccessListener { downloadUri ->
                    _uploadImageLiveData.value = Resource.Success(downloadUri.toString())
                }.addOnFailureListener { e ->
                    _uploadImageLiveData.value = Resource.Error("Image upload failed: ${e.message}")
                }
            }.addOnFailureListener { e ->
                _uploadImageLiveData.value = Resource.Error("Image upload failed: ${e.message}")
            }
        }
    }

    fun updateUserProfile(updates: Map<String, Any>) {
        val user = auth.currentUser
        user?.let {
            _updateProfileLiveData.value = Resource.Loading()

            val userId = it.uid
            db.collection("users").document(userId).update(updates)
                .addOnSuccessListener {
                    _updateProfileLiveData.value = Resource.Success(null)
                }
                .addOnFailureListener { e ->
                    _updateProfileLiveData.value = Resource.Error("Failed to update profile: ${e.message}")
                }
        }
    }

    fun getCurrentUserInfo(): Map<String, Any> {
        return currentUserInfo
    }
}
