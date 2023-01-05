package com.example.designersstore.presentation.ui.activityclient.checkout

import android.app.Application
import android.content.Intent
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import com.example.designersstore.data.repositoryimp.CheckoutImp
import com.example.designersstore.domain.models.AddressClient
import com.example.designersstore.domain.models.CartItem
import com.example.designersstore.presentation.ui.activityclient.DashboardActivity

class CheckoutViewModel(application: Application):AndroidViewModel(application) {

    private val checkoutImp: CheckoutImp
    var mAddressDetails: AddressClient? = null
    var appCon = application

    private var mSubTotal: Double = 0.0
    private lateinit var mCartItemList: ArrayList<CartItem>
    init {
       var  CartItemList: ArrayList<CartItem> = ArrayList()
        val mCartlist =  CartItemList
        checkoutImp = CheckoutImp(mCartlist,mAddressDetails)

    }

    fun placeAnOrder(){
        checkoutImp.placeAnOrder()
    }
    fun intentFLAG(){
        val intent = Intent(appCon, DashboardActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        appCon.startActivity(intent)
    }

    fun calculatePrice(mCartItemList: ArrayList<CartItem>){

        for (item in mCartItemList) {
            val availableQuantity = item.stock_quantity.toInt()
            if (availableQuantity > 0) {
                val price = item.price.toDouble()
                val quantity = item.cart_quantity.toInt()
                mSubTotal += (price * quantity)
                Log.d("mmAl1i","sucessed*****")
            }
        }

    }

}