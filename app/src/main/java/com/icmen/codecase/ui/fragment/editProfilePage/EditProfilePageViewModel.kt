package com.icmen.codecase.ui.fragment.editProfilePage

import android.net.Uri
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class EditProfilePageViewModel @Inject constructor(
    private val auth: FirebaseAuth,
    private val db: FirebaseFirestore,
    private val storage: FirebaseStorage
) : ViewModel() {

    private var currentUserInfo: MutableMap<String, Any> = mutableMapOf()

    fun loadUserProfile(onProfileLoaded: (Map<String, Any>) -> Unit, onError: (String) -> Unit) {
        val user = auth.currentUser
        user?.let {
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

                        onProfileLoaded(currentUserInfo)
                    }
                }
                .addOnFailureListener { e ->
                    onError("Failed to load profile: ${e.message}")
                }
        }
    }

    fun uploadProfileImage(selectedImageUri: Uri, onSuccess: (String) -> Unit, onError: (String) -> Unit) {
        val user = auth.currentUser
        user?.let {
            val userId = it.uid
            val storageRef = storage.reference.child("images/profileImages/$userId.jpg")
            storageRef.putFile(selectedImageUri).addOnSuccessListener {
                storageRef.downloadUrl.addOnSuccessListener { downloadUri ->
                    onSuccess(downloadUri.toString())
                }.addOnFailureListener { e ->
                    onError("Image upload failed: ${e.message}")
                }
            }.addOnFailureListener { e ->
                onError("Image upload failed: ${e.message}")
            }
        }
    }

    fun updateUserProfile(updates: Map<String, Any>, onSuccess: () -> Unit, onError: (String) -> Unit) {
        val user = auth.currentUser
        user?.let {
            val userId = it.uid
            db.collection("users").document(userId).update(updates)
                .addOnSuccessListener {
                    onSuccess()
                }
                .addOnFailureListener { e ->
                    onError("Failed to update profile: ${e.message}")
                }
        }
    }

    fun getCurrentUserInfo(): Map<String, Any> {
        return currentUserInfo
    }
}
