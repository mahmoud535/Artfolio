package com.example.designersstore.ui.activityclient

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.Toast
import com.example.designersstore.R
import com.example.designersstore.firestore.FireStoreClass
import com.example.designersstore.models.AddressClient
import com.example.designersstore.models.AddressDesigner
import com.example.designersstore.ui.activity.BaseActivity
import com.example.designersstore.utils.Constants
import kotlinx.android.synthetic.main.activity_add_edit_address.*

class AddEditAddressActivity : BaseActivity() {
    private var mAddressDetails:AddressClient?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_edit_address)
        setupActionBar()

        if (intent.hasExtra(Constants.EXTRA_ADDRESS_DETAILS_CLIENT)) {
            mAddressDetails =
                intent.getParcelableExtra(Constants.EXTRA_ADDRESS_DETAILS_CLIENT)!!
        }

        if (mAddressDetails != null ){
            if (mAddressDetails!!.id.isNotEmpty()){
                //tv_title_Client.text = resources.getString(R.string.title_edit_address)
                btn_submit_address_Client.text = resources.getString(R.string.btn_lbl_update)

                et_full_name_Client.setText(mAddressDetails?.name)
                et_full_name_Client.setText(mAddressDetails?.mobileNumber)
                et_address_Client.setText(mAddressDetails?.address)
                et_zip_code_Client.setText(mAddressDetails?.zipCode)
                et_additional_note_Client.setText(mAddressDetails?.additionalNote)

                when(mAddressDetails?.type){
                    Constants.HOME_CLIENT ->{
                        rb_home_Client.isChecked=true
                    }
                    Constants.OFFICE_CLIENT ->{
                        rb_office_Client.isChecked=true
                    }
                    else->{
                        rb_other_Client.isChecked=true
                        til_other_details_Client.visibility= View.VISIBLE
                        et_other_details_Client.setText(mAddressDetails?.otherDetails)
                    }
                }
            }
        }

        btn_submit_address_Client.setOnClickListener { saveAddressToFirestoreClient() }

        rg_type_Client.setOnCheckedChangeListener { _, checkedId ->
            if (checkedId == R.id.rb_other_Client) {
                til_other_details_Client.visibility = View.VISIBLE
            } else {
                til_other_details_Client.visibility = View.GONE
            }
        }
        // END


}

    private fun setupActionBar(){
        setSupportActionBar(toolbar_add_edit_address_activity)

        val actionBar=supportActionBar
        if (actionBar!=null){
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_white_color_back_24dp)
        }

        toolbar_add_edit_address_activity.setNavigationOnClickListener { onBackPressed() }
    }

    private fun saveAddressToFirestoreClient(){
        val fullName:String=et_full_name_Client.text.toString().trim(){it<=' '}
        val phoneNumber: String = et_phone_number_Client.text.toString().trim { it <= ' ' }
        val address: String = et_address_Client.text.toString().trim { it <= ' ' }
        val zipCode: String = et_zip_code_Client.text.toString().trim { it <= ' ' }
        val additionalNote: String = et_additional_note_Client.text.toString().trim { it <= ' ' }
        val otherDetails: String = et_other_details_Client.text.toString().trim { it <= ' ' }

        if (validateData()){
            //show the progress dialog
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
            val addressModel= AddressClient(
                FireStoreClass().getCurrentUserID(),
                fullName,
                phoneNumber,
                address,
                zipCode,
                additionalNote,
                addressType,
                otherDetails
            )

            if (mAddressDetails != null && mAddressDetails!!.id.isNotEmpty()){
                FireStoreClass().updateAddressClient(this,addressModel,mAddressDetails!!.id)
            }else{
                FireStoreClass().addAddressClient(this,addressModel)
            }
        }
    }

    fun addUpdateAddressSuccess() {

        // Hide progress dialog
        hideProgressDialog()

        val notifySuccessMessage:String=if(mAddressDetails != null && mAddressDetails!!.id.isNotEmpty()){
            resources.getString(R.string.msg_your_address_updated_successfully)
        }else{
            resources.getString(R.string.err_your_address_added_successfully)
        }

        Toast.makeText(
            this@AddEditAddressActivity,
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