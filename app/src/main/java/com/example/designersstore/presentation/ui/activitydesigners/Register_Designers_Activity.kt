package com.example.designersstore.presentation.ui.activitydesigners

import android.os.Build
import android.os.Bundle
import android.text.TextUtils
import android.view.WindowInsets
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.viewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.example.designersstore.R
import com.example.designersstore.data.designerfirestore.DesignerFirestore
import com.example.designersstore.data.designerfirestore.DesignerViewModel
import com.example.designersstore.databinding.ActivityLoginDesignersBinding
import com.example.designersstore.databinding.ActivityRegisterDesignersBinding
import com.example.designersstore.domain.models.Designer
import com.example.designersstore.presentation.ui.activity.BaseActivity
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.android.synthetic.main.activity_designers__profile_.*
import kotlinx.android.synthetic.main.activity_register__designers_.*
import kotlinx.android.synthetic.main.activity_register__designers_.et_profession


class Register_Designers_Activity : BaseActivity() {

    private val viewModel: DesignerViewModel by viewModels()
    private lateinit var binding: ActivityRegisterDesignersBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterDesignersBinding.inflate(layoutInflater)

        actions()

        setContentView(binding.root)
    }

    fun actions() {
        onClick()
    }

    private fun onClick() {
        binding.tvLoginDesigner.setOnClickListener {
            onBackPressed()
        }
        binding.btnRegisterDesigner.setOnClickListener {
            registerUser()
        }
        binding.backBtn.setOnClickListener {
            onBackPressed()
        }
    }

    private fun validateRegisterDetails(): Boolean {
        return when {
            TextUtils.isEmpty(binding.etFirstNameDesigner.text.toString().trim { it <= ' ' }) -> {
                showErrorSnackBar(resources.getString(R.string.err_msg_enter_first_name), true)
                false
            }
            TextUtils.isEmpty(binding.etLastNameDesigner.text.toString().trim { it <= ' ' }) -> {
                showErrorSnackBar(resources.getString(R.string.err_msg_enter_last_name), true)
                false
            }
            TextUtils.isEmpty(binding.etEmailDesigner.text.toString().trim { it <= ' ' }) -> {
                showErrorSnackBar(resources.getString(R.string.err_msg_enter_email), true)
                false
            }
            TextUtils.isEmpty(binding.etProfession.text.toString().trim { it <= ' ' }) -> {
                showErrorSnackBar(resources.getString(R.string.err_msg_enter_profession), true)
                false
            }
            TextUtils.isEmpty(binding.etPasswordDesigner.text.toString().trim { it <= ' ' }) -> {
                showErrorSnackBar(resources.getString(R.string.err_msg_enter_password), true)
                false
            }
            TextUtils.isEmpty(
                binding.etConfirmPasswordDesigner.text.toString().trim { it <= ' ' }) -> {
                showErrorSnackBar(
                    resources.getString(R.string.err_msg_enter_confirm_password),
                    true
                )
                false
            }
            binding.etPasswordDesigner.text.toString()
                .trim { it <= ' ' } != binding.etConfirmPasswordDesigner.text.toString()
                .trim { it <= ' ' } -> {
                showErrorSnackBar(
                    resources.getString(R.string.err_msg_password_and_confirm_password_mismatch),
                    true
                )
                false
            }
            !binding.cbTermsAndConditionDesigner.isChecked -> {
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

    private fun registerUser() {
        if (validateRegisterDetails()) {
            showProgressDialog(resources.getString(R.string.please_wait))
            val email: String = binding.etEmailDesigner.text.toString().trim { it <= ' ' }
            val password: String = binding.etPasswordDesigner.text.toString().trim { it <= ' ' }

            FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(
                    OnCompleteListener<AuthResult> { task ->
                        if (task.isSuccessful) {
                            val firebaseUser: FirebaseUser = task.result!!.user!!
                            val designer = Designer(
                                firebaseUser.uid,
                                binding.etFirstNameDesigner.text.toString().trim { it <= ' ' },
                                binding.etLastNameDesigner.text.toString().trim { it <= ' ' },
                                binding.etEmailDesigner.text.toString().trim { it <= ' ' },
                                binding.etProfession.text.toString().trim { it <= ' ' }
                            )
                            viewModel.registerDesigner(
                                this@Register_Designers_Activity,
                                designer
                            )
                        } else {
                            hideProgressDialog()
                            showErrorSnackBar(task.exception!!.message.toString(), true)
                        }
                    })
        }
    }

    fun designerRegistrationSuccess() {
        hideProgressDialog()
        Toast.makeText(
            this@Register_Designers_Activity,
            resources.getString(R.string.register_success),
            Toast.LENGTH_SHORT
        ).show()
    }
}