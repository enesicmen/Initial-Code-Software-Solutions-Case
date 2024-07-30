package com.icmen.ecommerceapplication.ui.activity

import android.os.Bundle
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.icmen.ecommerceapplication.R
import com.icmen.ecommerceapplication.databinding.ActivityMainBinding
import com.icmen.ecommerceapplication.ui.base.BaseActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : BaseActivity<ActivityMainBinding>() {

    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setViewBinding()
        initView(savedInstanceState)
    }

    override fun setViewBinding(): ActivityMainBinding {
        val binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        return binding
    }

    override fun initView(savedInstanceState: Bundle?) {
        setBottomNavigation()
    }

    private fun setBottomNavigation() {
            val navView: BottomNavigationView = getViewBinding()?.bottomNavigation
                ?: throw IllegalStateException("BottomNavigationView not found")

            val navHostFragment = supportFragmentManager.findFragmentById(R.id.navHostFragment) as? NavHostFragment
                ?: throw IllegalStateException("NavHostFragment not found")

            navController = navHostFragment.navController
            NavigationUI.setupWithNavController(navView, navController)

            // Bu satırı ekleyerek otomatik geçişi kaldırıyoruz
            navView.setOnNavigationItemSelectedListener { menuItem ->
                when (menuItem.itemId) {
                    R.id.menu_item_home -> {
                        navController.navigate(R.id.productsPageFragment)
                        true
                    }
                    R.id.menu_item_basket -> {
                        navController.navigate(R.id.basketPageFragment)
                        true
                    }
                    R.id.menu_item_profile -> {
                        navController.navigate(R.id.profilePageFragment)
                        true
                    }
                    else -> false
            }
        }

    }
}
