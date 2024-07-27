package com.icmen.ecommerceapplication.ui.fragment.Profile

import android.os.Bundle
import com.icmen.ecommerceapplication.databinding.FragmentProfileBinding
import com.icmen.ecommerceapplication.ui.base.BaseFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ProfilePageFragment : BaseFragment<FragmentProfileBinding, ProfilePageViewModel>(){
    override fun initView(savedInstanceState: Bundle?) {

    }

    override fun setViewModelClass() = ProfilePageViewModel::class.java

    override fun setViewBinding(): FragmentProfileBinding =
        FragmentProfileBinding.inflate(layoutInflater)
}