package com.example.designersstore.presentation.ui.activityclient.settingclient

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.fragment.app.Fragment
import com.example.designersstore.R
import com.example.designersstore.data.userfirestore.UserFirestoreViewModel
import com.example.designersstore.databinding.ActivitySettingsBinding
import com.example.designersstore.domain.models.User
import com.example.designersstore.presentation.ui.activity.BaseActivity
import com.example.designersstore.presentation.ui.activityclient.addresslistclient.AddressList_Client
import com.example.designersstore.presentation.ui.activityclient.clientprofile.Client_Profile_Activity
import com.example.designersstore.presentation.utils.Constants
import com.example.designersstore.presentation.utils.GlideLoader
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class SettingsActivity : BaseActivity() {

    private lateinit var mUserDetails: User
    private var fragment: Fragment = Fragment()
    private val viewModel:UserFirestoreViewModel by viewModels()
    private val viewModelClientSetting:SettingClientViewModel by viewModels()
    private lateinit var binding :ActivitySettingsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
       // setContentView(R.layout.activity_settings)
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        GlobalScope.launch(Dispatchers.Main) {
            actions()
        }
        setContentView(binding.root)
    }

    private fun actions(){
        GlobalScope.launch(Dispatchers.Main) {
            setupActionBar()
            setUpView()
        }
    }

    private fun setupActionBar(){
        GlobalScope.launch(Dispatchers.Main) {
            setSupportActionBar(binding.toolbarSettingsActivity)
            val actionBar = supportActionBar
            if (actionBar != null) {
                actionBar.setDisplayHomeAsUpEnabled(true)
                actionBar.setHomeAsUpIndicator(R.drawable.ic_white_color_back_24dp)
            }
            binding.toolbarSettingsActivity.setNavigationOnClickListener { onBackPressed() }
        }
    }

    private fun setUpView(){
        GlobalScope.launch(Dispatchers.Main) {
            binding.tvEdit.setOnClickListener {
                val intent = Intent(this@SettingsActivity, Client_Profile_Activity::class.java)
                intent.putExtra(Constants.EXTRA_USER_DETAILS, mUserDetails)
                startActivity(intent)
            }
            binding.btnLogout.setOnClickListener {
                FirebaseAuth.getInstance().signOut()
                viewModelClientSetting.intentFromBtnLogoutToLogin()
                finish()
            }
            binding.llAddress.setOnClickListener {
                val intent = Intent(this@SettingsActivity, AddressList_Client::class.java)
                startActivity(intent)
            }
        }
    }

    private fun getUserDetails(){
        showProgressDialog(resources.getString(R.string.please_wait))
        viewModel.getUserDetails(this)
    }

    fun userDetailsSuccess(user:User){
        mUserDetails=user
        hideProgressDialog()
        GlideLoader(this@SettingsActivity).loadUserPicture(user.image,binding.ivUserPhoto)
        binding.tvName.text="${user.firstName} ${user.lastName} "
        binding.tvGender.text=user.gender
        binding.tvEmail.text=user.email
        binding.tvMobileNumber.text="${user.mobile}"
    }

    override fun onResume() {
        super.onResume()
        getUserDetails()
    }

}