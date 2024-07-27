package com.icmen.ecommerceapplication.ui.fragment.Profile

import android.content.Intent
import android.os.Bundle
import com.google.firebase.auth.FirebaseAuth
import com.icmen.ecommerceapplication.databinding.FragmentProfileBinding
import com.icmen.ecommerceapplication.ui.activity.LoginActivity
import com.icmen.ecommerceapplication.ui.base.BaseFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ProfilePageFragment : BaseFragment<FragmentProfileBinding, ProfilePageViewModel>(){
    private lateinit var auth: FirebaseAuth

    override fun initView(savedInstanceState: Bundle?) {
        auth = FirebaseAuth.getInstance()

        getViewBinding()?.btnLogout?.setOnClickListener {
            logout()
        }
    }

    override fun setViewModelClass() = ProfilePageViewModel::class.java

    override fun setViewBinding(): FragmentProfileBinding =
        FragmentProfileBinding.inflate(layoutInflater)

    private fun logout() {
        FirebaseAuth.getInstance().signOut()
        val intent = Intent(requireActivity(), LoginActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
        requireActivity().finish()
    }
}