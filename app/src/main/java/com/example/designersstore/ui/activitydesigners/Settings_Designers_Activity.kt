package com.example.designersstore.ui.activitydesigners

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.example.designersstore.R
import com.example.designersstore.firestore.FireStoreClass
import com.example.designersstore.models.Designer
import com.example.designersstore.ui.activity.BaseActivity
import com.example.designersstore.utils.Constants
import com.example.designersstore.utils.GlideLoader
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import kotlinx.android.synthetic.main.activity_designers__profile_.*
import kotlinx.android.synthetic.main.activity_settings.*
import kotlinx.android.synthetic.main.activity_settings.toolbar_settings_activity
import kotlinx.android.synthetic.main.activity_settings__designers_.*

class Settings_Designers_Activity : BaseActivity() ,View.OnClickListener{

    private lateinit var mDesignerDetails:Designer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings__designers_)

        setupActionBar()

        tv_edit_Designers.setOnClickListener(this)
        btn_logout_Designer.setOnClickListener(this)
        ll_address_Designer.setOnClickListener(this)
    }

    private fun setupActionBar(){

        setSupportActionBar(toolbar_settings_activity_Designer)

        val actionBar=supportActionBar
        if (actionBar!= null){
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_white_color_back_24dp)
        }
        toolbar_settings_activity_Designer.setNavigationOnClickListener { onBackPressed() }
    }

    private fun getDesignerDetails(){
        showProgressDialog(resources.getString(R.string.please_wait))
        FireStoreClass().getDesignersDetails(this)
    }

    fun designerDetailsSuccess(designer:Designer){
        mDesignerDetails=designer

         hideProgressDialog()
        GlideLoader(this@Settings_Designers_Activity).loadUserPicture(designer.image,iv_Designer_photo)

        tv_name_Designer.text="${designer.firstName}${designer.lastName}"
        tv_Profession_Designer.text=designer.profession
        tv_gender_Designer.text=designer.gender
        tv_email_Designer.text=designer.email
        tv_mobile_number_Designer.text="${designer.mobile}"
    }

    override fun onResume() {
        super.onResume()
        getDesignerDetails()
    }

    override fun onClick(v: View?) {
        if (v!=null){
            when(v.id){
                R.id.tv_edit_Designers->{
                  val intent =Intent(this@Settings_Designers_Activity,Designers_Profile_Activity::class.java)
                    intent.putExtra(Constants.EXTRA_DESIGNERS_DETAILS,mDesignerDetails)
                    startActivity(intent)
                }
                R.id.btn_logout_Designer->{
                   FirebaseAuth.getInstance().signOut()
                    val intent= Intent(this@Settings_Designers_Activity,Login_Designers_Activity::class.java)
                    intent.flags= Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)
                    finish()
                }

                R.id.ll_address_Designer->{
                    val intent=Intent(this@Settings_Designers_Activity,AddressListActivity_Designer::class.java)
                    startActivity(intent)
                }
            }
        }
    }
}