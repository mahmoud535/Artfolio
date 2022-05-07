package com.example.designersstore.ui.activitydesigners

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.Toast
import com.example.designersstore.R
import com.example.designersstore.firestore.FireStoreClass
import com.example.designersstore.models.AddressDesigner
import com.example.designersstore.ui.activity.BaseActivity
import com.example.designersstore.utils.Constants
import kotlinx.android.synthetic.main.activity_add_edit_address_designers.*
import kotlinx.android.synthetic.main.activity_address_list__designer.*
import kotlinx.android.synthetic.main.activity_address_list__designer.tv_title_Designer

class AddEditAddressActivityDesigners : BaseActivity() {
    private var mAddressDetails:AddressDesigner?=null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_edit_address_designers)

        setupActionBar()
        if (intent.hasExtra(Constants.EXTRA_ADDRESS_DETAILS_DESIGNER)) {
            mAddressDetails =
                intent.getParcelableExtra(Constants.EXTRA_ADDRESS_DETAILS_DESIGNER)!!
        }

        if (mAddressDetails != null ){
            if (mAddressDetails!!.id.isNotEmpty()){
                tv_title_Designer.text = resources.getString(R.string.title_edit_address)
                btn_submit_address_Designer.text = resources.getString(R.string.btn_lbl_update)

                et_full_name_Designer.setText(mAddressDetails?.name)
                et_phone_number_Designer.setText(mAddressDetails?.mobileNumber)
                et_address_Designer.setText(mAddressDetails?.address)
                et_zip_code_Designer.setText(mAddressDetails?.zipCode)
                et_additional_note_Designer.setText(mAddressDetails?.additionalNote)

                when(mAddressDetails?.type){
                    Constants.HOME_DESIGNER ->{
                        rb_home_Designer.isChecked=true
                    }
                    Constants.OFFICE_DESIGNER ->{
                        rb_office_Designer.isChecked=true
                    }
                    else->{
                        rb_other_Designer.isChecked=true
                        til_other_details_Designer.visibility= View.VISIBLE
                        et_other_details_Designer.setText(mAddressDetails?.otherDetails)
                    }
                }
            }
        }

        btn_submit_address_Designer.setOnClickListener { saveAddressToFirestore() }

        rg_type_Designer.setOnCheckedChangeListener { _, checkedId ->
            if (checkedId == R.id.rb_other_Designer) {
                til_other_details_Designer.visibility = View.VISIBLE
            } else {
                til_other_details_Designer.visibility = View.GONE
            }
        }
        // END
    }


    private fun setupActionBar(){
        setSupportActionBar(toolbar_add_edit_address_activity_Designer)

        val actionBar=supportActionBar
        if (actionBar!=null){
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_white_color_back_24dp)
        }

        toolbar_add_edit_address_activity_Designer.setNavigationOnClickListener { onBackPressed() }
    }


    private fun saveAddressToFirestore(){
        val fullName:String=et_full_name_Designer.text.toString().trim(){it<=' '}
        val phoneNumber: String = et_phone_number_Designer.text.toString().trim { it <= ' ' }
        val address: String = et_address_Designer.text.toString().trim { it <= ' ' }
        val zipCode: String = et_zip_code_Designer.text.toString().trim { it <= ' ' }
        val additionalNote: String = et_additional_note_Designer.text.toString().trim { it <= ' ' }
        val otherDetails: String = et_other_details_Designer.text.toString().trim { it <= ' ' }

        if (validateData()){
            //show the progress dialog
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
            val addressModel= AddressDesigner(
                FireStoreClass().getCurrentDesignerID(),
                fullName,
                phoneNumber,
                address,
                zipCode,
                additionalNote,
                addressType,
                otherDetails
            )

            if (mAddressDetails != null && mAddressDetails!!.id.isNotEmpty()){
                FireStoreClass().updateAddressDesigner(this,addressModel,mAddressDetails!!.id)
            }else{
                FireStoreClass().addAddressDesigner(this,addressModel)
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
            this@AddEditAddressActivityDesigners,
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