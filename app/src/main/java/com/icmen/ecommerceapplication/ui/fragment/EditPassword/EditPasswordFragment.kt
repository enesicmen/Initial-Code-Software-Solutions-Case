package com.icmen.ecommerceapplication.ui.fragment.EditPassword

import android.os.Bundle
import com.icmen.ecommerceapplication.databinding.FragmentEditPasswordBinding
import com.icmen.ecommerceapplication.ui.base.BaseFragment

class EditPasswordFragment : BaseFragment<FragmentEditPasswordBinding> () {
    override fun initView(savedInstanceState: Bundle?) {

    }

    override fun setViewBinding(): FragmentEditPasswordBinding =
        FragmentEditPasswordBinding.inflate(layoutInflater)
}