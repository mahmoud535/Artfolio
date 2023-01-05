package com.example.designersstore.data.repositoryimp

import android.util.Log
import androidx.fragment.app.Fragment
import com.example.designersstore.domain.models.CartItem
import com.example.designersstore.domain.models.Product
import com.example.designersstore.presentation.ui.activityclient.ProductDetailsActivity
import com.example.designersstore.presentation.ui.fragmentsclient.favoritelist.FavoriteListFragment
import com.example.designersstore.domain.models.repository.FavoriteListRepo
import com.example.designersstore.presentation.ui.fragmentsdesigners.ProductsFragment
import com.example.designersstore.presentation.utils.Constants
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class FavoriteListImp(private val mFireStore: FirebaseFirestore = FirebaseFirestore.getInstance()):
    FavoriteListRepo {

    override suspend fun getFavoriteList(fragment: Fragment): List<CartItem> {

        withContext(Dispatchers.IO) {
            mFireStore.collection(Constants.FAVORITE_ITEMS)
                .whereEqualTo(Constants.USER_ID, UserFirestoreRepositoryImp().getCurrentUserID())
                .get() // Will get the documents snapshots.
                .addOnSuccessListener { document ->
                    Log.e(fragment.javaClass.simpleName, document.documents.toString())
                    var list: ArrayList<CartItem> = ArrayList()
                    for (i in document.documents) {
                        val productItem = i.toObject(CartItem::class.java)!!
                        productItem.id = i.id
                        list.add(productItem)
                    }

                    when (fragment) {
                        is FavoriteListFragment -> {
                            GlobalScope.launch(Dispatchers.Main) {
                                fragment.adapter.setList(list)
                                fragment.successFavoriteItemsList(list)
                            }
                        }
                    }
                }
                .addOnFailureListener { e ->
                    when (fragment) {
                        is FavoriteListFragment -> {
                            fragment.hideProgressDialog()
                        }
                    }
                    Log.e(
                        fragment.javaClass.simpleName,
                        "Error while getting the favorite list items.",
                        e
                    )
                }
        }
        return listOf()
    }

    override suspend fun getAllFavList(fragment: FavoriteListFragment):List<Product> {
        withContext(Dispatchers.IO) {
            mFireStore.collection(Constants.PRODUCTS)
                .get()
                .addOnSuccessListener { document ->
                    Log.e("Product List", document.documents.toString())
                    val productsList: ArrayList<Product> = ArrayList()
                    for (i in document.documents) {
                        val product = i.toObject(Product::class.java)
                        product!!.product_id = i.id

                        productsList.add(product)
                    }

                    when (fragment) {
                        is FavoriteListFragment -> {
                            fragment.successFavListFromFireStore(productsList)
                        }
                    }
                }.addOnFailureListener { e ->
                    when (fragment) {
                        is FavoriteListFragment -> {
                            fragment.hideProgressDialog()
                        }
                    }
                    Log.e("Get Product List", "Error while getting all product list.", e)
                }
        }
        return listOf()
    }

    override suspend fun addFavoriteItems(activity: ProductDetailsActivity, addToFav: CartItem) {
        withContext(Dispatchers.IO) {
            mFireStore.collection(Constants.FAVORITE_ITEMS)
                .document()
                .set(addToFav, SetOptions.merge())
                .addOnSuccessListener {
                    activity.addFavoriteListSuccess()
                }
                .addOnFailureListener { e ->
                    activity.hideProgressDialog()
                    Log.e(
                        activity.javaClass.simpleName,
                        "Error while creating the document for Favorite item.",
                        e
                    )
                }
        }
    }

    override suspend fun deleteProduct(fragment: FavoriteListFragment, productId: String) {
        withContext(Dispatchers.IO) {
            mFireStore.collection(Constants.FAVORITE_ITEMS)
                .document(productId)
                .delete()
                .addOnSuccessListener {
                    fragment.favoriteProductDeleteSuccess()
                }.addOnFailureListener { e ->
                   // fragment.hideProgressDialog()
                    Log.e(
                        fragment.requireActivity().javaClass.simpleName,
                        "Error while deleting the product.",
                        e
                    )
                }
        }
    }

}