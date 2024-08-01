package com.icmen.codecase.ui.fragment.profile

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.icmen.codecase.R
import com.icmen.codecase.data.model.User
import com.icmen.codecase.databinding.FragmentProfileBinding
import com.icmen.codecase.ui.base.BaseFragment
import dagger.hilt.android.AndroidEntryPoint
import de.hdodenhof.circleimageview.CircleImageView

@AndroidEntryPoint
class ProfilePageFragment : BaseFragment<FragmentProfileBinding>(){
    private lateinit var auth: FirebaseAuth

    override fun initView(savedInstanceState: Bundle?) {
        auth = FirebaseAuth.getInstance()
        val db = FirebaseFirestore.getInstance()
        val user = auth.currentUser
        val userId = user?.uid

        val progressBar = getViewBinding()?.progressBar

        progressBar?.visibility = View.VISIBLE

        val imageView: CircleImageView = getViewBinding()?.ivProfile ?: return
        if (userId != null) {
            db.collection("users").document(userId)
                .get()
                .addOnSuccessListener { document ->
                    progressBar?.visibility = View.GONE

                    if (document != null) {
                        val userModel = document.toObject(User::class.java)

                        userModel?.let { user ->
                            Glide.with(this)
                                .load(user.profileImageUrl)
                                .placeholder(R.drawable.ic_launcher_background)
                                .into(imageView)

                            Log.d("User Info", "Name: ${user.name}, Email: ${user.email}")
                        }
                    } else {
                        Log.d("User Info", "Belge bulunamadı")
                    }
                }
                .addOnFailureListener { exception ->
                    progressBar?.visibility = View.GONE
                    Log.w("User Info", "Belge alma hatası", exception)
                }
        } else {
            progressBar?.visibility = View.GONE
            Log.d("User Info", "Kullanıcı oturum açmamış veya ID bulunamadı.")
        }
        openEditPasswordPage()
        openEditProfilePage()
        openOrdersPage()
    }

    override fun setViewBinding(): FragmentProfileBinding =
        FragmentProfileBinding.inflate(layoutInflater)

    private fun openEditPasswordPage(){
        getViewBinding()?.llEditPassword?.setOnClickListener {
            val actionDetail = ProfilePageFragmentDirections.actionProfilePageFragmentToEditPasswordPageFragment()
            findNavController().navigate(actionDetail)
        }
    }

    private fun openEditProfilePage(){
        getViewBinding()?.llEditProfile?.setOnClickListener {
            val actionDetail = ProfilePageFragmentDirections.actionProfilePageFragmentToEditProfilePageFragment()
            findNavController().navigate(actionDetail)
        }
    }

    private fun openOrdersPage(){
        getViewBinding()?.llOrders?.setOnClickListener {
            val actionDetail = ProfilePageFragmentDirections.actionProfilePageFragmentToOrdersPageFragment()
            findNavController().navigate(actionDetail)
        }
    }

}