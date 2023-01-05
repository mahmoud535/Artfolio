package com.example.designersstore.presentation.ui.activitydesigners

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.view.WindowInsets
import android.view.WindowManager
import androidx.activity.viewModels
import com.example.designersstore.R
import com.example.designersstore.data.designerfirestore.DesignerFirestore
import com.example.designersstore.data.designerfirestore.DesignerViewModel
import com.example.designersstore.databinding.ActivityLoginDesignersBinding
import com.example.designersstore.domain.models.Designer
import com.example.designersstore.presentation.ui.activity.BaseActivity
import com.example.designersstore.presentation.utils.Constants
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_login__designers_.*

class Login_Designers_Activity : BaseActivity() {

    private val viewModel: DesignerViewModel by viewModels()
    private lateinit var binding: ActivityLoginDesignersBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginDesignersBinding.inflate(layoutInflater)

        removeTapeBar()
        seUpView()

        setContentView(binding.root)
    }

    private fun removeTapeBar() {
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

    fun designerLoggedInSuccess(designer: Designer) {
        hideProgressDialog()
        if (designer.profileCompleted == 0) {
            val intent =
                Intent(this@Login_Designers_Activity, Designers_Profile_Activity::class.java)
            intent.putExtra(Constants.EXTRA_DESIGNERS_DETAILS, designer)
            startActivity(intent)
        } else {
            startActivity(Intent(this@Login_Designers_Activity, Products_Activity::class.java))
        }
        finish()
    }

    private fun seUpView() {
        binding.tvRegisterDesigner.setOnClickListener {
            val intent =
                Intent(this@Login_Designers_Activity, Register_Designers_Activity::class.java)
            startActivity(intent)
        }
        binding.tvForgotPasswordDesigner.setOnClickListener {
            val intent =
                Intent(this@Login_Designers_Activity, Forgotpassword_Designers_Activity::class.java)
            startActivity(intent)
        }
        binding.btnLoginDesigner.setOnClickListener {
            logInRegisteredUser()
        }
    }

    private fun logInRegisteredUser() {
        if (validateLoginDetails()) {
            showProgressDialog(resources.getString(R.string.please_wait))
            val email = et_email_designer.text.toString().trim { it <= ' ' }
            val password = et_password_designer.text.toString().trim { it <= ' ' }
            FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        viewModel.getDesignersDetails(this@Login_Designers_Activity)
                    } else {
                        showErrorSnackBar(task.exception!!.message.toString(), true)
                        hideProgressDialog()
                    }
                }
        }
    }

    private fun validateLoginDetails(): Boolean {
        return when {
            TextUtils.isEmpty(et_email_designer.text.toString().trim { it <= ' ' }) -> {
                showErrorSnackBar(resources.getString(R.string.err_msg_enter_email), true)
                false
            }
            TextUtils.isEmpty(et_password_designer.text.toString().trim { it <= ' ' }) -> {
                showErrorSnackBar(resources.getString(R.string.err_msg_enter_password), true)
                false
            }
            else -> {
                true
            }

        }
    }
}