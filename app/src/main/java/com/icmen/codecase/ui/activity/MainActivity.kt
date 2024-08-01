package com.icmen.codecase.ui.activity

import android.os.Bundle
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.icmen.codecase.R
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var navController: NavController
    private lateinit var bottomNavigationView: BottomNavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setBottomNavigationAndNavigation()

        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (navController.currentDestination?.id == R.id.productsPageFragment) {
                    finishAffinity()
                } else {
                    navController.popBackStack()
                    updateBottomNavigationView()
                }
            }
        })
    }

    private fun setBottomNavigationAndNavigation() {
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController

        bottomNavigationView = findViewById(R.id.bottom_navigation)
        bottomNavigationView.setupWithNavController(navController)

        navController.addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {
                R.id.loginPageFragment, R.id.registerPageFragment -> {
                    bottomNavigationView.visibility = View.GONE
                }
                else -> {
                    bottomNavigationView.visibility = View.VISIBLE
                }
            }
        }

        bottomNavigationView.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.menu_item_home -> {
                    navigateToDestination(R.id.productsPageFragment)
                    true
                }
                R.id.menu_item_basket -> {
                    navigateToDestination(R.id.basketPageFragment)
                    true
                }
                R.id.menu_item_profile -> {
                    navigateToDestination(R.id.profilePageFragment)
                    true
                }
                else -> false
            }
        }
    }

    private fun navigateToDestination(destinationId: Int) {
        if (navController.currentDestination?.id != destinationId) {
            navController.navigate(destinationId)
        }
    }

    private fun updateBottomNavigationView() {
        when (navController.currentDestination?.id) {
            R.id.productsPageFragment -> bottomNavigationView.selectedItemId = R.id.menu_item_home
            R.id.basketPageFragment -> bottomNavigationView.selectedItemId = R.id.menu_item_basket
            R.id.profilePageFragment -> bottomNavigationView.selectedItemId = R.id.menu_item_profile
        }
    }
}
