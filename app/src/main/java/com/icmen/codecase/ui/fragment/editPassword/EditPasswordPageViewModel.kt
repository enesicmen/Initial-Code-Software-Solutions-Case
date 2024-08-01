package com.icmen.codecase.ui.fragment.editPassword

import android.util.Log
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class EditPasswordPageViewModel @Inject constructor(
    private val auth: FirebaseAuth
) : ViewModel() {

    fun changePassword(currentPassword: String, newPassword: String, confirmNewPassword: String, onSuccess: () -> Unit, onError: (String) -> Unit) {
        if (currentPassword.isEmpty() || newPassword.isEmpty() || confirmNewPassword.isEmpty()) {
            onError("Lütfen tüm alanları doldurun")
            return
        }
        if (newPassword != confirmNewPassword) {
            onError("Yeni şifreler eşleşmiyor")
            return
        }
        val user = auth.currentUser
        user?.let {
            val credential = EmailAuthProvider.getCredential(user.email!!, currentPassword)
            user.reauthenticate(credential).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    user.updatePassword(newPassword).addOnCompleteListener { updateTask ->
                        if (updateTask.isSuccessful) {
                            onSuccess()
                        } else {
                            onError("Şifre değiştirilemedi: ${updateTask.exception?.message}")
                            Log.e("EditPasswordViewModel", "Şifre değiştirilemedi: ${updateTask.exception?.message}")
                        }
                    }
                } else {
                    onError("Mevcut şifre yanlış")
                }
            }
        }
    }
}
