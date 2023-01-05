package com.example.designersstore.presentation.ui.activityclient.addeditaddressactivity

import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import com.example.designersstore.R
import com.example.designersstore.data.userfirestore.UserFireStore
import com.example.designersstore.data.userfirestore.UserFirestoreViewModel
import com.example.designersstore.databinding.AddEditAddressBinding
import com.example.designersstore.domain.models.AddressClient
import com.example.designersstore.presentation.ui.activity.BaseActivity
import com.example.designersstore.presentation.utils.Constants
import kotlinx.android.synthetic.main.add_edit_address.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AddEditAddress : BaseActivity() {

    private val viewModel: UserFirestoreViewModel by viewModels()
    private val viewModelAddEditAddress: AddressViewModel by viewModels()
    lateinit var binding: AddEditAddressBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = AddEditAddressBinding.inflate(layoutInflater)
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
        if (intent.hasExtra(Constants.EXTRA_ADDRESS_DETAILS_CLIENT)) {
            mAddressDetails =
                intent.getParcelableExtra(Constants.EXTRA_ADDRESS_DETAILS_CLIENT)!!
        }
//            viewModelAddEditAddress.parcelableAddressLiveData.observe(this, { parcelable ->
//                  binding.etFullNameClient.setText(parcelable)
//            })
    }

    private fun setUpView() {
        binding.btnSubmitAddressClient.setOnClickListener { saveAddressToFirestoreClient() }
        binding.rgTypeClient.setOnCheckedChangeListener { _, checkedId ->
            if (checkedId == R.id.rb_other_Client) {
                til_other_details_Client.visibility = View.VISIBLE
            } else {
                til_other_details_Client.visibility = View.GONE
            }
        }
    }

    private fun setupActionBar() {
        GlobalScope.launch {
            setSupportActionBar(toolbar_add_edit_address_activity)
            val actionBar = supportActionBar
            if (actionBar != null) {
                actionBar.setDisplayHomeAsUpEnabled(true)
                actionBar.setHomeAsUpIndicator(R.drawable.ic_white_color_back_24dp)
            }
            toolbar_add_edit_address_activity.setNavigationOnClickListener { onBackPressed() }
        }
    }

    private var mAddressDetails: AddressClient? = null
    private suspend fun displayAddress() {
        withContext(Dispatchers.Main) {
            if (mAddressDetails != null) {
                if (mAddressDetails!!.id.isNotEmpty()) {
                    setText()
                    when (mAddressDetails?.type) {
                        Constants.HOME_CLIENT -> {
                            rb_home_Client.isChecked = true
                        }
                        Constants.OFFICE_CLIENT -> {
                            rb_office_Client.isChecked = true
                        }
                        else -> {
                            rb_other_Client.isChecked = true
                            til_other_details_Client.visibility = View.VISIBLE
                            et_other_details_Client.setText(mAddressDetails?.otherDetails)
                        }
                    }
                }
            }
        }
    }

    private suspend fun setText() {
        withContext(Dispatchers.Main) {
           // tv_title_Client.text = resources.getString(R.string.title_edit_address)
            btn_submit_address_Client.text = resources.getString(R.string.btn_lbl_update)
            et_full_name_Client.setText(mAddressDetails?.name)
            et_phone_number_Client.setText(mAddressDetails?.mobileNumber)
            et_address_Client.setText(mAddressDetails?.address)
            et_zip_code_Client.setText(mAddressDetails?.zipCode)
            et_additional_note_Client.setText(mAddressDetails?.additionalNote)
        }
    }

    private fun saveAddressToFirestoreClient() {
        val fullName: String = et_full_name_Client.text.toString().trim() { it <= ' ' }
        val phoneNumber: String = et_phone_number_Client.text.toString().trim { it <= ' ' }
        val address: String = et_address_Client.text.toString().trim { it <= ' ' }
        val zipCode: String = et_zip_code_Client.text.toString().trim { it <= ' ' }
        val additionalNote: String = et_additional_note_Client.text.toString().trim { it <= ' ' }
        val otherDetails: String = et_other_details_Client.text.toString().trim { it <= ' ' }
        if (validateData()) {
            showProgressDialog(resources.getString(R.string.please_wait))
            val addressType: String = when {
                rb_home_Client.isChecked -> {
                    Constants.HOME_CLIENT
                }
                rb_office_Client.isChecked -> {
                    Constants.OFFICE_CLIENT
                }
                else -> {
                    Constants.OTHER_CLIENT
                }
            }
            val addressModel = AddressClient(
                UserFireStore().getCurrentUserID(),
                fullName,
                phoneNumber,
                address,
                zipCode,
                additionalNote,
                addressType,
                otherDetails
            )

            if (mAddressDetails != null && mAddressDetails!!.id.isNotEmpty()) {
                viewModel.updateAddressClient(this, addressModel, mAddressDetails!!.id)
            } else {
                viewModel.addAddressClient(this, addressModel)
            }
        }
    }

    fun addUpdateAddressSuccess() {
        hideProgressDialog()
        val notifySuccessMessage: String =
            if (mAddressDetails != null && mAddressDetails!!.id.isNotEmpty()) {
                resources.getString(R.string.msg_your_address_updated_successfully)
            } else {
                resources.getString(R.string.err_your_address_added_successfully)
            }
        Toast.makeText(
            this@AddEditAddress,
            notifySuccessMessage,
            Toast.LENGTH_SHORT
        ).show()
        setResult(RESULT_OK)
        finish()

    }

    private fun validateData(): Boolean {
        return when {
            TextUtils.isEmpty(et_full_name_Client.text.toString().trim { it <= ' ' }) -> {
                showErrorSnackBar(
                    resources.getString(R.string.err_msg_please_enter_full_name),
                    true
                )
                false
            }
            TextUtils.isEmpty(et_phone_number_Client.text.toString().trim { it <= ' ' }) -> {
                showErrorSnackBar(
                    resources.getString(R.string.err_msg_please_enter_phone_number),
                    true
                )
                false
            }
            TextUtils.isEmpty(et_address_Client.text.toString().trim { it <= ' ' }) -> {
                showErrorSnackBar(resources.getString(R.string.err_msg_please_enter_address), true)
                false
            }
            TextUtils.isEmpty(et_zip_code_Client.text.toString().trim { it <= ' ' }) -> {
                showErrorSnackBar(resources.getString(R.string.err_msg_please_enter_zip_code), true)
                false
            }
            rb_other_Client.isChecked && TextUtils.isEmpty(
                et_zip_code_Client.text.toString().trim { it <= ' ' }) -> {
                showErrorSnackBar(resources.getString(R.string.err_msg_please_enter_zip_code), true)
                false
            }
            else -> {
                true
            }
        }
    }
}