package com.icmen.codecase.ui.fragment.editProfilePage

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Toast
import com.bumptech.glide.Glide
import com.icmen.codecase.R
import com.icmen.codecase.databinding.FragmentEditProfileBinding
import com.icmen.codecase.ui.base.BaseFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class EditProfilePageFragment : BaseFragment<FragmentEditProfileBinding, EditProfilePageViewModel>() {

    private var selectedImageUri: Uri? = null

    override fun setViewBinding(): FragmentEditProfileBinding =
        FragmentEditProfileBinding.inflate(layoutInflater)

    override fun setViewModelClass() = EditProfilePageViewModel::class.java

    override fun initView(savedInstanceState: Bundle?) {
        loadUserProfile()

        getViewBinding()?.profileImageView?.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            startActivityForResult(intent, REQUEST_CODE_IMAGE_PICK)
        }

        getViewBinding()?.btnUpdate?.setOnClickListener {
            val name = getViewBinding()?.etName?.text.toString()
            val surname = getViewBinding()?.etSurname?.text.toString()
            val address = getViewBinding()?.etAddress?.text.toString()

            val updates = mutableMapOf<String, Any>()
            val currentUserInfo = getViewModel()!!.getCurrentUserInfo()

            if (name != currentUserInfo["name"]) {
                updates["name"] = name
            }
            if (surname != currentUserInfo["surname"]) {
                updates["surname"] = surname
            }
            if (address != currentUserInfo["address"]) {
                updates["address"] = address
            }

            if (selectedImageUri != null) {
                getViewModel()?.uploadProfileImage(selectedImageUri!!,
                    onSuccess = { imageUrl ->
                        updates["profileImageUrl"] = imageUrl
                        if (updates.isNotEmpty()) {
                            updateUserProfile(updates)
                        }
                    },
                    onError = { message ->
                        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
                    })
            } else {
                if (updates.isNotEmpty()) {
                    updateUserProfile(updates)
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE_IMAGE_PICK && resultCode == Activity.RESULT_OK && data != null) {
            selectedImageUri = data.data
            getViewBinding()?.profileImageView?.setImageURI(selectedImageUri)
        }
    }

    private fun loadUserProfile() {
        getViewModel()?.loadUserProfile(
            onProfileLoaded = { userInfo ->
                getViewBinding()?.etName?.setText(userInfo["name"] as? String)
                getViewBinding()?.etSurname?.setText(userInfo["surname"] as? String)
                getViewBinding()?.etAddress?.setText(userInfo["address"] as? String)

                val profileImageUrl = userInfo["profileImageUrl"] as? String
                if (!profileImageUrl.isNullOrEmpty()) {
                    getViewBinding()?.profileImageView?.let { imageView ->
                        Glide.with(this).load(profileImageUrl).into(imageView)
                    }
                }
            },
            onError = { message ->
                Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
            }
        )
    }

    private fun updateUserProfile(updates: Map<String, Any>) {
        getViewModel()?.updateUserProfile(updates,
            onSuccess = {
                Toast.makeText(requireContext(), getString(R.string.profile_updated_successfully), Toast.LENGTH_SHORT).show()
            },
            onError = { message ->
                Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
            }
        )
    }

    companion object {
        private const val REQUEST_CODE_IMAGE_PICK = 1001
    }
}
