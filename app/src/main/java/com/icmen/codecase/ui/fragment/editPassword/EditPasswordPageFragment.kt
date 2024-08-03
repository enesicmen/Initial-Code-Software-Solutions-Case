package com.icmen.codecase.ui.fragment.editPassword

import android.os.Bundle
import android.view.View
import androidx.activity.OnBackPressedCallback
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

    override fun setViewBinding(): FragmentEditPasswordBinding =
        FragmentEditPasswordBinding.inflate(layoutInflater)

    override fun initView(savedInstanceState: Bundle?) {
        getViewBinding()?.btnChangePassword?.setOnClickListener {
            val currentPassword = getViewBinding()?.etCurrentPassword?.text.toString()
            val newPassword = getViewBinding()?.etNewPassword?.text.toString()
            val confirmNewPassword = getViewBinding()?.etConfirmNewPassword?.text.toString()

            getViewModel()?.changePassword(currentPassword, newPassword, confirmNewPassword)
        }
        observeViewModel()
        onBackPressedDispatcher()
    }
    private fun onBackPressedDispatcher (){
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
        getViewModel()?.changePasswordLiveData?.observe(viewLifecycleOwner) { resource ->
            when (resource) {
                is Resource.Loading -> {
                    getViewBinding()?.progressBar?.visibility = View.VISIBLE
                }
                is Resource.Success -> {
                    getViewBinding()?.progressBar?.visibility = View.GONE
                    navigateToSuccessPage()
                }
                is Resource.Error -> {
                    getViewBinding()?.progressBar?.visibility = View.GONE
                    when (resource.error) {
                        "0" -> {
                            var title = getString(R.string.error)
                            var message = getString(R.string.fill_in_all_fields)
                            setOneButtonDialog(title,message)
                        }
                        "1" -> {
                            var title = getString(R.string.error)
                            var message = getString(R.string.new_passwords_do_not_match)
                            setOneButtonDialog(title,message)
                        }
                        "2" -> {
                            var title = getString(R.string.error)
                            var message = getString(R.string.password_could_not_be_changed)
                            setOneButtonDialog(title,message)
                        }
                        "3" -> {
                            var title = getString(R.string.error)
                            var message = getString(R.string.current_password_is_incorrect)
                            setOneButtonDialog(title,message)
                        }
                        "4" -> {
                            var title = getString(R.string.error)
                            var message = getString(R.string.must_be_at_least_six_characters_long)
                            setOneButtonDialog(title,message)
                        }
                    }
                }
            }
        }
    }

    override fun setViewModelClass() = EditPasswordPageViewModel::class.java

    private fun navigateToSuccessPage() {
        val title = getString(R.string.success)
        val message = getString(R.string.change_password_success)
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
