package com.example.designersstore.data.repositoryimp

import com.example.designersstore.data.userfirestore.UserFireStore
import com.example.designersstore.domain.models.AddressClient
import com.example.designersstore.domain.models.CartItem
import com.example.designersstore.domain.models.Order
import com.example.designersstore.domain.models.PlacedOrder
import com.example.designersstore.domain.models.repository.Checkout

class CheckoutImp : Checkout {


    private lateinit var mOrderDetails: Order
    private var mCartItemList: ArrayList<CartItem>
    private var mAddressDetails: AddressClient = AddressClient()
    var placedOrder: PlacedOrder

    constructor(CartItemList: ArrayList<CartItem>,AddressDetails: AddressClient? = null) {
        if (AddressDetails != null) {
            this.mAddressDetails = AddressDetails
        }
        this.mCartItemList = CartItemList
        this.placedOrder = PlacedOrder()

    }

    override fun placeAnOrder() {
        mOrderDetails =  Order(
            UserFireStore().getCurrentUserID(),
            mCartItemList,
            mAddressDetails!!,
            "My order ${System.currentTimeMillis()}",
            CartItem().image,
            PlacedOrder().mSubTotal.toString(),
            "10.0", // The Shipping Charge is fixed as $10 for now in our case.
            PlacedOrder().mTotalAmount.toString(),
            System.currentTimeMillis()
        )

    }
}