package com.icmen.ecommerceapplication.ui.activity

import android.os.Bundle
import com.icmen.ecommerceapplication.databinding.ActivityMainBinding
import com.icmen.ecommerceapplication.ui.common.BaseActivity

class MainActivity : BaseActivity<ActivityMainBinding>() {
    override fun initView(savedInstanceState: Bundle?) {

    }

    override fun setViewBinding(): ActivityMainBinding {
        return ActivityMainBinding.inflate(layoutInflater)
    }

}