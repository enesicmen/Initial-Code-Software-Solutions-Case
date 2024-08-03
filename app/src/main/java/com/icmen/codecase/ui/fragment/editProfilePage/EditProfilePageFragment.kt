package com.icmen.codecase.ui.fragment.editProfilePage

import android.os.Bundle
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.icmen.codecase.R
import com.icmen.codecase.data.Resource
import com.icmen.codecase.databinding.FragmentEditProfileBinding
import com.icmen.codecase.ui.base.BaseFragment
import com.icmen.codecase.ui.fragment.custom.CustomDialogWithOneButtonFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class EditProfilePageFragment : BaseFragment<FragmentEditProfileBinding, EditProfilePageViewModel>() {

    override fun setViewBinding(): FragmentEditProfileBinding =
        FragmentEditProfileBinding.inflate(layoutInflater)

    override fun setViewModelClass() = EditProfilePageViewModel::class.java

    override fun initView(savedInstanceState: Bundle?) {
        observeViewModel()
        getViewModel()?.loadUserProfile()

        getViewBinding()?.btnUpdate?.setOnClickListener {
            val name = getViewBinding()?.etName?.text.toString()
            val surname = getViewBinding()?.etSurname?.text.toString()
            val address = getViewBinding()?.etAddress?.text.toString()

            val updates = mutableMapOf<String, Any>()
            val currentUserInfo = getViewModel()?.getCurrentUserInfo()

            if (name != currentUserInfo?.get("name") ?: "") {
                updates["name"] = name
            }
            if (surname != currentUserInfo?.get("surname") ?: "") {
                updates["surname"] = surname
            }
            if (address != currentUserInfo?.get("address") ?: "") {
                updates["address"] = address
            }

            if (updates.isNotEmpty()) {
                getViewModel()?.updateUserProfile(updates)
            }
        }
        onBackPressedDispatcher()
    }

    private fun onBackPressedDispatcher(){
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
        getViewModel()?.userProfileLiveData?.observe(viewLifecycleOwner) { resource ->
            when (resource) {
                is Resource.Loading -> {
                    getViewBinding()?.progressBar?.visibility = View.VISIBLE
                }
                is Resource.Success -> {
                    getViewBinding()?.progressBar?.visibility = View.GONE
                    val userInfo = resource.data!!
                    getViewBinding()?.etName?.setText(userInfo["name"] as? String)
                    getViewBinding()?.etSurname?.setText(userInfo["surname"] as? String)
                    getViewBinding()?.etAddress?.setText(userInfo["address"] as? String)
                }
                is Resource.Error -> {
                    getViewBinding()?.progressBar?.visibility = View.GONE
                    var title = getString(R.string.error)
                    var message = resource.error
                    if (message != null) {
                        setOneButtonDialog(title, message)
                    }
                }
            }
        }

        getViewModel()?.updateProfileLiveData?.observe(viewLifecycleOwner) { resource ->
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
                    var title = getString(R.string.error)
                    var message = resource.error
                    if (message != null) {
                        setOneButtonDialog(title, message)
                    }
                }
            }
        }
    }
    private fun setOneButtonDialog(title: String, message: String) {
        val dialog = CustomDialogWithOneButtonFragment.newInstance(title, message)
        dialog.onOkClicked = {}
        dialog.show(requireActivity().supportFragmentManager, "customDialog")
    }

    private fun navigateToSuccessPage() {
        var title = getString(R.string.success)
        var message = getString(R.string.profile_updated_successfully)
        val navOptions = NavOptions.Builder()
            .setPopUpTo(R.id.editProfilePageFragment, true)
            .build()
        setOneButtonDialog(title, message)
        findNavController().navigate(R.id.action_editProfilePageFragment_to_profilePageFragment, null, navOptions)
        updateBottomNavigationView()
    }

    private fun navigateToProductsPageAndClearBackStack() {
        val navOptions = NavOptions.Builder()
            .setPopUpTo(R.id.editProfilePageFragment, true)
            .build()
        findNavController().navigate(R.id.profilePageFragment, null, navOptions)
        updateBottomNavigationView()
    }

    private fun updateBottomNavigationView() {
        val bottomNavigationView = requireActivity().findViewById<BottomNavigationView>(R.id.bottom_navigation)
        bottomNavigationView?.selectedItemId = R.id.profilePageFragment
    }
}
