package com.example.designersstore.presentation.ui.activityclient.addresslistclient

import android.app.Activity
import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.recyclerview.widget.RecyclerView
import com.example.designersstore.data.repositoryimp.AddressListClientImp
import com.example.designersstore.domain.models.AddressClient


class A_LClientViewModel() : ViewModel() {

    private var createSwipeMutableLiveData= MutableLiveData<List<AddressClient>>()
    val createSwipeLiveData: MutableLiveData<List<AddressClient>> get() = createSwipeMutableLiveData


    private val addressListClientImp: AddressListClientImp

    init {
        addressListClientImp = AddressListClientImp()
    }

      fun createSwipeToEdit(context: Context, recyclerView: RecyclerView, activity: Activity) {
           // createSwipeMutableLiveData.postValue(addressListClientImp.createSwipe(context, recyclerView))
         addressListClientImp.createSwipeToEdit(context, recyclerView,activity)

    }
    fun createSwipeToDelete(context: Context,addressList: ArrayList<AddressClient>,recyclerView: RecyclerView,activity: AddressList_Client){
        addressListClientImp.createSwipeToDelete(context, addressList, recyclerView, activity)
    }


}