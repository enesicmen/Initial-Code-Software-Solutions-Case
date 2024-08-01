package com.icmen.codecase.ui.fragment.login

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.icmen.codecase.R
import com.icmen.codecase.databinding.FragmentLoginBinding
import com.icmen.codecase.ui.base.BaseFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LoginPageFragment : BaseFragment<FragmentLoginBinding, LoginPageViewModel>() {

    override fun setViewBinding(): FragmentLoginBinding {
        return FragmentLoginBinding.inflate(layoutInflater)
    }

    override fun setViewModelClass() = LoginPageViewModel::class.java

    override fun initView(savedInstanceState: Bundle?) {
        getViewBinding()?.btnLogin?.setOnClickListener {
            val email = getViewBinding()?.etEmail?.text.toString().trim()
            val password = getViewBinding()?.etPassword?.text.toString().trim()
            getViewModel()?.loginUser(email, password)
        }

        goToRegisterPage()
        observeViewModel()
    }

    private fun observeViewModel() {
        getViewModel()?.loginResult?.observe(viewLifecycleOwner, Observer { result ->
            result.onSuccess {
                Toast.makeText(requireContext(), getString(R.string.login_success), Toast.LENGTH_SHORT).show()
                val action = LoginPageFragmentDirections.actionLoginPageFragmentToProductsPageFragment()
                findNavController().navigate(action)
            }
            result.onFailure { exception ->
                Toast.makeText(requireContext(), exception.message, Toast.LENGTH_SHORT).show()
            }
        })

        getViewModel()?.progressVisibility?.observe(viewLifecycleOwner, Observer { isVisible ->
            getViewBinding()?.fmProgress?.visibility = if (isVisible) View.VISIBLE else View.GONE
        })
    }

    private fun goToRegisterPage() {
        getViewBinding()?.tvRegister?.setOnClickListener {
            val action = LoginPageFragmentDirections.actionLoginPageFragmentToRegisterPageFragment()
            findNavController().navigate(action)
        }
    }
}
