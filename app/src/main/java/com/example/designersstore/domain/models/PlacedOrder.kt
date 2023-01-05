package com.example.designersstore.domain.models

 class PlacedOrder(){
     var mSubTotal: Double = 0.0
     var mTotalAmount: Double = 0.0
     lateinit var mOrderDetails: Order
     lateinit var mCartItemList: ArrayList<CartItem>
     var mAddressDetails: AddressClient? = null
}
