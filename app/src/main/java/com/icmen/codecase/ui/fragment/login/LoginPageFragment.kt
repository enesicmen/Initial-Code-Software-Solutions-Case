package com.icmen.codecase.ui.fragment.login

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.icmen.codecase.R
import com.icmen.codecase.databinding.FragmentLoginBinding
import com.icmen.codecase.ui.base.BaseFragment

class LoginPageFragment : BaseFragment<FragmentLoginBinding>() {

    private lateinit var auth: FirebaseAuth

    override fun setViewBinding(): FragmentLoginBinding {
        return FragmentLoginBinding.inflate(layoutInflater)
    }

    override fun initView(savedInstanceState: Bundle?) {
        auth = FirebaseAuth.getInstance()

        getViewBinding()?.btnLogin?.setOnClickListener {
            val email = getViewBinding()?.etEmail?.text.toString().trim()
            val password = getViewBinding()?.etPassword?.text.toString().trim()

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(requireContext(), getString(R.string.please_fill_in_all_fields), Toast.LENGTH_SHORT).show()
            } else {
                getViewBinding()?.fmProgress?.visibility = View.VISIBLE
                loginUser(email, password)
            }
        }

        goToRegisterPage()
    }

    private fun goToRegisterPage() {
        getViewBinding()?.tvRegister?.setOnClickListener {
            val action = LoginPageFragmentDirections.actionLoginPageFragmentToRegisterPageFragment()
            findNavController().navigate(action)
        }
    }

    private fun loginUser(email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(requireActivity()) { task ->
                getViewBinding()?.fmProgress?.visibility = View.GONE
                if (task.isSuccessful) {
                    Toast.makeText(requireContext(), getString(R.string.login_success), Toast.LENGTH_SHORT).show()
                    val action = LoginPageFragmentDirections.actionLoginPageFragmentToProductsPageFragment()
                    findNavController().navigate(action)
                } else {
                    Toast.makeText(requireContext(), getString(R.string.login_failed) + ": " + task.exception?.message, Toast.LENGTH_SHORT).show()
                }
            }
    }
}
