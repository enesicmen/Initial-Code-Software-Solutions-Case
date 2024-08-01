package com.icmen.codecase.ui.fragment.splash

import android.os.Bundle
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.icmen.codecase.databinding.FragmentSplashBinding
import com.icmen.codecase.ui.base.BaseFragment

class SplashPageFragment : BaseFragment<FragmentSplashBinding>() {
    override fun initView(savedInstanceState: Bundle?) {
        redirect()
    }

    override fun setViewBinding(): FragmentSplashBinding =
        FragmentSplashBinding.inflate(layoutInflater)

    private fun redirect() {
        val auth = FirebaseAuth.getInstance()
        val currentUser = auth.currentUser
        if (currentUser != null) {
            val login = SplashPageFragmentDirections.actionSplashPageFragmentToProductsPageFragment()
            findNavController().navigate(login)
        } else {
            val register = SplashPageFragmentDirections.actionSplashPageFragmentToRegisterPageFragment()
            findNavController().navigate(register)
        }
    }

}