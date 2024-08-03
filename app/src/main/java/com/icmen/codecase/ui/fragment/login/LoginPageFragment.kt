package com.icmen.codecase.ui.fragment.login

import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.icmen.codecase.R
import com.icmen.codecase.data.Resource
import com.icmen.codecase.databinding.FragmentLoginBinding
import com.icmen.codecase.ui.base.BaseFragment
import com.icmen.codecase.ui.fragment.custom.CustomDialogWithOneButtonFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LoginPageFragment : BaseFragment<FragmentLoginBinding, LoginPageViewModel>() {


    override fun setViewBinding(): FragmentLoginBinding {
        return FragmentLoginBinding.inflate(layoutInflater)
    }

    override fun setViewModelClass() = LoginPageViewModel::class.java

    override fun initView(savedInstanceState: Bundle?) {
        setLoginButton()
        goToRegisterPage()
        observeViewModel()
    }

    private fun setLoginButton(){
        getViewBinding()?.btnLogin?.setOnClickListener {
            val email = getViewBinding()?.etEmail?.text.toString().trim()
            val password = getViewBinding()?.etPassword?.text.toString().trim()
            getViewModel()?.loginUser(email, password)
        }
    }
    private fun observeViewModel() {
        getViewModel()?.loginResult?.observe(viewLifecycleOwner, Observer { resource ->
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
                    when (resource.error) {
                        "0" -> {
                            val title = getString(R.string.error)
                            val message = getString(R.string.fill_in_all_fields)
                            setOneButtonDialog(title,message)
                        }
                        "1" -> {
                            val title = getString(R.string.error)
                            val message = getString(R.string.login_error)
                            setOneButtonDialog(title,message)
                        }
                    }
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
    private fun setOneButtonDialog(title: String, message: String) {
        val dialog = CustomDialogWithOneButtonFragment.newInstance(title, message)
        dialog.onOkClicked = {}
        dialog.show(requireActivity().supportFragmentManager, "customDialog")
    }
}
