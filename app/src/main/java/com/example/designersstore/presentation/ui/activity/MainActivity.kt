package com.example.designersstore.presentation.ui.activity

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.content.ContextCompat
import com.example.designersstore.R
import com.example.designersstore.presentation.utils.Constants
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val sharedPreferences=getSharedPreferences(Constants.DESIGNERSSTORE_PREFERENCES, Context.MODE_PRIVATE)
        val username=sharedPreferences.getString(Constants.LOGGED_IN_USERNAME,"")!!

        tv_main.text="Hello $username."
        window.statusBarColor = ContextCompat.getColor(this, R.color.black)
    }
}