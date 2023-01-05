package com.example.designersstore.presentation.ui.activityclient

import android.os.Bundle
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI.setupWithNavController
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.designersstore.R
import com.example.designersstore.databinding.ActivityDashboardBinding
import com.example.designersstore.databinding.ActivityMainBinding
import com.example.designersstore.presentation.ui.activity.BaseActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class DashboardActivity : BaseActivity() {

    private lateinit var binding:ActivityDashboardBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDashboardBinding.inflate(layoutInflater)

        val host =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        setupWithNavController(binding.navView, host.navController)

        setContentView(binding.root)
    }

    override fun onBackPressed() {
        doubleBackToExit()
    }
}