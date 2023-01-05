package com.example.designersstore.presentation.ui.activitydesigners

import android.os.Bundle
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.designersstore.R
import com.example.designersstore.databinding.ActivityDashboardBinding
import com.example.designersstore.databinding.ActivityProductsBinding
import com.example.designersstore.presentation.ui.activity.BaseActivity
import com.google.android.material.bottomnavigation.BottomNavigationView

class Products_Activity : BaseActivity() {

    private lateinit var binding: ActivityProductsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProductsBinding.inflate(layoutInflater)

        val host =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment_Designers) as NavHostFragment
        NavigationUI.setupWithNavController(binding.navViewDesigners, host.navController)

        setContentView(binding.root)
    }

    override fun onBackPressed() {
        doubleBackToExit()
    }
}