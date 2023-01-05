package com.example.designersstore.domain.models.repository

import androidx.fragment.app.Fragment
import com.example.designersstore.domain.models.CartItem
import com.example.designersstore.domain.models.Product
import com.example.designersstore.presentation.ui.activityclient.ProductDetailsActivity
import com.example.designersstore.presentation.ui.fragmentsclient.favoritelist.FavoriteListFragment

interface FavoriteListRepo {

    suspend fun getFavoriteList(fragment: Fragment): List<CartItem>

    suspend fun getAllFavList(fragment: FavoriteListFragment):List<Product>

    suspend fun addFavoriteItems(activity: ProductDetailsActivity, addToFav: CartItem)

    suspend fun deleteProduct(fragment: FavoriteListFragment, productId: String)
}