package com.example.designersstore.presentation.ui.activityclient

import android.os.Bundle
import android.widget.Toast
import com.example.designersstore.R
import com.example.designersstore.databinding.ActivityForgotpasswordClientBinding
import com.example.designersstore.presentation.ui.activity.BaseActivity
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_forgotpassword__client_.*
import kotlinx.coroutines.*

class Forgotpassword_Client_Activity : BaseActivity() {

    private lateinit var binding: ActivityForgotpasswordClientBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityForgotpasswordClientBinding.inflate(layoutInflater)

        GlobalScope.launch(Dispatchers.IO) {
            withContext(Dispatchers.Main) {
                setupActionBar()
            }
            setUpView()
        }
        setContentView(binding.root)
    }

    private fun setupActionBar() {
        GlobalScope.launch(Dispatchers.Main) {
            setSupportActionBar(toolbar_forgot_password_activity)
            val actionBar = supportActionBar
            if (actionBar != null) {
                actionBar.setDisplayHomeAsUpEnabled(true)
                actionBar.setHomeAsUpIndicator(R.drawable.ic_black_color_back_24dp)
            }
            toolbar_forgot_password_activity.setNavigationOnClickListener { onBackPressed() }
        }
    }

    private fun setUpView() {
        GlobalScope.launch(Dispatchers.IO) {
            binding.btnSubmit.setOnClickListener {
                val email: String = binding.etEmail.text.toString().trim { it <= ' ' }
                if (email.isEmpty()) {
                    showErrorSnackBar(resources.getString(R.string.err_msg_enter_email), true)
                } else {
                    showProgressDialog(resources.getString(R.string.please_wait))
                    sendPasswordResetEmail(email)
                }
            }
        }
    }

    private fun sendPasswordResetEmail(email: String) {
        GlobalScope.launch(Dispatchers.IO) {
            FirebaseAuth.getInstance().sendPasswordResetEmail(email)
                .addOnCompleteListener { task ->
                    hideProgressDialog()
                    if (task.isSuccessful) {
                        Toast.makeText(
                            this@Forgotpassword_Client_Activity,
                            resources.getString(R.string.email_sent_success),
                            Toast.LENGTH_LONG
                        ).show()
                        finish()
                    } else {
                        showErrorSnackBar(task.exception!!.message.toString(), true)
                    }
                }
        }
    }


}