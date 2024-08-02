package com.icmen.codecase.ui.fragment.profile

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.icmen.codecase.R
import com.icmen.codecase.data.Resource
import com.icmen.codecase.databinding.FragmentProfileBinding
import com.icmen.codecase.ui.base.BaseFragment
import dagger.hilt.android.AndroidEntryPoint
import de.hdodenhof.circleimageview.CircleImageView

@AndroidEntryPoint
class ProfilePageFragment : BaseFragment<FragmentProfileBinding, ProfilePageViewModel>() {

    private val viewModel: ProfilePageViewModel by viewModels()
    private lateinit var auth: FirebaseAuth

    override fun initView(savedInstanceState: Bundle?) {
        auth = FirebaseAuth.getInstance()
        val user = auth.currentUser
        val userId = user?.uid

        val progressBar = getViewBinding()?.progressBar
        progressBar?.visibility = View.VISIBLE

        val imageView: CircleImageView = getViewBinding()?.ivProfile ?: return

        if (userId != null) {
            viewModel.fetchUserData(userId)
            observeUserData(imageView, progressBar)
        } else {
            progressBar?.visibility = View.GONE
            Log.d("Kullanıcı Bilgisi", "Kullanıcı oturum açmamış veya ID bulunamadı.")
        }

        openEditPasswordPage()
        openEditProfilePage()
        openOrdersPage()
    }

    override fun setViewModelClass() = ProfilePageViewModel::class.java

    override fun setViewBinding(): FragmentProfileBinding =
        FragmentProfileBinding.inflate(layoutInflater)

    private fun observeUserData(imageView: CircleImageView, progressBar: View?) {
        viewModel.userData.observe(viewLifecycleOwner) { resource ->
            when (resource) {
                is Resource.Loading -> {
                    progressBar?.visibility = View.VISIBLE
                }
                is Resource.Success -> {
                    progressBar?.visibility = View.GONE
                    resource.data?.let { user ->
                        Glide.with(this)
                            .load(user.profileImageUrl)
                            .placeholder(R.drawable.ic_launcher_background)
                            .into(imageView)

                        Log.d("Kullanıcı Bilgisi", "İsim: ${user.name}, Email: ${user.email}")
                    }
                }
                is Resource.Error -> {
                    progressBar?.visibility = View.GONE
                    Toast.makeText(requireContext(), resource.error ?: "Veri yüklenirken hata oluştu", Toast.LENGTH_SHORT).show()
                    Log.d("Kullanıcı Bilgisi", resource.error ?: "Veri yüklenirken hata oluştu")
                }
            }
        }
    }

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
