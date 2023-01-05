package com.example.designersstore.presentation.ui.activityclient.cartlist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.designersstore.data.repositoryimp.CartListImp
import com.example.designersstore.domain.models.CartItem
import com.example.designersstore.domain.models.Product
import kotlinx.coroutines.launch

class CartListViewModel : ViewModel() {

    val cartListImp: CartListImp

    init {
        cartListImp = CartListImp()
    }

    fun checkProductListAndCartItem(cartList: ArrayList<CartItem>,mProductList: ArrayList<Product>) =
        viewModelScope.launch {
            cartListImp.checkProductListAndCartItem(cartList,mProductList)
        }

    fun calculatePrices(mCartListItems: ArrayList<CartItem>, subTotal : Double = 0.0) =
        viewModelScope.launch {
            cartListImp.calculatePrices(mCartListItems,subTotal)
        }

}