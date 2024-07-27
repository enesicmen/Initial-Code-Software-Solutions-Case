package com.icmen.ecommerceapplication.ui.fragment.Basket

import android.os.Bundle
import com.icmen.ecommerceapplication.databinding.FragmentBasketBinding
import com.icmen.ecommerceapplication.ui.base.BaseFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class BasketPageFragment : BaseFragment<FragmentBasketBinding, BasketPageViewModel>(){
    override fun initView(savedInstanceState: Bundle?) {

    }

    override fun setViewModelClass() = BasketPageViewModel::class.java

    override fun setViewBinding(): FragmentBasketBinding =
        FragmentBasketBinding.inflate(layoutInflater)
}