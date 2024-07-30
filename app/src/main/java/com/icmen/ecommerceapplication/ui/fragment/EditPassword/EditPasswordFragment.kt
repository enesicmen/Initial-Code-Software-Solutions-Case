package com.icmen.ecommerceapplication.ui.fragment

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.icmen.ecommerceapplication.databinding.FragmentEditPasswordBinding
import com.icmen.ecommerceapplication.ui.base.BaseFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class EditPasswordFragment : BaseFragment<FragmentEditPasswordBinding>() {

    private lateinit var auth: FirebaseAuth

    override fun setViewBinding(): FragmentEditPasswordBinding =
        FragmentEditPasswordBinding.inflate(layoutInflater)

    override fun initView(savedInstanceState: Bundle?) {
        auth = FirebaseAuth.getInstance()
        getViewBinding()?.btnChangePassword?.setOnClickListener { changePassword() }
    }

    private fun changePassword() {
        val currentPassword = getViewBinding()?.etCurrentPassword?.text.toString()
        val newPassword = getViewBinding()?.etNewPassword?.text.toString()
        val confirmNewPassword = getViewBinding()?.etConfirmNewPassword?.text.toString()

        // Şifre alanlarının boş olup olmadığını kontrol et
        if (currentPassword.isEmpty() || newPassword.isEmpty() || confirmNewPassword.isEmpty()) {
            Toast.makeText(requireContext(), "Lütfen tüm alanları doldurun", Toast.LENGTH_SHORT).show()
            return
        }

        // Yeni şifre ve tekrar şifre eşleşiyor mu kontrol et
        if (newPassword != confirmNewPassword) {
            Toast.makeText(requireContext(), "Yeni şifreler eşleşmiyor", Toast.LENGTH_SHORT).show()
            return
        }

        // Kullanıcıdan mevcut şifre ile giriş yapmasını iste
        val user = auth.currentUser
        user?.let {
            // Mevcut şifre ile kullanıcıyı doğrula
            val credential = EmailAuthProvider.getCredential(user.email!!, currentPassword)

            // Şifreyi değiştirmeden önce kimlik doğrulamasını kontrol et
            user.reauthenticate(credential).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // Kimlik doğrulaması başarılı ise yeni şifreyi değiştir
                    user.updatePassword(newPassword).addOnCompleteListener { updateTask ->
                        if (updateTask.isSuccessful) {
                            Toast.makeText(requireContext(), "Şifre başarıyla değiştirildi", Toast.LENGTH_SHORT).show()
                        } else {
                            Toast.makeText(requireContext(), "Şifre değiştirilemedi: ${updateTask.exception?.message}", Toast.LENGTH_SHORT).show()
                            Log.e("EditPasswordFragment", "Şifre değiştirilemedi: ${updateTask.exception?.message}")

                        }
                    }
                } else {
                    Toast.makeText(requireContext(), "Mevcut şifre yanlış", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}
