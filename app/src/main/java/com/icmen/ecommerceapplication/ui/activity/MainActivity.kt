package com.icmen.ecommerceapplication.ui.activity

import android.os.Bundle
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.icmen.ecommerceapplication.R
import com.icmen.ecommerceapplication.databinding.ActivityMainBinding
import com.icmen.ecommerceapplication.ui.base.BaseActivity
import com.icmen.ecommerceapplication.ui.fragment.Basket.BasketPageFragment
import com.icmen.ecommerceapplication.ui.fragment.Home.HomePageFragment
import com.icmen.ecommerceapplication.ui.fragment.Profile.ProfilePageFragment

class MainActivity : BaseActivity<ActivityMainBinding>() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setViewBinding()
        initView(savedInstanceState)
    }

    override fun initView(savedInstanceState: Bundle?) {
        setBottomNavigation(savedInstanceState)
    }

    override fun setViewBinding(): ActivityMainBinding {
        val binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        return binding
    }

    private fun setBottomNavigation(savedInstanceState: Bundle?) {
        val navView: BottomNavigationView = findViewById(R.id.bottom_navigation)

        navView.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.menu_item_home -> {
                    loadFragment(HomePageFragment())
                    return@setOnNavigationItemSelectedListener true
                }
                R.id.menu_item_basket -> {
                    loadFragment(BasketPageFragment())
                    return@setOnNavigationItemSelectedListener true
                }
                R.id.menu_item_profile -> {
                    loadFragment(ProfilePageFragment())
                    return@setOnNavigationItemSelectedListener true
                }
            }
            false
        }

        if (savedInstanceState == null) {
            navView.selectedItemId = R.id.menu_item_home
        }
    }

    private fun loadFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.nav_host_fragment, fragment)
            .commit()
    }
}
