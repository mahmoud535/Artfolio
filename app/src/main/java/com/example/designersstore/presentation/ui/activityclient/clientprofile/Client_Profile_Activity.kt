package com.example.designersstore.presentation.ui.activityclient.clientprofile

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.widget.Toast
import androidx.activity.viewModels
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.designersstore.R
import com.example.designersstore.data.userfirestore.UserFirestoreViewModel
import com.example.designersstore.databinding.ActivityClientProfileBinding
import com.example.designersstore.domain.models.User
import com.example.designersstore.presentation.ui.activity.BaseActivity
import com.example.designersstore.presentation.ui.activityclient.DashboardActivity
import com.example.designersstore.presentation.utils.Constants
import com.example.designersstore.presentation.utils.GlideLoader
import com.squareup.okhttp.Dispatcher
import kotlinx.android.synthetic.main.activity_client__profile_.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.io.IOException

class Client_Profile_Activity : BaseActivity(){

    private val viewModel:UserFirestoreViewModel by viewModels()
    private lateinit var binding: ActivityClientProfileBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityClientProfileBinding.inflate(layoutInflater)

        actions()

        setContentView(binding.root)
    }

    private fun actions(){
        setUpView()
        putExtra()
        GlobalScope.launch(Dispatchers.Main){
            setProfileDetails()
            setUpActionBar()
        }
    }

    private lateinit var mUserDetails: User
    private fun putExtra(){
        if (intent.hasExtra(Constants.EXTRA_USER_DETAILS)){
            mUserDetails=intent.getParcelableExtra(Constants.EXTRA_USER_DETAILS)!!
        }
    }

    private fun setUpView(){
        binding.ivUserPhoto.setOnClickListener {
            checkImagePermission()
        }
        binding.btnSubmit.setOnClickListener {
            submitProfileDetails()
        }
    }

    private fun setProfileDetails(){
        GlobalScope.launch(Dispatchers.Main) {
            binding.etFirstName.setText(mUserDetails.firstName)
            binding.etLastName.setText(mUserDetails.lastName)
            binding.etEmail.isEnabled = false
            binding.etEmail.setText(mUserDetails.email)

            if (mUserDetails.profileCompleted == 0) {
                binding.tvTitle.text = resources.getString(R.string.title_complete_profile)
                binding.etFirstName.isEnabled = false
                binding.etLastName.isEnabled = false
            } else {

                binding.tvTitle.text = resources.getString(R.string.title_edit_profile)
                GlideLoader(this@Client_Profile_Activity).loadUserPicture(
                    mUserDetails.image,
                    binding.ivUserPhoto
                )
                if (mUserDetails.mobile != 0L) {
                    binding.etMobileNumber.setText(mUserDetails.mobile.toString())
                }
                if (mUserDetails.gender == Constants.MALE) {
                    binding.rbMale.isChecked = true
                } else {
                    binding.rbFemale.isChecked = true
                }
            }
        }
    }


    private fun checkImagePermission(){
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
            == PackageManager.PERMISSION_GRANTED){
            Constants.showImageChooser(this)
        }else{
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                Constants.READ_STORAGE_PERMISSION_CODE
            )
        }
    }

    private  var mSelectedImageFileUri: Uri?=null
    private fun submitProfileDetails(){
        if (validateUserProfileDetails()){
            showProgressDialog(resources.getString(R.string.please_wait))
            if (mSelectedImageFileUri != null){
                uploadImageToCloudStorage()
            }else{
                updateUserProfileDetails()
            }
        }
    }

    private fun validateUserProfileDetails():Boolean{
        return when{
            TextUtils.isEmpty(binding.etMobileNumber.text.toString().trim{it<=' '})->{
                showErrorSnackBar(resources.getString(R.string.err_msg_enter_mobile_number),true)
                false
            }
            else->{
                true
            }
        }
    }

    private fun uploadImageToCloudStorage(){
        viewModel.uploadImageToCloudStorage(
            this@Client_Profile_Activity,
            mSelectedImageFileUri,
            Constants.USER_PROFILE_IMAGE)
    }

    private  var mUserProfileImageURL:String=""
    private fun updateUserProfileDetails() {
        val userHashMap=HashMap<String,Any>()
        val firstname=binding.etFirstName.text.toString().trim{it<=' '}
        val lastname=binding.etLastName.text.toString().trim{it<=' '}
        val mobileNumber = binding.etMobileNumber.text.toString().trim { it <= ' ' }

        if (firstname != mUserDetails.firstName){
            userHashMap[Constants.FIRST_NAME]=firstname
        }
        if (lastname != mUserDetails.lastName){
            userHashMap[Constants.LAST_NAME]=lastname
        }
        val gender = if (binding.rbMale.isChecked) {
            Constants.MALE
        } else {
            Constants.FEMALE
        }
        if (mUserProfileImageURL.isNotEmpty()) {
            userHashMap[Constants.IMAGE] = mUserProfileImageURL
        }
        if (mobileNumber.isNotEmpty()&&mobileNumber!=mUserDetails.mobile.toString()) {
            userHashMap[Constants.MOBILE] = mobileNumber.toLong()
        }
        if (gender.isNotEmpty()&& gender!=mUserDetails.gender) {
            userHashMap[Constants.GENDER] =gender
        }
        userHashMap[Constants.GENDER] = gender
        userHashMap[Constants.COMPLETE_PROFILE] = 1

        updateUserProfileData(userHashMap)
    }

    private fun updateUserProfileData(userHashMap:HashMap<String,Any>){
        viewModel.updateUserProfileData(
            this@Client_Profile_Activity,
            userHashMap
        )
    }

    private fun setUpActionBar(){
        GlobalScope.launch(Dispatchers.Main) {
            setSupportActionBar(toolbar_user_profile_activity)
            val actionBar = supportActionBar
            if (actionBar != null) {
                actionBar.setDisplayHomeAsUpEnabled(true)
                actionBar.setHomeAsUpIndicator(R.drawable.ic_white_color_back_24dp)
            }
            toolbar_user_profile_activity.setNavigationOnClickListener { onBackPressed() }
        }
    }


    fun userProfileUpdateSuccess(){
        hideProgressDialog()
        Toast.makeText(
            this@Client_Profile_Activity,
            resources.getString(R.string.msg_profile_update_success),
            Toast.LENGTH_SHORT
        ).show()
        startActivity(Intent(this@Client_Profile_Activity, DashboardActivity::class.java))
        finish()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == Constants.READ_STORAGE_PERMISSION_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Constants.showImageChooser(this@Client_Profile_Activity)
            } else {
                Toast.makeText(
                    this,
                    resources.getString(R.string.read_storage_permission_denied),
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == Constants.PICK_IMAGE_REQUEST_CODE) {
                if (data != null) {
                    try {
                        mSelectedImageFileUri = data.data!!
                        GlideLoader(this).loadUserPicture(mSelectedImageFileUri!!,binding.ivUserPhoto)
                    } catch (e: IOException) {
                        e.printStackTrace()
                        Toast.makeText(
                            this@Client_Profile_Activity,
                            resources.getString(R.string.image_selection_failed),
                            Toast.LENGTH_SHORT
                        )
                            .show()
                    }
                }
            }
        } else if (resultCode == Activity.RESULT_CANCELED) {
            Log.e("Request Cancelled", "Image selection cancelled")
        }
    }

    fun imageUploadSuccess(imageURL: String) {
        mUserProfileImageURL=imageURL
        updateUserProfileDetails()
    }
}