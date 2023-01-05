package com.example.designersstore.presentation.ui.activityclient.addeditaddressactivity

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.designersstore.data.repositoryimp.AddEditAddressImp
import com.example.designersstore.presentation.utils.Constants

class AddressViewModel() : ViewModel() {

    private val addEditAddressImp: AddEditAddressImp

    private val parcelableAddressMutableLiveData= MutableLiveData(Constants.EXTRA_ADDRESS_DETAILS_CLIENT)
    val parcelableAddressLiveData: LiveData<String> = parcelableAddressMutableLiveData
    init {
        addEditAddressImp = AddEditAddressImp()
    }

//    fun parcelableAddress(activity: Activity) = viewModelScope.launch {
//        addEditAddressImp.parcelableAddress(activity)
//    }

    private fun parcelableAddress(parAddress:String) {
        parcelableAddressMutableLiveData.value = parAddress
//        if (intent.hasExtra(Constants.EXTRA_ADDRESS_DETAILS_CLIENT)) {
//            mAddressDetails =
//                intent.getParcelableExtra(Constants.EXTRA_ADDRESS_DETAILS_CLIENT)!!
//        }
        //  viewModelAddEditAddress.parcelableAddress(this)
    }
}