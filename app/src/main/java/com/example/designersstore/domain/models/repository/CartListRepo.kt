package com.example.designersstore.domain.models.repository

import com.example.designersstore.domain.models.CartItem
import com.example.designersstore.domain.models.Product

interface CartListRepo {


    suspend fun checkProductListAndCartItem(cartList: ArrayList<CartItem>, mProductList: ArrayList<Product>)
    suspend fun calculatePrices(mCartListItems: ArrayList<CartItem>, subTotal : Double = 0.0)
}