package com.icmen.codecase.ui.fragment.editPassword

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.icmen.codecase.data.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class EditPasswordPageViewModel @Inject constructor(
    private val auth: FirebaseAuth
) : ViewModel() {

    private val _changePasswordLiveData = MutableLiveData<Resource<Void>>()
    val changePasswordLiveData: LiveData<Resource<Void>> = _changePasswordLiveData

    fun changePassword(currentPassword: String, newPassword: String, confirmNewPassword: String) {
        if (currentPassword.isEmpty() || newPassword.isEmpty() || confirmNewPassword.isEmpty()) {
            _changePasswordLiveData.value = Resource.Error("0")
            return
        }
        if (newPassword.length < 6 || confirmNewPassword.length < 6) {
            _changePasswordLiveData.value = Resource.Error("4")
            return
        }
        if (newPassword != confirmNewPassword) {
            _changePasswordLiveData.value = Resource.Error("1")
            return
        }

        val user = auth.currentUser
        user?.let {
            val credential = EmailAuthProvider.getCredential(user.email!!, currentPassword)
            _changePasswordLiveData.value = Resource.Loading()

            user.reauthenticate(credential).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    user.updatePassword(newPassword).addOnCompleteListener { updateTask ->
                        if (updateTask.isSuccessful) {
                            _changePasswordLiveData.value = Resource.Success(null)
                        } else {
                            _changePasswordLiveData.value = Resource.Error("2 ${updateTask.exception?.message}")
                        }
                    }
                } else {
                    _changePasswordLiveData.value = Resource.Error("3")
                }
            }
        }
    }
}
