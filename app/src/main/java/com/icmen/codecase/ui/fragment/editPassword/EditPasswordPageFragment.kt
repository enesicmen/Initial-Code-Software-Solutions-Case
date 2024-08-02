package com.icmen.codecase.ui.fragment.editPassword

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.viewModels
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.icmen.codecase.R
import com.icmen.codecase.data.Resource
import com.icmen.codecase.databinding.FragmentEditPasswordBinding
import com.icmen.codecase.ui.base.BaseFragment
import com.icmen.codecase.ui.fragment.custom.CustomDialogWithOneButtonFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class EditPasswordPageFragment : BaseFragment<FragmentEditPasswordBinding, EditPasswordPageViewModel>() {

    private val viewModel: EditPasswordPageViewModel by viewModels()

    override fun setViewBinding(): FragmentEditPasswordBinding =
        FragmentEditPasswordBinding.inflate(layoutInflater)

    override fun initView(savedInstanceState: Bundle?) {
        getViewBinding()?.btnChangePassword?.setOnClickListener {
            val currentPassword = getViewBinding()?.etCurrentPassword?.text.toString()
            val newPassword = getViewBinding()?.etNewPassword?.text.toString()
            val confirmNewPassword = getViewBinding()?.etConfirmNewPassword?.text.toString()

            viewModel.changePassword(currentPassword, newPassword, confirmNewPassword)
        }

        observeViewModel()


        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    navigateToProductsPageAndClearBackStack()
                }
            }
        )
    }

    private fun observeViewModel() {
        viewModel.changePasswordLiveData.observe(viewLifecycleOwner) { resource ->
            when (resource) {
                is Resource.Loading -> {
                    getViewBinding()?.progressBar?.visibility = View.VISIBLE
                }
                is Resource.Success -> {
                    getViewBinding()?.progressBar?.visibility = View.GONE
                    Toast.makeText(requireContext(), "Şifre başarıyla değiştirildi", Toast.LENGTH_SHORT).show()
                    navigateToSuccessPage()
                }
                is Resource.Error -> {
                    getViewBinding()?.progressBar?.visibility = View.GONE
                    Toast.makeText(requireContext(), resource.error ?: "Bilinmeyen bir hata oluştu", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    override fun setViewModelClass() = EditPasswordPageViewModel::class.java

    private fun navigateToSuccessPage() {
        val title = getString(R.string.success)
        val message = getString(R.string.order_success)
        val navOptions = NavOptions.Builder()
            .setPopUpTo(R.id.editPasswordPageFragment, true)
            .build()
        setOneButtonDialog(title, message)
        findNavController().navigate(R.id.action_editPasswordPageFragment_to_profilePageFragment, null, navOptions)
        updateBottomNavigationView()
    }

    private fun navigateToProductsPageAndClearBackStack() {
        val navOptions = NavOptions.Builder()
            .setPopUpTo(R.id.editPasswordPageFragment, true)
            .build()
        findNavController().navigate(R.id.profilePageFragment, null, navOptions)
        updateBottomNavigationView()
    }

    private fun updateBottomNavigationView() {
        val bottomNavigationView = requireActivity().findViewById<BottomNavigationView>(R.id.bottom_navigation)
        bottomNavigationView?.selectedItemId = R.id.menu_item_profile
    }

    private fun setOneButtonDialog(title: String, message: String) {
        val dialog = CustomDialogWithOneButtonFragment.newInstance(title, message)
        dialog.onOkClicked = {}
        dialog.show(requireActivity().supportFragmentManager, "customDialog")
    }
}
