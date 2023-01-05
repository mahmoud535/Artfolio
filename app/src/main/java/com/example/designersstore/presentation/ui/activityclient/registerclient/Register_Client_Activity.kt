package com.example.designersstore.presentation.ui.activityclient.registerclient

import android.os.Build
import android.os.Bundle
import android.text.TextUtils
import android.view.WindowInsets
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import com.example.designersstore.R
import com.example.designersstore.data.userfirestore.UserFirestoreViewModel
import com.example.designersstore.databinding.ActivityRegisterClientBinding
import com.example.designersstore.domain.models.User
import com.example.designersstore.presentation.ui.activity.BaseActivity
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.android.synthetic.main.activity_register__client_.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class Register_Client_Activity : BaseActivity() {

    private val viewModel: UserFirestoreViewModel by viewModels()
    private val registerClientViewModel: RegisterClientViewModel by viewModels()
    private lateinit var binding: ActivityRegisterClientBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register__client_)
        binding = ActivityRegisterClientBinding.inflate(layoutInflater)

        removeTapeBar()
        setupActionBar()
        setUpView()

        setContentView(binding.root)
    }


    private fun removeTapeBar() {
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

    private fun setUpView() {
        binding.tvLoginClient.setOnClickListener {
            onBackPressed()
        }
        binding.btnRegisterClient.setOnClickListener {
            registerUser()
        }

    }

    private fun setupActionBar() {
        setSupportActionBar(toolbar_register_activity)
        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_black_color_back_24dp)
        }
        toolbar_register_activity.setNavigationOnClickListener { onBackPressed() }
    }

    private fun registerUser() {
        //check with validate function if the entries are valid or not.
        if (validateRegisterDetails()) {
            showProgressDialog(resources.getString(R.string.please_wait))
            val email: String = et_email_Client.text.toString().trim { it <= ' ' }
            val password: String = et_password_Client.text.toString().trim { it <= ' ' }

            //create an instance and create are register a user with email and password
            FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(
                    OnCompleteListener<AuthResult> { task ->
                        if (task.isSuccessful) {
                            // Firebase register user
                            val firebaseUser: FirebaseUser = task.result!!.user!!
                            // Instance of User data model class.
                            val user = User(
                                firebaseUser.uid,
                                et_first_name_Client.text.toString().trim { it <= ' ' },
                                et_last_name_Client.text.toString().trim { it <= ' ' },
                                et_email_Client.text.toString().trim { it <= ' ' }
                            )
                            viewModel.registerUser(this@Register_Client_Activity, user)

                        } else {
                            hideProgressDialog()
                            showErrorSnackBar(task.exception!!.message.toString(), true)
                        }
                    })

            //TODO: Crash when FirebaseAuth from RegisterViewModel
//            registerClientViewModel.infoLiveData.observe(this,
//            Observer {
//
//                // Instance of User data model class.
//
//                val user = User(
//
//                    et_first_name_Client.text.toString().trim { it <= ' ' },
//                    et_last_name_Client.text.toString().trim { it <= ' ' },
//                    et_email_Client.text.toString().trim { it <= ' ' }
//                )
//                viewModel.registerUser(this@Register_Client_Activity, user)
//                registerClientViewModel.registerUser(email,password,user)
//            })
            //todo*******************************************************************
        }

    }

    private fun validateRegisterDetails(): Boolean {
        return when {
            TextUtils.isEmpty(et_first_name_Client.text.toString().trim { it <= ' ' }) -> {
                showErrorSnackBar(resources.getString(R.string.err_msg_enter_first_name), true)
                false
            }
            TextUtils.isEmpty(et_last_name_Client.text.toString().trim { it <= ' ' }) -> {
                showErrorSnackBar(resources.getString(R.string.err_msg_enter_last_name), true)
                false
            }
            TextUtils.isEmpty(et_email_Client.text.toString().trim { it <= ' ' }) -> {
                showErrorSnackBar(resources.getString(R.string.err_msg_enter_email), true)
                false
            }
            TextUtils.isEmpty(et_password_Client.text.toString().trim { it <= ' ' }) -> {
                showErrorSnackBar(resources.getString(R.string.err_msg_enter_password), true)
                false
            }
            TextUtils.isEmpty(et_confirm_password_Client.text.toString().trim { it <= ' ' }) -> {
                showErrorSnackBar(
                    resources.getString(R.string.err_msg_enter_confirm_password),
                    true
                )
                false
            }
            et_password_Client.text.toString()
                .trim { it <= ' ' } != et_confirm_password_Client.text.toString()
                .trim { it <= ' ' } -> {
                showErrorSnackBar(
                    resources.getString(R.string.err_msg_password_and_confirm_password_mismatch),
                    true
                )
                false
            }
            !cb_terms_and_condition_Client.isChecked -> {
                showErrorSnackBar(
                    resources.getString(R.string.err_msg_agree_terms_and_condition),
                    true
                )
                false
            }
            else -> {
                true
            }
        }
    }

    fun userRegistrationSuccess() {
        hideProgressDialog()
        Toast.makeText(
            this@Register_Client_Activity,
            resources.getString(R.string.register_success),
            Toast.LENGTH_SHORT
        ).show()
    }
}