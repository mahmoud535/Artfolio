package com.example.designersstore.ui.activity

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.WindowInsets
import android.view.WindowManager
import com.example.designersstore.R
import com.example.designersstore.ui.activityclient.Login_Client_Activity
import com.example.designersstore.ui.activitydesigners.Login_Designers_Activity
import kotlinx.android.synthetic.main.activity_welcome.*

class WelcomeActivity : BaseActivity(), View.OnClickListener {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_welcome)

        //لازاله الشريط اللزي يوجد اعلي واجهه الSplashActivity
        //لجعل الواجهه ملئ الشاشه
        @Suppress("DEPRECATION")
        if (Build.VERSION.SDK_INT>= Build.VERSION_CODES.R){
            window.insetsController?.hide(WindowInsets.Type.statusBars())
        }else
        {
            window.setFlags(
                    WindowManager.LayoutParams.FLAG_FULLSCREEN,
                    WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        }
        //Start
        btn_designer.setOnClickListener(this)
        btn_Client.setOnClickListener(this)
    }
    override fun onClick(v: View?) {
        if (v != null){
            when(v.id){
                R.id.btn_designer->{
                 val intent=Intent(this@WelcomeActivity, Login_Designers_Activity::class.java)
                    startActivity(intent)
                }
                R.id.btn_Client->{
                    val intent=Intent(this@WelcomeActivity, Login_Client_Activity::class.java)
                    startActivity(intent)
                }
            }
        }

    }
}