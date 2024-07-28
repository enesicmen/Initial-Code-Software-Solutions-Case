package com.icmen.ecommerceapplication.ui.fragment

import android.os.Bundle
import com.icmen.ecommerceapplication.databinding.FragmentProductsBinding
import com.icmen.ecommerceapplication.ui.base.BaseFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class EditProfilePageFragment : BaseFragment<FragmentProductsBinding>() {

    override fun setViewBinding(): FragmentProductsBinding =
        FragmentProductsBinding.inflate(layoutInflater)

    override fun initView(savedInstanceState: Bundle?) {
    }

}
