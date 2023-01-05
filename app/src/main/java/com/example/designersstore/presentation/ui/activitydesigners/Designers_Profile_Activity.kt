package com.example.designersstore.presentation.ui.activitydesigners

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.designersstore.R
import com.example.designersstore.data.designerfirestore.DesignerFirestore
import com.example.designersstore.data.designerfirestore.DesignerViewModel
import com.example.designersstore.data.userfirestore.UserFireStore
import com.example.designersstore.data.userfirestore.UserFirestoreViewModel
import com.example.designersstore.databinding.ActivityDesignersProfileBinding
import com.example.designersstore.domain.models.Designer
import com.example.designersstore.presentation.ui.activity.BaseActivity
import com.example.designersstore.presentation.utils.Constants
import com.example.designersstore.presentation.utils.GlideLoader
import kotlinx.android.synthetic.main.activity_designers__profile_.*
import java.io.IOException

class Designers_Profile_Activity : BaseActivity() {

    private val viewModelUser: UserFirestoreViewModel by viewModels()
    private val viewModel: DesignerViewModel by viewModels()
    private lateinit var binding: ActivityDesignersProfileBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDesignersProfileBinding.inflate(layoutInflater)

        actions()

        setContentView(binding.root)
    }

    private fun actions() {
        getExtraDetails()
        setPersonInfo()
        checkProfileDetails()
        setUpView()
        setUpActionBar()
    }

    private lateinit var mDesignersDetails: Designer
    private fun getExtraDetails() {
        if (intent.hasExtra(Constants.EXTRA_DESIGNERS_DETAILS)) {
            //Get the user details from intent as a ParcelableExtra.
            mDesignersDetails = intent.getParcelableExtra(Constants.EXTRA_DESIGNERS_DETAILS)!!
        }
    }

    private fun setPersonInfo() {
        binding.etFirstNameDesigner.setText(mDesignersDetails.firstName)
        binding.etLastName.setText(mDesignersDetails.lastName)
        binding.etEmail.isEnabled = false
        binding.etProfession.setText(mDesignersDetails.profession)
        binding.etEmail.setText(mDesignersDetails.email)
    }

    private fun checkProfileDetails() {
        if (mDesignersDetails.profileCompleted == 0) {
            infoCompleted()
        } else {
            completeOtherInfo()
            selectGender()
        }
    }

    private fun infoCompleted() {
        binding.tvTitle.text = resources.getString(R.string.title_complete_profile)
        binding.etFirstNameDesigner.isEnabled = false
        binding.etLastName.isEnabled = false
        binding.etProfession.isEnabled = false
    }

    private fun completeOtherInfo() {
        binding.tvTitle.text = resources.getString(R.string.title_edit_profile)
        GlideLoader(this@Designers_Profile_Activity).loadUserPicture(
            mDesignersDetails.image,
            binding.ivUserPhoto
        )
        if (mDesignersDetails.mobile != 0L) {
            binding.etMobileNumber.setText(mDesignersDetails.mobile.toString())
        }
    }

    private fun selectGender() {
        if (mDesignersDetails.gender == Constants.MALE) {
            binding.rbMale.isChecked = true
        } else {
            binding.rbFemale.isChecked = true
        }
    }

    private fun setUpActionBar() {
        setSupportActionBar(binding.toolbarUserProfileActivity)
        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_white_color_back_24dp)
        }
        binding.toolbarUserProfileActivity.setNavigationOnClickListener { onBackPressed() }
    }

    private var mSelectedImageFileUri: Uri? = null
    private fun setUpView() {
        binding.ivUserPhoto.setOnClickListener {
            requestLicenseToUploadImages()
        }
        binding.btnSubmit.setOnClickListener {
            submitPersonalInfo()
        }
    }

    private fun requestLicenseToUploadImages() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
            == PackageManager.PERMISSION_GRANTED
        ) {
            Constants.showImageChooser(this)
        } else {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                Constants.READ_STORAGE_PERMISSION_CODE
            )
        }
    }

    private fun submitPersonalInfo() {
        if (validateDesignerProfileDetails()) {
            showProgressDialog(resources.getString(R.string.please_wait))
            if (mSelectedImageFileUri != null) {
                uploadImageToCloudStorage()
            } else {
                // TODO Step 4: Move this piece of code to the separate function as the profile image is optional. So, if the image is uploaded then we will update the image URL in the firestore.
                updateDesignerProfileDetails()
            }
        }
    }

    private fun uploadImageToCloudStorage() {
        viewModelUser.uploadImageToCloudStorage(
            this@Designers_Profile_Activity,
            mSelectedImageFileUri,
            Constants.USER_PROFILE_IMAGE
        )
    }

    private fun validateDesignerProfileDetails(): Boolean {
        return when {
            TextUtils.isEmpty(et_mobile_number.text.toString().trim { it <= ' ' }) -> {
                showErrorSnackBar(resources.getString(R.string.err_msg_enter_mobile_number), true)
                false
            }
            else -> {
                true
            }
        }
    }

    private var mDesignerProfileImageURL: String = ""
    private fun updateDesignerProfileDetails() {
        val userHashMap = HashMap<String, Any>()
        val firstname = binding.etFirstNameDesigner.text.toString().trim { it <= ' ' }

        if (firstname != mDesignersDetails.firstName) {
            userHashMap[Constants.FIRST_NAME] = firstname
        }
        val lastname = binding.etLastName.text.toString().trim { it <= ' ' }
        if (lastname != mDesignersDetails.lastName) {
            userHashMap[Constants.LAST_NAME] = lastname
        }
        val profession = binding.etProfession.text.toString().trim { it <= ' ' }
        if (profession != mDesignersDetails.profession) {
            userHashMap[Constants.PROFESSION] = profession
        }
        val mobileNumber = binding.etMobileNumber.text.toString().trim { it <= ' ' }
        val gender = if (rb_male.isChecked) {
            Constants.MALE
        } else {
            Constants.FEMALE
        }
        if (mDesignerProfileImageURL.isNotEmpty()) {
            userHashMap[Constants.IMAGE] = mDesignerProfileImageURL
        }
        if (mobileNumber.isNotEmpty() && mobileNumber != mDesignersDetails.mobile.toString()) {
            userHashMap[Constants.MOBILE] = mobileNumber.toLong()
        }
        if (gender.isNotEmpty() && gender != mDesignersDetails.gender) {
            userHashMap[Constants.GENDER] = gender
        }
        userHashMap[Constants.GENDER] = gender
        userHashMap[Constants.COMPLETE_PROFILE] = 1

        updateDesignerProfileData(userHashMap)
    }

    private fun updateDesignerProfileData(userHashMap: HashMap<String, Any>) {
        viewModel.updateDesignerProfileData(
            this@Designers_Profile_Activity,
            userHashMap
        )
    }

    fun designerProfileUpdateSuccess() {
        hideProgressDialog()
        Toast.makeText(
            this@Designers_Profile_Activity,
            resources.getString(R.string.msg_profile_update_success),
            Toast.LENGTH_SHORT
        ).show()
        startActivity(Intent(this@Designers_Profile_Activity, Products_Activity::class.java))
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
                Constants.showImageChooser(this@Designers_Profile_Activity)
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
                        GlideLoader(this).loadUserPicture(mSelectedImageFileUri!!, iv_user_photo)
                    } catch (e: IOException) {
                        e.printStackTrace()
                        Toast.makeText(
                            this@Designers_Profile_Activity,
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
        mDesignerProfileImageURL = imageURL
        updateDesignerProfileDetails()
    }
}