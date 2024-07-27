package com.icmen.ecommerceapplication.ui.fragment.Home

import android.os.Bundle
import com.icmen.ecommerceapplication.databinding.FragmentHomeBinding
import com.icmen.ecommerceapplication.ui.base.BaseFragment

class HomePageFragment : BaseFragment<FragmentHomeBinding, HomePageViewModel>(){
    override fun initView(savedInstanceState: Bundle?) {

    }

    override fun setViewModelClass() = HomePageViewModel::class.java

    override fun setViewBinding(): FragmentHomeBinding =
        FragmentHomeBinding.inflate(layoutInflater)
}