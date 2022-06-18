package com.example.designersstore.ui.activitydesigners

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.designersstore.R
import com.example.designersstore.ui.activity.BaseActivity
import com.google.android.material.bottomnavigation.BottomNavigationView

class Products_Activity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_products_)
        val navView: BottomNavigationView = findViewById(R.id.nav_view_Designers)

        val navController = findNavController(R.id.nav_host_fragment_Designers)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_Products, R.id.navigation_chat_Designer, R.id.navigation_sold_products
            )
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
    }

    override fun onBackPressed() {
        doubleBackToExit()
    }
}