package com.icmen.codecase.ui.fragment.editPassword

import android.os.Bundle
import android.widget.Toast
import com.icmen.codecase.databinding.FragmentEditPasswordBinding
import com.icmen.codecase.ui.base.BaseFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class EditPasswordPageFragment : BaseFragment<FragmentEditPasswordBinding, EditPasswordPageViewModel>() {

    override fun setViewBinding(): FragmentEditPasswordBinding =
        FragmentEditPasswordBinding.inflate(layoutInflater)

    override fun initView(savedInstanceState: Bundle?) {
        getViewBinding()?.btnChangePassword?.setOnClickListener {
            val currentPassword = getViewBinding()?.etCurrentPassword?.text.toString()
            val newPassword = getViewBinding()?.etNewPassword?.text.toString()
            val confirmNewPassword = getViewBinding()?.etConfirmNewPassword?.text.toString()

            getViewModel()?.changePassword(currentPassword, newPassword, confirmNewPassword,
                onSuccess = {
                    Toast.makeText(requireContext(), "Şifre başarıyla değiştirildi", Toast.LENGTH_SHORT).show()
                },
                onError = { errorMessage ->
                    Toast.makeText(requireContext(), errorMessage, Toast.LENGTH_SHORT).show()
                }
            )
        }
    }

    override fun setViewModelClass() = EditPasswordPageViewModel::class.java
}
