package com.example.designersstore.ui.activitydesigners

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.view.WindowInsets
import android.view.WindowManager
import com.example.designersstore.R
import com.example.designersstore.firestore.FireStoreClass
import com.example.designersstore.models.Designer
import com.example.designersstore.ui.activity.BaseActivity
import com.example.designersstore.ui.activity.MainActivity
import com.example.designersstore.utils.Constants
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_login__designers_.*

class Login_Designers_Activity :BaseActivity(),View.OnClickListener {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login__designers_)

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


        tv_register_designer.setOnClickListener(this)
        tv_forgot_password_designer.setOnClickListener(this)
        btn_login_designer.setOnClickListener(this)
    }

    //لملء البيانات والانتقال من واجهه الLoginActivity الي واجهه الDashboardActivity
    fun designerLoggedInSuccess(designer: Designer) {
        //Hide the progress dialog
        hideProgressDialog()

        if (designer.profileCompleted == 0) {
            //if the user profile is incomplete the launch the ClientProfileActivity.
            val intent = Intent(this@Login_Designers_Activity, Designers_Profile_Activity::class.java)
            intent.putExtra(Constants.EXTRA_DESIGNERS_DETAILS, designer)
            startActivity(intent)
        } else {
            startActivity(Intent(this@Login_Designers_Activity, Products_Activity::class.java))
        }
        finish()
    }

    override fun onClick(v: View?) {
        if (v != null) {
            when (v.id) {

                R.id.tv_register_designer->{
                    val intent=Intent(this@Login_Designers_Activity, Register_Designers_Activity::class.java)
                    startActivity(intent)
                }
                R.id.tv_forgot_password_designer->{
                    val intent=Intent(this@Login_Designers_Activity, Forgotpassword_Designers_Activity::class.java)
                    startActivity(intent)
                }

                R.id.btn_login_designer->{
                    logInRegisteredUser()
                }
            }
        }
    }


    private fun validateLoginDetails():Boolean{
        return when{
            TextUtils.isEmpty(et_email_designer.text.toString().trim{it <= ' '}) ->{
                showErrorSnackBar(resources.getString(R.string.err_msg_enter_email),true)
                false
            }
            TextUtils.isEmpty(et_password_designer.text.toString().trim{it <= ' '}) ->{
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
            val email =et_email_designer.text.toString().trim{it <= ' '}
            val password =et_password_designer.text.toString().trim{it <= ' '}

            //Login Using FirebaseAuth
            FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password)
                .addOnCompleteListener { task->

                    if (task.isSuccessful){
                        FireStoreClass().getDesignersDetails(this@Login_Designers_Activity)
                    }else{
                        showErrorSnackBar(task.exception!!.message.toString(),true)
                    }

                }
        }
    }
}