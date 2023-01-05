package com.example.designersstore.presentation.ui.activityclient.settingclient

import android.app.Application
import android.content.Intent
import androidx.lifecycle.AndroidViewModel
import com.example.designersstore.domain.models.User
import com.example.designersstore.presentation.ui.activityclient.addresslistclient.AddressList_Client
import com.example.designersstore.presentation.ui.activityclient.clientprofile.Client_Profile_Activity
import com.example.designersstore.presentation.ui.activityclient.loginclient.Login_Client_Activity
import com.example.designersstore.presentation.utils.Constants

class SettingClientViewModel(application: Application): AndroidViewModel(application) {
    var appCon= application

    fun extraFromBtnEditToClientProfile(userDetails: User){
        val intent=Intent(appCon, Client_Profile_Activity::class.java)
        intent.putExtra(Constants.EXTRA_USER_DETAILS,userDetails)
        appCon.startActivity(intent)
    }

    fun intentFromBtnLogoutToLogin(){
        val intent= Intent(appCon, Login_Client_Activity::class.java)
        intent.flags= Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        appCon.startActivity(intent)
    }
    fun btnAddressToAddressList(){
        val intent=Intent(appCon, AddressList_Client::class.java)
        appCon.startActivity(intent)
    }


}