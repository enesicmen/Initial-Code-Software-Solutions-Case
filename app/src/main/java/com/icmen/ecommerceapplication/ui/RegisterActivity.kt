package com.icmen.ecommerceapplication.ui

import android.os.Bundle
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.icmen.ecommerceapplication.R
import com.icmen.ecommerceapplication.databinding.ActivityRegisterBinding
import com.icmen.ecommerceapplication.ui.common.BaseActivity

class RegisterActivity : BaseActivity<ActivityRegisterBinding>() {

    private lateinit var auth: FirebaseAuth

    override fun setViewBinding(): ActivityRegisterBinding {
        return ActivityRegisterBinding.inflate(layoutInflater)
    }

    override fun initView(savedInstanceState: Bundle?) {
        auth = FirebaseAuth.getInstance()
        getViewBinding()?.registerButton?.setOnClickListener {
            val email = getViewBinding()?.emailEditText?.text.toString()
            val password = getViewBinding()?.passwordEditText?.text.toString()

            if (email.isNotEmpty() && password.isNotEmpty()) {
                registerUser(email, password)
            } else {
                Toast.makeText(this, R.string.please_fill_in_all_fields, Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun registerUser(email: String, password: String) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val user: FirebaseUser? = auth.currentUser
                    Toast.makeText(this, R.string.registration_successful, Toast.LENGTH_SHORT).show()
                    updateUI(user)
                } else {
                    val errorMessage = getString(R.string.registration_failed) + ": " +task.exception?.message
                    Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show()
                    updateUI(null)
                }
            }
    }

    private fun updateUI(user: FirebaseUser?) {
    }
}
