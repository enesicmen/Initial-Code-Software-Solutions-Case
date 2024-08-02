package com.icmen.codecase.ui.fragment.register

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseUser
import com.icmen.codecase.R
import com.icmen.codecase.data.Resource
import com.icmen.codecase.databinding.FragmentRegisterBinding
import com.icmen.codecase.ui.base.BaseFragment
import com.icmen.codecase.ui.fragment.custom.CustomDialogWithOneButtonFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class RegisterPageFragment : BaseFragment<FragmentRegisterBinding, RegisterPageViewModel>() {

    private var selectedImageUri: Uri? = null
    private val registerPageViewModel: RegisterPageViewModel by viewModels()

    override fun setViewBinding(): FragmentRegisterBinding {
        return FragmentRegisterBinding.inflate(layoutInflater)
    }

    override fun setViewModelClass() = RegisterPageViewModel::class.java

    override fun initView(savedInstanceState: Bundle?) {
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

            registerPageViewModel.registerUser(name, surname, email, address, password, selectedImageUri)
        }

        goToLoginPage()
        observeViewModel()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE_IMAGE_PICK && resultCode == Activity.RESULT_OK && data != null) {
            selectedImageUri = data.data
            getViewBinding()?.profileImageView?.setImageURI(selectedImageUri)
        }
    }

    private fun observeViewModel() {
        registerPageViewModel.registrationResult.observe(viewLifecycleOwner, Observer { resource ->
            when (resource) {
                is Resource.Loading -> {
                    getViewBinding()?.progressBar?.visibility = View.VISIBLE
                }
                is Resource.Success -> {
                    getViewBinding()?.progressBar?.visibility = View.GONE
                    updateUI(resource.data)
                }
                is Resource.Error -> {
                    getViewBinding()?.progressBar?.visibility = View.GONE
                    val title = getString(R.string.error)
                    setOneButtonDialog(title, resource.error ?: "Registration failed")
                }
            }
        })

        registerPageViewModel.progressVisibility.observe(viewLifecycleOwner, Observer { isVisible ->
            getViewBinding()?.progressBar?.visibility = if (isVisible) View.VISIBLE else View.GONE
        })
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

    private fun setOneButtonDialog(title: String, message: String) {
        val dialog = CustomDialogWithOneButtonFragment.newInstance(title, message)
        dialog.onOkClicked = {}
        dialog.show(requireActivity().supportFragmentManager, "customDialog")
    }
}
