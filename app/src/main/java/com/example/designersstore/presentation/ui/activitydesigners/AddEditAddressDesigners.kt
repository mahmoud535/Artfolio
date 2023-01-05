package com.example.designersstore.presentation.ui.activitydesigners

import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import com.example.designersstore.R
import com.example.designersstore.data.designerfirestore.DesignerViewModel
import com.example.designersstore.data.userfirestore.UserFireStore
import com.example.designersstore.databinding.AddEditAddressDesignersBinding
import com.example.designersstore.domain.models.AddressClient
import com.example.designersstore.presentation.ui.activity.BaseActivity
import com.example.designersstore.presentation.utils.Constants
import kotlinx.android.synthetic.main.add_edit_address_designers.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AddEditAddressDesigners : BaseActivity() {

    private val viewModel: DesignerViewModel by viewModels()
    lateinit var binding:AddEditAddressDesignersBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
         binding = AddEditAddressDesignersBinding.inflate(layoutInflater)

        GlobalScope.launch {
            setupActionBar()
            actions()
        }
        setUpView()
        setContentView(binding.root)
    }

    private fun actions() {
        GlobalScope.launch {
            parcelableAddress()
            displayAddress()
        }
    }

    private fun parcelableAddress() {
        if (intent.hasExtra(Constants.EXTRA_ADDRESS_DETAILS_DESIGNER)) {
            mAddressDetails =
                intent.getParcelableExtra(Constants.EXTRA_ADDRESS_DETAILS_DESIGNER)!!
        }
    }

    private fun setUpView() {
        binding.btnSubmitAddressDesigner.setOnClickListener { saveAddressToFirestore() }
        binding.rgTypeDesigner.setOnCheckedChangeListener { _, checkedId ->
            if (checkedId == R.id.rb_other_Designer) {
                binding.tilOtherDetailsDesigner.visibility = View.VISIBLE
            } else {
                binding.tilOtherDetailsDesigner.visibility = View.GONE
            }
        }
    }

    private fun setupActionBar(){
        GlobalScope.launch {
            setSupportActionBar(binding.toolbarAddEditAddressActivityDesigner)
            val actionBar = supportActionBar
            if (actionBar != null) {
                actionBar.setDisplayHomeAsUpEnabled(true)
                actionBar.setHomeAsUpIndicator(R.drawable.ic_white_color_back_24dp)
            }
            binding.toolbarAddEditAddressActivityDesigner.setNavigationOnClickListener { onBackPressed() }
        }
    }

    private var mAddressDetails:AddressClient?=null
    private suspend fun displayAddress() {
        withContext(Dispatchers.Main) {
            if (mAddressDetails != null) {
                if (mAddressDetails!!.id.isNotEmpty()) {
                    setText()
                    when (mAddressDetails?.type) {
                        Constants.HOME_DESIGNER -> {
                            binding.rbHomeDesigner.isChecked = true
                        }
                        Constants.OFFICE_DESIGNER -> {
                            binding.rbOfficeDesigner.isChecked = true
                        }
                        else -> {
                            binding.rbOtherDesigner.isChecked = true
                            binding.tilOtherDetailsDesigner.visibility = View.VISIBLE
                            binding.etOtherDetailsDesigner.setText(mAddressDetails?.otherDetails)
                        }
                    }
                }
            }
        }
    }

    private suspend fun setText() {
        withContext(Dispatchers.Main) {
           // binding.tvTitleDesigner.text = resources.getString(R.string.title_edit_address)
            binding.btnSubmitAddressDesigner.text = resources.getString(R.string.btn_lbl_update)
            binding.etFullNameDesigner.setText(mAddressDetails?.name)
            binding.etPhoneNumberDesigner.setText(mAddressDetails?.mobileNumber)
            binding.etAddressDesigner.setText(mAddressDetails?.address)
            binding.etZipCodeDesigner.setText(mAddressDetails?.zipCode)
            binding.etAdditionalNoteDesigner.setText(mAddressDetails?.additionalNote)
        }
    }


    private fun saveAddressToFirestore(){
        val fullName:String=binding.etFullNameDesigner.text.toString().trim(){it<=' '}
        val phoneNumber: String = binding.etPhoneNumberDesigner.text.toString().trim { it <= ' ' }
        val address: String = binding.etAddressDesigner.text.toString().trim { it <= ' ' }
        val zipCode: String = binding.etZipCodeDesigner.text.toString().trim { it <= ' ' }
        val additionalNote: String = binding.etAdditionalNoteDesigner.text.toString().trim { it <= ' ' }
        val otherDetails: String = binding.etOtherDetailsDesigner.text.toString().trim { it <= ' ' }
        if (validateData()){
            showProgressDialog(resources.getString(R.string.please_wait))
            val addressType: String = when {
                rb_home_Designer.isChecked -> {
                    Constants.HOME_DESIGNER
                }
                rb_office_Designer.isChecked -> {
                    Constants.OFFICE_DESIGNER
                }
                else -> {
                    Constants.OTHER_DESIGNER
                }
            }
            val addressModel= AddressClient(
                UserFireStore().getCurrentUserID(),
                fullName,
                phoneNumber,
                address,
                zipCode,
                additionalNote,
                addressType,
                otherDetails
            )

            if (mAddressDetails != null && mAddressDetails!!.id.isNotEmpty()){
               viewModel.updateAddressDesigner(this,addressModel,mAddressDetails!!.id)
            }else{
                viewModel.addAddressDesigner(this,addressModel)
            }
        }
    }

    fun addUpdateAddressSuccess() {
        hideProgressDialog()
        val notifySuccessMessage:String=if(mAddressDetails != null && mAddressDetails!!.id.isNotEmpty()){
            resources.getString(R.string.msg_your_address_updated_successfully)
        }else{
            resources.getString(R.string.err_your_address_added_successfully)
        }
        Toast.makeText(
            this@AddEditAddressDesigners,
            notifySuccessMessage,
            Toast.LENGTH_SHORT
        ).show()
        setResult(RESULT_OK)
        finish()
    }

    private fun validateData(): Boolean {
        return when {
            TextUtils.isEmpty(et_full_name_Designer.text.toString().trim { it <= ' ' }) -> {
                showErrorSnackBar(
                    resources.getString(R.string.err_msg_please_enter_full_name),
                    true
                )
                false
            }
            TextUtils.isEmpty(et_phone_number_Designer.text.toString().trim { it <= ' ' }) -> {
                showErrorSnackBar(
                    resources.getString(R.string.err_msg_please_enter_phone_number),
                    true
                )
                false
            }
            TextUtils.isEmpty(et_address_Designer.text.toString().trim { it <= ' ' }) -> {
                showErrorSnackBar(resources.getString(R.string.err_msg_please_enter_address), true)
                false
            }
            TextUtils.isEmpty(et_zip_code_Designer.text.toString().trim { it <= ' ' }) -> {
                showErrorSnackBar(resources.getString(R.string.err_msg_please_enter_zip_code), true)
                false
            }
            rb_other_Designer.isChecked && TextUtils.isEmpty(
                et_zip_code_Designer.text.toString().trim { it <= ' ' }) -> {
                showErrorSnackBar(resources.getString(R.string.err_msg_please_enter_zip_code), true)
                false
            }
            else -> {
                true
            }
        }
    }
}