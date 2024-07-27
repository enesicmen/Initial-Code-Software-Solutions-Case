package com.icmen.ecommerceapplication.ui.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import com.google.firebase.auth.FirebaseAuth
import com.icmen.ecommerceapplication.databinding.ActivitySplashBinding
import com.icmen.ecommerceapplication.ui.base.BaseActivity


@SuppressLint("CustomSplashScreen")
class SplashActivity: BaseActivity<ActivitySplashBinding>(){
    override fun initView(savedInstanceState: Bundle?) {
        redirect()
    }

    override fun setViewBinding(): ActivitySplashBinding {
        return ActivitySplashBinding.inflate(layoutInflater)
    }

    private fun redirect() {
        val auth = FirebaseAuth.getInstance()
        val currentUser = auth.currentUser
        val intent = if (currentUser != null) {
            Intent(this, MainActivity::class.java)
        } else {
            Intent(this, RegisterActivity::class.java)
        }
        startActivity(intent)
    }
}