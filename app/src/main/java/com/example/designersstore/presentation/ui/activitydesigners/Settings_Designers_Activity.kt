package com.example.designersstore.presentation.ui.activitydesigners

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import com.example.designersstore.R
import com.example.designersstore.data.designerfirestore.DesignerViewModel
import com.example.designersstore.databinding.ActivitySettingsDesignersBinding
import com.example.designersstore.domain.models.Designer
import com.example.designersstore.presentation.ui.activity.BaseActivity
import com.example.designersstore.presentation.ui.activitydesigners.addressdesigner.AddressList_Designer
import com.example.designersstore.presentation.utils.Constants
import com.example.designersstore.presentation.utils.GlideLoader
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_settings__designers_.*

class Settings_Designers_Activity : BaseActivity(){

    private val viewModel: DesignerViewModel by viewModels()
    private lateinit var binding: ActivitySettingsDesignersBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings__designers_)
        binding = ActivitySettingsDesignersBinding.inflate(layoutInflater)

        setupActionBar()
        setUpView()
        setContentView(binding.root)
    }

    private fun setupActionBar() {
        setSupportActionBar(binding.toolbarSettingsActivityDesigner)
        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_white_color_back_24dp)
        }
        binding.toolbarSettingsActivityDesigner.setNavigationOnClickListener { onBackPressed() }
    }

    private fun getDesignerDetails() {
        showProgressDialog(resources.getString(R.string.please_wait))
        viewModel.getDesignersDetails(this)
    }

    private lateinit var mDesignerDetails: Designer
    fun designerDetailsSuccess(designer: Designer) {
        mDesignerDetails = designer
        hideProgressDialog()
        GlideLoader(this@Settings_Designers_Activity).loadUserPicture(
            designer.image,
            iv_Designer_photo
        )
        binding.tvNameDesigner.text = "${designer.firstName}${designer.lastName}"
        binding.tvProfessionDesigner.text = designer.profession
        binding.tvGenderDesigner.text = designer.gender
        binding.tvEmailDesigner.text = designer.email
        binding.tvMobileNumberDesigner.text = "${designer.mobile}"
    }

    override fun onResume() {
        super.onResume()
        getDesignerDetails()
    }

    private fun setUpView() {
        binding.tvEditDesigners.setOnClickListener {
            gotoDesignerProfile()
        }
        binding.btnLogoutDesigner.setOnClickListener {
            gotoLoginDesigner()
        }
        binding.llAddressDesigner.setOnClickListener {
            gotoAddressDesigner()
        }
    }

    private fun gotoDesignerProfile() {
        val intent =
            Intent(this@Settings_Designers_Activity, Designers_Profile_Activity::class.java)
        intent.putExtra(Constants.EXTRA_DESIGNERS_DETAILS, mDesignerDetails)
        startActivity(intent)
    }

    private fun gotoLoginDesigner() {
        FirebaseAuth.getInstance().signOut()
        val intent = Intent(this@Settings_Designers_Activity, Login_Designers_Activity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }

    private fun gotoAddressDesigner() {
        val intent = Intent(
            this@Settings_Designers_Activity,
            AddressList_Designer::class.java
        )
        startActivity(intent)
    }
}