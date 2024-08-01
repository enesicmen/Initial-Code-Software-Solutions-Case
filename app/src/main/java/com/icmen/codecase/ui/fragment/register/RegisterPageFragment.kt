package com.icmen.codecase.ui.fragment.register

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.icmen.codecase.R
import com.icmen.codecase.databinding.FragmentRegisterBinding
import com.icmen.codecase.ui.base.BaseFragment

class RegisterPageFragment : BaseFragment<FragmentRegisterBinding>() {

    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore
    private lateinit var storage: FirebaseStorage
    private var selectedImageUri: Uri? = null
    override fun setViewBinding(): FragmentRegisterBinding {
        return FragmentRegisterBinding.inflate(layoutInflater)
    }

    override fun initView(savedInstanceState: Bundle?) {
        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()
        storage = FirebaseStorage.getInstance()

        getViewBinding()?.profileImageView?.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            startActivityForResult(intent, REQUEST_CODE_IMAGE_PICK)
        }

        getViewBinding()?.btnRegister?.setOnClickListener {
            val name = getViewBinding()?.etName?.text.toString()
            val surname = getViewBinding()?.etSurname?.text.toString()
            val address = getViewBinding()?.etAddress?.text.toString()
            val email = getViewBinding()?.etEmail?.text.toString()
            val password = getViewBinding()?.etPassword?.text.toString()

            if (name.isEmpty() || surname.isEmpty() || address.isEmpty() || email.isEmpty() || password.isEmpty()) {
                Toast.makeText(requireContext(), getString(R.string.please_fill_in_all_fields), Toast.LENGTH_SHORT).show()
            } else if (selectedImageUri == null) {
                Toast.makeText(requireContext(), getString(R.string.please_select_image), Toast.LENGTH_SHORT).show()
            } else {
                getViewBinding()?.fmProgress?.visibility = View.VISIBLE
                registerUser(name, surname, email, address, password)
            }
        }
        goToLoginPage()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE_IMAGE_PICK && resultCode == Activity.RESULT_OK && data != null) {
            selectedImageUri = data.data
            getViewBinding()?.profileImageView?.setImageURI(selectedImageUri)
        }
    }

    private fun registerUser(name: String, surname: String, email: String, address: String, password: String) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(requireActivity()) { task ->
                if (task.isSuccessful) {
                    val user: FirebaseUser? = auth.currentUser
                    user?.let {
                        uploadProfileImage(it.uid, name, surname, email, address)
                    }
                } else {
                    getViewBinding()?.fmProgress?.visibility = View.GONE
                    val errorMessage = getString(R.string.registration_failed) + ": " + task.exception?.message
                    Toast.makeText(requireContext(), errorMessage, Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun uploadProfileImage(userId: String, name: String, surname: String, email: String, address: String) {
        val storageRef = storage.reference.child("images/profileImages/$userId.jpg")
        selectedImageUri?.let {
            storageRef.putFile(it).addOnSuccessListener {
                storageRef.downloadUrl.addOnSuccessListener { uri ->
                    saveUserInfo(userId, name, surname, email, address, uri.toString())
                }
            }.addOnFailureListener { e ->
                getViewBinding()?.fmProgress?.visibility = View.GONE
                val errorMessage = getString(R.string.image_upload_failed) + ": " + e.message
                Toast.makeText(requireContext(), errorMessage, Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun saveUserInfo(userId: String, name: String, surname: String, email: String, address: String, imageUrl: String?) {
        val userInfo = hashMapOf(
            "userId" to userId,
            "name" to name,
            "surname" to surname,
            "email" to email,
            "address" to address,
            "profileImageUrl" to imageUrl
        )
        db.collection("users").document(userId).set(userInfo)
            .addOnSuccessListener {
                getViewBinding()?.fmProgress?.visibility = View.GONE
                Toast.makeText(requireContext(), getString(R.string.registration_successful), Toast.LENGTH_SHORT).show()
                updateUI(auth.currentUser)
            }
            .addOnFailureListener { e ->
                getViewBinding()?.fmProgress?.visibility = View.GONE
                val errorMessage = getString(R.string.adding_to_firestore_failed) + ": " + e.message
                Toast.makeText(requireContext(), errorMessage, Toast.LENGTH_SHORT).show()
            }
    }

    private fun updateUI(user: FirebaseUser?) {
        val login = RegisterPageFragmentDirections.actionRegisterPageFragmentToProductsPageFragment()
        findNavController().navigate(login)
    }

    private fun goToLoginPage() {
        getViewBinding()?.tvLogin?.setOnClickListener {
            val login = RegisterPageFragmentDirections.actionRegisterPageFragmentToLoginPageFragment()
            findNavController().navigate(login)
        }
    }

    companion object {
        private const val REQUEST_CODE_IMAGE_PICK = 1001
    }
}
