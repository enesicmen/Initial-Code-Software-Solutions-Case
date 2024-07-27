package com.icmen.ecommerceapplication.ui

import android.os.Bundle
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.icmen.ecommerceapplication.R
import com.icmen.ecommerceapplication.databinding.ActivityRegisterBinding
import com.icmen.ecommerceapplication.ui.common.BaseActivity

class RegisterActivity : BaseActivity<ActivityRegisterBinding>() {

    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    override fun setViewBinding(): ActivityRegisterBinding {
        return ActivityRegisterBinding.inflate(layoutInflater)
    }

    override fun initView(savedInstanceState: Bundle?) {
        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        getViewBinding()?.btnRegister?.setOnClickListener {
            val name = getViewBinding()?.etName?.text.toString()
            val surname = getViewBinding()?.etSurname?.text.toString()
            val city = getViewBinding()?.etCity?.text.toString()
            val email = getViewBinding()?.etEmail?.text.toString()
            val password = getViewBinding()?.etPassword?.text.toString()


            if (email.isNotEmpty() && password.isNotEmpty()) {
                registerUser(name, surname, email, city, password)
            } else {
                Toast.makeText(this, getString(R.string.please_fill_in_all_fields), Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun registerUser(name: String, surname: String, email: String, city: String, password: String) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {

                    val user: FirebaseUser? = auth.currentUser
                    user?.let {
                        val userInfo = hashMapOf(
                            "userId" to it.uid,
                            "name" to name,
                            "surname" to surname,
                            "email" to email,
                            "city" to city
                        )
                        db.collection("users").document(it.uid).set(userInfo)
                            .addOnSuccessListener {
                                Toast.makeText(this, getString(R.string.registration_successful), Toast.LENGTH_SHORT).show()
                                updateUI(user)
                            }
                            .addOnFailureListener { e ->
                                val errorMessage = getString(R.string.adding_to_firestore_failed) +": "+ task.exception?.message
                                Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show()
                            }
                    }
                } else {
                    val errorMessage = getString(R.string.registration_failed) + ": "+ task.exception?.message
                    Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show()
                    updateUI(null)
                }
            }
    }

    private fun updateUI(user: FirebaseUser?) {
    }
}
