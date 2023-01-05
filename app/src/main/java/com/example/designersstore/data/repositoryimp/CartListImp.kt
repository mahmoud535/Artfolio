package com.example.designersstore.data.repositoryimp

import android.util.Log
import com.example.designersstore.domain.models.CartItem
import com.example.designersstore.domain.models.Product
import com.example.designersstore.domain.models.repository.CartListRepo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class CartListImp : CartListRepo {

    //private lateinit var mProductList: ArrayList<Product>
    override suspend fun checkProductListAndCartItem(cartList: ArrayList<CartItem>,mProductList: ArrayList<Product>) =
        withContext(Dispatchers.IO) {
            for (product in mProductList) {
                for (cartItem in cartList) {
                    if (product.product_id == cartItem.product_id) {
                        cartItem.stock_quantity = product.stock_quantity
                        if (product.stock_quantity.toInt() == 0) {
                            cartItem.cart_quantity = product.stock_quantity
                        }
                    }
                }
            }
        }

    //private lateinit var mCartListItems: ArrayList<CartItem>
    override suspend fun calculatePrices(mCartListItems: ArrayList<CartItem>, subTotal : Double  ) =
        withContext(Dispatchers.Main) {
            var subTotal = subTotal

            for (item in mCartListItems) {
                val availableQuantity = item.stock_quantity.toInt()
                if (availableQuantity > 0) {
                    val price = item.price.toDouble()
                    val quantity = item.cart_quantity.toInt()
                    subTotal += (price * quantity)
                }
                Log.d("Mahmoud","MMMMMMMMMMMMMMMMmmmmmmmmmm")
            }
        }


}