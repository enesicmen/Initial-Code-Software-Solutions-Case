package com.icmen.ecommerceapplication.ui.activity

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.icmen.ecommerceapplication.R
import com.icmen.ecommerceapplication.databinding.ActivityLoginBinding
import com.icmen.ecommerceapplication.ui.common.BaseActivity

class LoginActivity : BaseActivity<ActivityLoginBinding>() {
    private lateinit var auth: FirebaseAuth

    override fun initView(savedInstanceState: Bundle?) {
        auth = FirebaseAuth.getInstance()

        getViewBinding()?.btnLogin?.setOnClickListener {
            val email = getViewBinding()?.etEmail?.text.toString().trim()
            val password = getViewBinding()?.etPassword?.text.toString().trim()

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, getString(R.string.please_fill_in_all_fields), Toast.LENGTH_SHORT).show()
            } else {
                getViewBinding()?.fmProgress?.visibility = android.view.View.VISIBLE
                loginUser(email, password)
            }
        }
        goToRegisterPage()

    }

    override fun setViewBinding(): ActivityLoginBinding {
        return ActivityLoginBinding.inflate(layoutInflater)
    }

    private fun goToRegisterPage(){
        getViewBinding()?.tvRegister?.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }
    }
    private fun loginUser(email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                getViewBinding()?.fmProgress?.visibility = android.view.View.GONE
                if (task.isSuccessful) {
                    Toast.makeText(this, getString(R.string.login_success), Toast.LENGTH_SHORT).show()
                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                    finish()
                } else {
                    Toast.makeText(this, getString(R.string.login_failed) + ": " + task.exception?.message, Toast.LENGTH_SHORT).show()
                }
            }
    }
}
