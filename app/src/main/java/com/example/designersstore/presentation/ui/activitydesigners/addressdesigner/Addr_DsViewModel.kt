package com.example.designersstore.presentation.ui.activitydesigners.addressdesigner

import android.app.Activity
import android.app.Application
import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.recyclerview.widget.RecyclerView
import com.example.designersstore.data.repositoryimpdesigner.AddressDesignerImp
import com.example.designersstore.domain.models.AddressClient

class Addr_DsViewModel(): ViewModel() {

    private val addressListDesignerImp: AddressDesignerImp

    init {
        val application:Application = Application()
        addressListDesignerImp = AddressDesignerImp(application)
    }

    fun createSwipeToEdit(context: Context, recyclerView: RecyclerView, activity: Activity) {
        // createSwipeMutableLiveData.postValue(addressListClientImp.createSwipe(context, recyclerView))
        addressListDesignerImp.createSwipeToEdit(context, recyclerView,activity)

    }
    fun createSwipeToDelete(context: Context, addressList: ArrayList<AddressClient>, recyclerView: RecyclerView, activity: AddressList_Designer){
        addressListDesignerImp.createSwipeToDelete(context, addressList, recyclerView, activity)
    }


}