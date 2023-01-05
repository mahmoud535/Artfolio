package com.example.designersstore.presentation.ui.activity

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.WindowInsets
import android.view.WindowManager
import com.example.designersstore.R
import com.example.designersstore.presentation.ui.activityclient.loginclient.Login_Client_Activity
import com.example.designersstore.presentation.ui.activitydesigners.Login_Designers_Activity
import kotlinx.android.synthetic.main.activity_welcome.*
import java.util.*

class WelcomeActivity : BaseActivity(), View.OnClickListener {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_welcome)
        loadLocate()// call LoadLocate

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


    //Change languages
    private fun showChangeLang() {

        val listItems= arrayOf("عربي","English")

        val mBuilder= AlertDialog.Builder(this@WelcomeActivity)
        mBuilder.setTitle("Choose Language")
        mBuilder.setSingleChoiceItems(listItems,-1){ dialog, which->
            if (which==0){
                setLocate("ar")
                recreate()
            }else if(which==1){
                setLocate("en")
                recreate()
            }
            dialog.dismiss()
        }
        val mDialog=mBuilder.create()
        mDialog.show()
    }

    private fun setLocate(Lang:String){
        val locale= Locale(Lang)
        Locale.setDefault(locale)
        val config= Configuration()
        config.locale=locale
        baseContext.resources.updateConfiguration(config,baseContext.resources.displayMetrics)
        val editor=getSharedPreferences("Settings", Context.MODE_PRIVATE).edit()
        editor.putString("My_Lang",Lang)
        editor.apply()
    }
    private fun loadLocate(){
        val sharedPreferences=getSharedPreferences("Settings", Activity.MODE_PRIVATE)
        val language=sharedPreferences.getString("My_lang", "")
        setLocate(language!!)
    }
}