package com.icmen.codecase.ui.fragment.editProfilePage

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.Toast
import androidx.fragment.app.viewModels
import com.bumptech.glide.Glide
import com.icmen.codecase.R
import com.icmen.codecase.data.Resource
import com.icmen.codecase.databinding.FragmentEditProfileBinding
import com.icmen.codecase.ui.base.BaseFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class EditProfilePageFragment : BaseFragment<FragmentEditProfileBinding, EditProfilePageViewModel>() {

    private val viewModel: EditProfilePageViewModel by viewModels()
    private var selectedImageUri: Uri? = null

    override fun setViewBinding(): FragmentEditProfileBinding =
        FragmentEditProfileBinding.inflate(layoutInflater)

    override fun setViewModelClass() = EditProfilePageViewModel::class.java

    override fun initView(savedInstanceState: Bundle?) {
        observeViewModel()
        viewModel.loadUserProfile()

        getViewBinding()?.profileImageView?.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            startActivityForResult(intent, REQUEST_CODE_IMAGE_PICK)
        }

        getViewBinding()?.btnUpdate?.setOnClickListener {
            val name = getViewBinding()?.etName?.text.toString()
            val surname = getViewBinding()?.etSurname?.text.toString()
            val address = getViewBinding()?.etAddress?.text.toString()

            val updates = mutableMapOf<String, Any>()
            val currentUserInfo = viewModel.getCurrentUserInfo()

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
                viewModel.uploadProfileImage(selectedImageUri!!)
            } else {
                if (updates.isNotEmpty()) {
                    viewModel.updateUserProfile(updates)
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

    private fun observeViewModel() {
        viewModel.userProfileLiveData.observe(viewLifecycleOwner) { resource ->
            when (resource) {
                is Resource.Loading -> {
                    getViewBinding()?.progressBar?.visibility = View.VISIBLE
                }
                is Resource.Success -> {
                    getViewBinding()?.progressBar?.visibility = View.GONE
                    val userInfo = resource.data!!
                    getViewBinding()?.etName?.setText(userInfo["name"] as? String)
                    getViewBinding()?.etSurname?.setText(userInfo["surname"] as? String)
                    getViewBinding()?.etAddress?.setText(userInfo["address"] as? String)

                    val profileImageUrl = userInfo["profileImageUrl"] as? String
                    if (!profileImageUrl.isNullOrEmpty()) {
                        getViewBinding()?.profileImageView?.let { imageView ->
                            Glide.with(this).load(profileImageUrl).into(imageView)
                        }
                    }
                }
                is Resource.Error -> {
                    getViewBinding()?.progressBar?.visibility = View.GONE
                    Toast.makeText(requireContext(), resource.error ?: "Bilinmeyen bir hata oluştu", Toast.LENGTH_SHORT).show()
                }
            }
        }

        viewModel.uploadImageLiveData.observe(viewLifecycleOwner) { resource ->
            when (resource) {
                is Resource.Loading -> {
                    getViewBinding()?.progressBar?.visibility = View.VISIBLE
                }
                is Resource.Success -> {
                    getViewBinding()?.progressBar?.visibility = View.GONE
                    val imageUrl = resource.data!!
                    val updates = mutableMapOf<String, Any>()
                    updates["profileImageUrl"] = imageUrl

                    val name = getViewBinding()?.etName?.text.toString()
                    val surname = getViewBinding()?.etSurname?.text.toString()
                    val address = getViewBinding()?.etAddress?.text.toString()

                    val currentUserInfo = viewModel.getCurrentUserInfo()
                    if (name != currentUserInfo["name"]) {
                        updates["name"] = name
                    }
                    if (surname != currentUserInfo["surname"]) {
                        updates["surname"] = surname
                    }
                    if (address != currentUserInfo["address"]) {
                        updates["address"] = address
                    }

                    if (updates.isNotEmpty()) {
                        viewModel.updateUserProfile(updates)
                    }
                }
                is Resource.Error -> {
                    getViewBinding()?.progressBar?.visibility = View.GONE
                    Toast.makeText(requireContext(), resource.error ?: "Bilinmeyen bir hata oluştu", Toast.LENGTH_SHORT).show()
                }
            }
        }

        viewModel.updateProfileLiveData.observe(viewLifecycleOwner) { resource ->
            when (resource) {
                is Resource.Loading -> {
                    getViewBinding()?.progressBar?.visibility = View.VISIBLE
                }
                is Resource.Success -> {
                    getViewBinding()?.progressBar?.visibility = View.GONE
                    Toast.makeText(requireContext(), getString(R.string.profile_updated_successfully), Toast.LENGTH_SHORT).show()
                }
                is Resource.Error -> {
                    getViewBinding()?.progressBar?.visibility = View.GONE
                    Toast.makeText(requireContext(), resource.error ?: "Bilinmeyen bir hata oluştu", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    companion object {
        private const val REQUEST_CODE_IMAGE_PICK = 1001
    }
}
