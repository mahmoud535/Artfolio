package com.example.designersstore.domain.models.repository

import android.app.Activity
import android.content.Context
import androidx.recyclerview.widget.RecyclerView
import com.example.designersstore.domain.models.AddressClient
import com.example.designersstore.presentation.ui.activityclient.addresslistclient.AddressList_Client

interface AddressListClient {

     fun createSwipeToEdit(context: Context, recyclerView: RecyclerView,activity: Activity)

     fun createSwipeToDelete(context: Context,addressList: ArrayList<AddressClient>,recyclerView: RecyclerView,activity: AddressList_Client)
}