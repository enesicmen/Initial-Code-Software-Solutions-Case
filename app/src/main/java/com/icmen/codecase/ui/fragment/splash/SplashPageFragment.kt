package com.icmen.codecase.ui.fragment.splash

import android.os.Bundle
import android.view.View
import androidx.navigation.fragment.findNavController
import com.icmen.codecase.data.Resource
import com.icmen.codecase.databinding.FragmentSplashBinding
import com.icmen.codecase.ui.base.BaseFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SplashPageFragment : BaseFragment<FragmentSplashBinding, SplashPageViewModel>() {

    override fun setViewModelClass() = SplashPageViewModel::class.java

    override fun setViewBinding(): FragmentSplashBinding =
        FragmentSplashBinding.inflate(layoutInflater)

    override fun initView(savedInstanceState: Bundle?) {
        observeViewModel()
        getViewModel()?.checkUserAuthentication()
    }

    private fun observeViewModel() {
        getViewModel()?.isUserAuthenticatedLiveData?.observe(viewLifecycleOwner) { resource ->
            when (resource) {
                is Resource.Loading -> {
                    getViewBinding()?.progressBar?.visibility = View.VISIBLE
                }
                is Resource.Success -> {
                    getViewBinding()?.progressBar?.visibility = View.GONE
                    if (resource.data == true) {
                        val login = SplashPageFragmentDirections.actionSplashPageFragmentToProductsPageFragment()
                        findNavController().navigate(login)
                    } else {
                        val login = SplashPageFragmentDirections.actionSplashPageFragmentToLoginPageFragment()
                        findNavController().navigate(login)
                    }
                }
                is Resource.Error -> {
                    getViewBinding()?.progressBar?.visibility = View.GONE
                    val register = SplashPageFragmentDirections.actionSplashPageFragmentToLoginPageFragment()
                    findNavController().navigate(register)
                }
            }
        }
    }
}
