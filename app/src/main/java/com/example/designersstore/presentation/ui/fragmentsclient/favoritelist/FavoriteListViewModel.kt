package com.example.designersstore.presentation.ui.fragmentsclient.favoritelist

import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.designersstore.data.repositoryimp.FavoriteListImp
import com.example.designersstore.domain.models.CartItem
import com.example.designersstore.presentation.ui.activityclient.ProductDetailsActivity
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch

class FavoriteListViewModel: ViewModel() {
    private var favoriteListImp: FavoriteListImp
    init {
        var firestore = FirebaseFirestore.getInstance()

        favoriteListImp = FavoriteListImp(firestore)
    }

    fun getFavoriteList(fragment: Fragment)= viewModelScope.launch {
        favoriteListImp.getFavoriteList(fragment)
    }

    fun addFavoriteItems(activity: ProductDetailsActivity, addToFav: CartItem)=
        viewModelScope.launch {
            favoriteListImp.addFavoriteItems(activity, addToFav)
        }

    fun getAllFavList(fragment: FavoriteListFragment)=viewModelScope.launch {
        favoriteListImp.getAllFavList(fragment)
    }

     fun deleteProduct(fragment: FavoriteListFragment, productId: String) =viewModelScope.launch {
         favoriteListImp.deleteProduct(fragment,productId)
    }
}