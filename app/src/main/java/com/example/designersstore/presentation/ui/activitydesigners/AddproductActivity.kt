package com.example.designersstore.presentation.ui.activitydesigners

import android.app.Activity
import android.content.Context
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
import com.example.designersstore.data.userfirestore.UserFireStore
import com.example.designersstore.data.userfirestore.UserFirestoreViewModel
import com.example.designersstore.databinding.ActivityAddproductBinding
import com.example.designersstore.domain.models.Product
import com.example.designersstore.presentation.ui.activity.BaseActivity
import com.example.designersstore.presentation.utils.Constants
import com.example.designersstore.presentation.utils.GlideLoader
import kotlinx.android.synthetic.main.activity_addproduct.*
import java.io.IOException

class AddproductActivity : BaseActivity(){

    private val viewModel: UserFirestoreViewModel by viewModels()
    private lateinit var binding:ActivityAddproductBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddproductBinding.inflate(layoutInflater)

        setupActionBar()
        setUpView()

        setContentView(binding.root)
    }

    private fun setupActionBar() {
        setSupportActionBar(binding.toolbarAddProductActivity)
        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_white_color_back_24dp)
        }
        binding.toolbarAddProductActivity.setNavigationOnClickListener { onBackPressed() }
    }

    private fun setUpView(){
        binding.ivAddUpdateProduct.setOnClickListener {
            checkImagefPermission()
        }
        binding.btnSubmitAddProduct.setOnClickListener {
            if (validateProductDetails()){
                uploadProductImage()
            }
        }
    }

    private fun checkImagefPermission(){
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED){
            Constants.showImageChooser(this@AddproductActivity)
        }else{
            ActivityCompat.requestPermissions(
                this,
                arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE),
                Constants.READ_STORAGE_PERMISSION_CODE
            )
        }
    }

    private  var mSelectedImageFileUri: Uri?=null
    private fun uploadProductImage(){
        showProgressDialog(resources.getString(R.string.please_wait))
        viewModel.uploadImageToCloudStorage(this,mSelectedImageFileUri,Constants.PRODUCT_IMAGE)
    }

    fun productUploadSuccess(){
        hideProgressDialog()
        Toast.makeText(
            this@AddproductActivity,
            resources.getString(R.string.design_uploaded_success_message),
            Toast.LENGTH_SHORT
        ).show()
        finish()
    }

    private  var mProductImageURL:String=""
    fun imageUploadSuccess(imageURL: String) {
        mProductImageURL=imageURL
        uploadProductDetails()
    }

    //اضافه تفاصيل التصميم في cloud fireStore
    private fun uploadProductDetails(){
        val username=this.getSharedPreferences(
            Constants.DESIGNERSSTORE_PREFERENCES, Context.MODE_PRIVATE)
            .getString(Constants.LOGGED_IN_USERNAME_DESIGNER,"")!!
        val product= Product(
            DesignerFirestore().getCurrentDesignerID(),
            username,
            binding.etProductTitle.text.toString().trim{it<= ' '},
            binding.etProductPrice.text.toString().trim{it<= ' '},
            binding.etProductDescription.text.toString().trim{it<= ' '},
            binding.etProductQuantity.text.toString().trim{it<= ' '},
            mProductImageURL
        )
        viewModel.uploadProductDetails(this,product)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == Constants.READ_STORAGE_PERMISSION_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Constants.showImageChooser(this@AddproductActivity)
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
                    binding.ivAddUpdateProduct.setImageDrawable(ContextCompat.getDrawable(this,R.drawable.ic_vector_edit))
                    setUriImage(data)
                }
            }
        } else if (resultCode == Activity.RESULT_CANCELED) {
            Log.e("Request Cancelled", "Image selection cancelled")
        }
    }

    private fun setUriImage( data: Intent?){
        try {
            // The uri of selected image from phone storage.
            mSelectedImageFileUri = data!!.data!!
            GlideLoader(this).loadUserPicture(mSelectedImageFileUri!!,binding.ivProductImage)
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    private fun validateProductDetails(): Boolean {
        return when {
            mSelectedImageFileUri == null -> {
                showErrorSnackBar(resources.getString(R.string.err_msg_select_design_image), true)
                false
            }
            TextUtils.isEmpty(et_product_title.text.toString().trim { it <= ' ' }) -> {
                showErrorSnackBar(resources.getString(R.string.err_msg_enter_design_title), true)
                false
            }
            TextUtils.isEmpty(et_product_price.text.toString().trim { it <= ' ' }) -> {
                showErrorSnackBar(resources.getString(R.string.err_msg_enter_design_price), true)
                false
            }
            TextUtils.isEmpty(et_product_description.text.toString().trim { it <= ' ' }) -> {
                showErrorSnackBar(
                    resources.getString(R.string.err_msg_enter_design_description),
                    true
                )
                false
            }
            TextUtils.isEmpty(et_product_quantity.text.toString().trim { it <= ' ' }) -> {
                showErrorSnackBar(
                    resources.getString(R.string.err_msg_enter_design_quantity),
                    true
                )
                false
            }
            else -> {
                true
            }
        }
    }
}