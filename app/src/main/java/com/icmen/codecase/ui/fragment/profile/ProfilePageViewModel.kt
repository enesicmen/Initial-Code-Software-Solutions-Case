package com.icmen.codecase.ui.fragment.profile

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.FirebaseFirestore
import com.icmen.codecase.data.Resource
import com.icmen.codecase.data.model.User

class ProfilePageViewModel : ViewModel() {
    private val firestore = FirebaseFirestore.getInstance()

    private val _userData = MutableLiveData<Resource<User>>()
    val userData: LiveData<Resource<User>> get() = _userData

    fun fetchUserData(userId: String) {
        _userData.value = Resource.Loading()
        firestore.collection("users").document(userId)
            .get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    val userModel = document.toObject(User::class.java)
                    _userData.value = Resource.Success(userModel)
                } else {
                    _userData.value = Resource.Error("Belge bulunamadı")
                }
            }
            .addOnFailureListener { exception ->
                _userData.value = Resource.Error(exception.message ?: "Veri yüklenirken hata oluştu")
            }
    }
}
