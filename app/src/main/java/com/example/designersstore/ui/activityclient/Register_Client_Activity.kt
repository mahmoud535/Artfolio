package com.example.designersstore.ui.activityclient

import android.os.Build
import android.os.Bundle
import android.text.TextUtils
import android.view.WindowInsets
import android.view.WindowManager
import android.widget.Toast
import com.example.designersstore.R
import com.example.designersstore.firestore.FireStoreClass
import com.example.designersstore.models.User
import com.example.designersstore.ui.activity.BaseActivity
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.android.synthetic.main.activity_client__profile_.*
import kotlinx.android.synthetic.main.activity_register__client_.*

class Register_Client_Activity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register__client_)


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
        setupActionBar()

        tv_login_Client.setOnClickListener {
            onBackPressed()
        }

        btn_register_Client.setOnClickListener {
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

    /**
     * A function to validate the entries of a new user.
     */
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

            et_password_Client.text.toString().trim { it <= ' ' } != et_confirm_password_Client.text.toString()
                    .trim { it <= ' ' } -> {
                showErrorSnackBar(
                        resources.getString(R.string.err_msg_password_and_confirm_password_mismatch),
                        true
                )
                false
            }
            !cb_terms_and_condition_Client.isChecked -> {
                showErrorSnackBar(resources.getString(R.string.err_msg_agree_terms_and_condition), true)

                false
            }
            else -> {
                //showErrorSnackBar(resources.getString(R.string. registery_successfull),false)
                true
            }
        }
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

                                //IF THE Register is successful done
                                if (task.isSuccessful) {
                                    // Firebase register user
                                    val firebaseUser:FirebaseUser=task.result!!.user!!

                                    // Instance of User data model class.
                                    val user= User(
                                        firebaseUser.uid,
                                        et_first_name_Client.text.toString().trim{it<= ' '},
                                        et_last_name_Client.text.toString().trim { it<=' ' },
                                        et_email_Client.text.toString().trim{it <= ' '}
                                    )

                                    // Pass the required values in the constructor.
                                    FireStoreClass().registerUser(this@Register_Client_Activity,user)


                                }else{
                                    hideProgressDialog()
                                    //If the Register is not successful then show error message.
                                    showErrorSnackBar(task.exception!!.message.toString(),true)
                                }
                            })
        }
    }

    fun userRegistrationSuccess(){
        //Hide the progress dialog
        hideProgressDialog()

        Toast.makeText(
            this@Register_Client_Activity,
            resources.getString(R.string.register_success),
            Toast.LENGTH_SHORT
        ).show()
    }
}