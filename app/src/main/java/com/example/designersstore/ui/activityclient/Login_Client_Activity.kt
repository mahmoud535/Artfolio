package com.example.designersstore.ui.activityclient

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.view.WindowInsets
import android.view.WindowManager
import com.example.designersstore.R
import com.example.designersstore.firestore.FireStoreClass
import com.example.designersstore.models.User
import com.example.designersstore.ui.activity.BaseActivity
import com.example.designersstore.ui.activity.MainActivity
import com.example.designersstore.utils.Constants
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_login__client_.*

class Login_Client_Activity : BaseActivity() ,View.OnClickListener {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login__client_)

        //لازاله الشريط اللزي يوجد اعلي واجهه الSplashActivity
        //لجعل الواجهه ملئ الشاشه
        @Suppress("DEPRECATION")
        if (Build.VERSION.SDK_INT>= Build.VERSION_CODES.R){
            window.insetsController?.hide(WindowInsets.Type.statusBars())
        }else{
            window.setFlags(
                    WindowManager.LayoutParams.FLAG_FULLSCREEN,
                    WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        }


        tv_register.setOnClickListener(this)
        tv_forgot_password_Client.setOnClickListener(this)
        btn_login_Client.setOnClickListener(this)
    }

    //لملء البيانات والانتقال من واجهه الLoginActivity الي واجهه الDashboardActivity
    fun userLoggedInSuccess(user: User){
        //Hide the progress dialog
        hideProgressDialog()

        if (user.profileCompleted==0){
            //if the user profile is incomplete the launch the ClientProfileActivity.
            val intent=Intent(this@Login_Client_Activity, Client_Profile_Activity::class.java)
            intent.putExtra(Constants.EXTRA_USER_DETAILS,user)
            startActivity(intent)
        }else{
            startActivity(Intent(this@Login_Client_Activity, DashboardActivity::class.java))
        }
        finish()

    }

    //In Login screen the clickable components are Login Button,ForgotPassword text Register Text.
    override fun onClick(v: View?) {
        if (v != null) {
            when (v.id) {

                R.id.tv_register->{
                    val intent=Intent(this@Login_Client_Activity, Register_Client_Activity::class.java)
                    startActivity(intent)
                }
                R.id.tv_forgot_password_Client->{
                    val intent=Intent(this@Login_Client_Activity, Forgotpassword_Client_Activity::class.java)
                    startActivity(intent)
                }

                R.id.btn_login_Client->{
                    logInRegisteredUser()
                }
            }
        }
    }

    private fun validateLoginDetails():Boolean{
        return when{
            TextUtils.isEmpty(et_email_Client.text.toString().trim{it <= ' '}) ->{
                showErrorSnackBar(resources.getString(R.string.err_msg_enter_email),true)
                false
            }
            TextUtils.isEmpty(et_password_Client.text.toString().trim{it <= ' '}) ->{
                showErrorSnackBar(resources.getString(R.string.err_msg_enter_password),true)
                false
            }
            else->{
                //  showErrorSnackBar("Your details are valid",true)
                true
            }

        }
    }

    private fun logInRegisteredUser() {
        if (validateLoginDetails()){
            //Show the progress dialog.
            showProgressDialog(resources.getString(R.string.please_wait))

            //Get the text from editText and trim the space.
            val email =et_email_Client.text.toString().trim{it <= ' '}
            val password =et_password_Client.text.toString().trim{it <= ' '}

            //Login Using FirebaseAuth
            FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password)
                .addOnCompleteListener { task->

                    if (task.isSuccessful){
                        FireStoreClass().getUserDetails(this@Login_Client_Activity)
                    }else{
                        showErrorSnackBar(task.exception!!.message.toString(),true)
                    }

                }
        }
    }
}