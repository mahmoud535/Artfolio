package com.example.designersstore.presentation.ui.activityclient.loginclient

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.text.TextUtils
import android.view.WindowInsets
import android.view.WindowManager
import androidx.activity.viewModels
import androidx.fragment.app.Fragment
import com.example.designersstore.R
import com.example.designersstore.data.userfirestore.UserFirestoreViewModel
import com.example.designersstore.databinding.ActivityLoginClientBinding
import com.example.designersstore.domain.models.User
import com.example.designersstore.presentation.ui.activity.BaseActivity
import com.example.designersstore.presentation.ui.activityclient.DashboardActivity
import com.example.designersstore.presentation.ui.activityclient.Forgotpassword_Client_Activity
import com.example.designersstore.presentation.ui.activityclient.registerclient.Register_Client_Activity
import com.example.designersstore.presentation.ui.activityclient.clientprofile.Client_Profile_Activity
import com.example.designersstore.presentation.utils.Constants
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_login__client_.*

class Login_Client_Activity : BaseActivity() {

    private val viewModel: UserFirestoreViewModel by viewModels()
    private lateinit var binding: ActivityLoginClientBinding
    private var fragment:Fragment = Fragment()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login__client_)
        binding = ActivityLoginClientBinding.inflate(layoutInflater)

        actions()
        setContentView(binding.root)
    }

    private fun actions(){
        removeTapeBar()
        setUpView()
    }

    private fun removeTapeBar(){
        //لازاله الشريط اللزي يوجد اعلي واجهه الSplashActivity
        //لجعل الواجهه ملئ الشاشه
        @Suppress("DEPRECATION")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.hide(WindowInsets.Type.statusBars())
        } else {
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        }
    }

    fun userLoggedInSuccess(user: User) {
        hideProgressDialog()
        if (user.profileCompleted == 0) {
            val intent = Intent(this@Login_Client_Activity, Client_Profile_Activity::class.java)
            intent.putExtra(Constants.EXTRA_USER_DETAILS, user)
            startActivity(intent)
        } else {
            startActivity(Intent(this@Login_Client_Activity, DashboardActivity::class.java))
        }
        finish()
    }

    private fun setUpView() {
        binding.tvRegister.setOnClickListener {
            val intent = Intent(this@Login_Client_Activity, Register_Client_Activity::class.java)
            startActivity(intent)
        }
        binding.tvForgotPasswordClient.setOnClickListener {
            val intent =
                Intent(this@Login_Client_Activity, Forgotpassword_Client_Activity::class.java)
            startActivity(intent)
        }
        binding.btnLoginClient.setOnClickListener {
            logInRegisteredUser()
        }
    }

    private fun validateLoginDetails(): Boolean {
        return when {
            TextUtils.isEmpty(et_email_Client.text.toString().trim { it <= ' ' }) -> {
                showErrorSnackBar(resources.getString(R.string.err_msg_enter_email), true)
                false
            }
            TextUtils.isEmpty(et_password_Client.text.toString().trim { it <= ' ' }) -> {
                showErrorSnackBar(resources.getString(R.string.err_msg_enter_password), true)
                false
            }
            else -> {
                true
            }

        }
    }

    private fun logInRegisteredUser() {
        if (validateLoginDetails()) {
            showProgressDialog(resources.getString(R.string.please_wait))
            val email = et_email_Client.text.toString().trim { it <= ' ' }
            val password = et_password_Client.text.toString().trim { it <= ' ' }
            FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        viewModel.getUserDetails(this@Login_Client_Activity)
                    } else {
                        showErrorSnackBar(task.exception!!.message.toString(), true)
                        hideProgressDialog()
                    }
                }
        }
    }
}