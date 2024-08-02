package com.icmen.codecase.ui.fragment.profile

import android.os.Bundle
import androidx.navigation.fragment.findNavController
import com.icmen.codecase.databinding.FragmentProfileBinding
import com.icmen.codecase.ui.base.BaseFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ProfilePageFragment : BaseFragment<FragmentProfileBinding, ProfilePageViewModel>() {

    override fun initView(savedInstanceState: Bundle?) {
        openEditPasswordPage()
        openEditProfilePage()
        openOrdersPage()
    }

    override fun setViewModelClass() = ProfilePageViewModel::class.java

    override fun setViewBinding(): FragmentProfileBinding =
        FragmentProfileBinding.inflate(layoutInflater)

    private fun openEditPasswordPage() {
        getViewBinding()?.llEditPassword?.setOnClickListener {
            val actionDetail = ProfilePageFragmentDirections.actionProfilePageFragmentToEditPasswordPageFragment()
            findNavController().navigate(actionDetail)
        }
    }

    private fun openEditProfilePage() {
        getViewBinding()?.llEditProfile?.setOnClickListener {
            val actionDetail = ProfilePageFragmentDirections.actionProfilePageFragmentToEditProfilePageFragment()
            findNavController().navigate(actionDetail)
        }
    }

    private fun openOrdersPage() {
        getViewBinding()?.llOrders?.setOnClickListener {
            val actionDetail = ProfilePageFragmentDirections.actionProfilePageFragmentToOrdersPageFragment()
            findNavController().navigate(actionDetail)
        }
    }
}
