package com.icmen.codecase.ui.fragment.login

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.icmen.codecase.data.Resource
import com.icmen.codecase.databinding.FragmentLoginBinding
import com.icmen.codecase.ui.base.BaseFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LoginPageFragment : BaseFragment<FragmentLoginBinding, LoginPageViewModel>() {

    private val viewModel: LoginPageViewModel by viewModels()

    override fun setViewBinding(): FragmentLoginBinding {
        return FragmentLoginBinding.inflate(layoutInflater)
    }

    override fun setViewModelClass() = LoginPageViewModel::class.java

    override fun initView(savedInstanceState: Bundle?) {
        getViewBinding()?.btnLogin?.setOnClickListener {
            val email = getViewBinding()?.etEmail?.text.toString().trim()
            val password = getViewBinding()?.etPassword?.text.toString().trim()
            viewModel.loginUser(email, password)
        }

        goToRegisterPage()
        observeViewModel()
    }

    private fun observeViewModel() {
        viewModel.loginResult.observe(viewLifecycleOwner, Observer { resource ->
            when (resource) {
                is Resource.Loading -> {
                    getViewBinding()?.fmProgress?.visibility = View.VISIBLE
                }
                is Resource.Success -> {
                    getViewBinding()?.fmProgress?.visibility = View.GONE
                    val action = LoginPageFragmentDirections.actionLoginPageFragmentToProductsPageFragment()
                    findNavController().navigate(action)
                }
                is Resource.Error -> {
                    getViewBinding()?.fmProgress?.visibility = View.GONE
                    Toast.makeText(requireContext(), resource.error ?: "Bilinmeyen bir hata oluştu", Toast.LENGTH_SHORT).show()
                }
            }
        })
    }

    private fun goToRegisterPage() {
        getViewBinding()?.tvRegister?.setOnClickListener {
            val action = LoginPageFragmentDirections.actionLoginPageFragmentToRegisterPageFragment()
            findNavController().navigate(action)
        }
    }
}
