package com.icmen.codecase.ui.activity
import android.os.Bundle
import android.view.View
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setBottomNavigationAndNavigation()
    }

    private fun setBottomNavigationAndNavigation(){
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController

        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottom_navigation)
        bottomNavigationView.setupWithNavController(navController)
        bottomNavigationView.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
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

    fun setBottomNavigationVisibility(isVisible: Boolean) {
        val bottomNavigation = findViewById<BottomNavigationView>(R.id.bottom_navigation)
        bottomNavigation.visibility = if (isVisible) View.VISIBLE else View.GONE
    }
}