package com.icmen.codecase.ui.fragment.profile

import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.FirebaseFirestore
import com.icmen.codecase.data.model.User

class ProfilePageViewModel : ViewModel() {
    private val firestore = FirebaseFirestore.getInstance()

    fun fetchUserData(userId: String, callback: (User?) -> Unit) {
        firestore.collection("users").document(userId)
            .get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    val userModel = document.toObject(User::class.java)
                    callback(userModel)
                } else {
                    callback(null)
                }
            }
            .addOnFailureListener { exception ->
                callback(null)
            }
    }
}
