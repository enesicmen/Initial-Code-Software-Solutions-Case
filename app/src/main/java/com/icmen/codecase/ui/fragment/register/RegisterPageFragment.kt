package com.icmen.codecase.ui.fragment.register

import android.os.Bundle
import android.view.View
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

    override fun setViewBinding(): FragmentRegisterBinding {
        return FragmentRegisterBinding.inflate(layoutInflater)
    }

    override fun setViewModelClass() = RegisterPageViewModel::class.java

    override fun initView(savedInstanceState: Bundle?) {
        registerUser()
        goToLoginPage()
        observeViewModel()
    }

    private fun registerUser(){
        getViewBinding()?.btnRegister?.setOnClickListener {
            val name = getViewBinding()?.etName?.text.toString()
            val surname = getViewBinding()?.etSurname?.text.toString()
            val address = getViewBinding()?.etAddress?.text.toString()
            val email = getViewBinding()?.etEmail?.text.toString()
            val password = getViewBinding()?.etPassword?.text.toString()

            getViewModel()?.registerUser(name, surname, email, address, password)
        }
    }
    private fun observeViewModel() {
        getViewModel()?.registrationResult?.observe(viewLifecycleOwner, Observer { resource ->
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
                    when (resource.error) {
                        "0" -> {
                            val title = getString(R.string.error)
                            val message = getString(R.string.fill_in_all_fields)
                            setOneButtonDialog(title,message)
                        }
                        "1" -> {
                            val title = getString(R.string.error)
                            val message = getString(R.string.must_be_at_least_six_characters_long)
                            setOneButtonDialog(title,message)
                        }
                        "2" -> {
                            val title = getString(R.string.error)
                            val message = getString(R.string.register_failed)
                            setOneButtonDialog(title,message)
                        }
                    }
                }
            }
        })

        getViewModel()?.progressVisibility?.observe(viewLifecycleOwner, Observer { isVisible ->
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
    private fun setOneButtonDialog(title: String, message: String) {
        val dialog = CustomDialogWithOneButtonFragment.newInstance(title, message)
        dialog.onOkClicked = {}
        dialog.show(requireActivity().supportFragmentManager, "customDialog")
    }
}
