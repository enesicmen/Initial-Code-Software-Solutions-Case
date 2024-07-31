package com.icmen.ecommerceapplication.ui.fragment

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Toast
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.icmen.ecommerceapplication.R
import com.icmen.ecommerceapplication.databinding.FragmentEditProfileBinding
import com.icmen.ecommerceapplication.ui.base.BaseFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class EditProfilePageFragment : BaseFragment<FragmentEditProfileBinding>() {

    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore
    private lateinit var storage: FirebaseStorage

    private var selectedImageUri: Uri? = null
    private var currentUserInfo: MutableMap<String, Any> = mutableMapOf()

    override fun setViewBinding(): FragmentEditProfileBinding =
        FragmentEditProfileBinding.inflate(layoutInflater)

    override fun initView(savedInstanceState: Bundle?) {
        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()
        storage = FirebaseStorage.getInstance()

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
                uploadProfileImage { imageUrl ->
                    updates["profileImageUrl"] = imageUrl
                    if (updates.isNotEmpty()) {
                        updateUserProfile(updates)
                    }
                }
            } else {
                if (updates.isNotEmpty()) {
                    updateUserProfile(updates)
                } else {
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
        val user = auth.currentUser
        user?.let {
            db.collection("users").document(it.uid).get()
                .addOnSuccessListener { document ->
                    if (document != null) {
                        val name = document.getString("name")
                        val surname = document.getString("surname")
                        val address = document.getString("address")
                        val profileImageUrl = document.getString("profileImageUrl")

                        currentUserInfo["name"] = name ?: ""
                        currentUserInfo["surname"] = surname ?: ""
                        currentUserInfo["address"] = address ?: ""
                        currentUserInfo["profileImageUrl"] = profileImageUrl ?: ""

                        getViewBinding()?.etName?.setText(name)
                        getViewBinding()?.etSurname?.setText(surname)
                        getViewBinding()?.etAddress?.setText(address)
                        if (!profileImageUrl.isNullOrEmpty()) {
                            getViewBinding()?.profileImageView?.let { it1 ->
                                Glide.with(this).load(profileImageUrl).into(
                                    it1
                                )
                            }
                        }
                    }
                }
                .addOnFailureListener { e ->
                    Toast.makeText(requireContext(), "Failed to load profile: ${e.message}", Toast.LENGTH_SHORT).show()
                }
        }
    }

    private fun uploadProfileImage(onSuccess: (String) -> Unit) {
        val user = auth.currentUser
        user?.let {
            val userId = it.uid
            val storageRef = storage.reference.child("images/profileImages/$userId.jpg")
            selectedImageUri?.let { uri ->
                storageRef.putFile(uri).addOnSuccessListener {
                    storageRef.downloadUrl.addOnSuccessListener { downloadUri ->
                        onSuccess(downloadUri.toString())
                    }
                }.addOnFailureListener { e ->
                    Toast.makeText(requireContext(), "Image upload failed: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun updateUserProfile(updates: Map<String, Any>) {
        val user = auth.currentUser
        user?.let {
            val userId = it.uid
            db.collection("users").document(userId).update(updates)
                .addOnSuccessListener {
                    Toast.makeText(requireContext(), getString(R.string.profile_updated_successfully), Toast.LENGTH_SHORT).show()
                }
                .addOnFailureListener { e ->
                    Toast.makeText(requireContext(), "Failed to update profile: ${e.message}", Toast.LENGTH_SHORT).show()
                }
        }
    }

    companion object {
        private const val REQUEST_CODE_IMAGE_PICK = 1001
    }
}
