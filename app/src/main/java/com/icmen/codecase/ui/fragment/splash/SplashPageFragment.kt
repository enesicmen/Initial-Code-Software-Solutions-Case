package com.icmen.codecase.ui.fragment.splash

import android.os.Bundle
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.icmen.codecase.databinding.FragmentSplashBinding
import com.icmen.codecase.ui.base.BaseFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SplashPageFragment : BaseFragment<FragmentSplashBinding, SplashPageViewModel>() {

    private val splashPageViewModel: SplashPageViewModel by viewModels()

    override fun setViewModelClass() = SplashPageViewModel::class.java

    override fun setViewBinding(): FragmentSplashBinding =
        FragmentSplashBinding.inflate(layoutInflater)

    override fun initView(savedInstanceState: Bundle?) {
        observeViewModel()
        splashPageViewModel.checkUserAuthentication()
    }

    private fun observeViewModel() {
        splashPageViewModel.isUserAuthenticatedLiveData.observe(viewLifecycleOwner) { isUserAuthenticated ->
            if (isUserAuthenticated) {
                val login = SplashPageFragmentDirections.actionSplashPageFragmentToProductsPageFragment()
                findNavController().navigate(login)
            } else {
                val register = SplashPageFragmentDirections.actionSplashPageFragmentToRegisterPageFragment()
                findNavController().navigate(register)
            }
        }
    }
}
