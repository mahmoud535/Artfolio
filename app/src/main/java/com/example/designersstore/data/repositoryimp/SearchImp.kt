package com.example.designersstore.data.repositoryimp

import android.util.Log
import android.widget.Toast
import com.example.designersstore.domain.models.Product
import com.example.designersstore.presentation.ui.fragmentsclient.searchfilter.SearchFragment
import com.example.designersstore.domain.models.repository.SearchRepo
import com.example.designersstore.presentation.utils.Constants
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SearchImp(private val mFireStore: FirebaseFirestore = FirebaseFirestore.getInstance()) :
    SearchRepo {
    val productsList: ArrayList<Product> = ArrayList()
    override suspend fun getSearchItemsList(fragmentSearch: SearchFragment) {
        withContext(Dispatchers.IO) {
            mFireStore.collection(Constants.PRODUCTS)
                .get()
                .addOnSuccessListener { document ->
                    Log.e(fragmentSearch.javaClass.simpleName, document.documents.toString())

                    val text: String = String()
                    for (i in document.documents) {
                        val product = i.toObject(Product::class.java)!!
                        product.product_id = i.id

                        if (product.title.toLowerCase().contains(text.toLowerCase())) {
                            productsList.add(product)
                        }
                    }

                    GlobalScope.launch(Dispatchers.Main) {
                        if (productsList.isEmpty()) {
                            Log.e(
                                fragmentSearch.javaClass.simpleName,
                                "No Data Found..",

                                )
                        } else {
                            fragmentSearch.adapter.setList(productsList)
                            fragmentSearch.successDashboardItemsList(productsList)
                        }

                    }


                }
        }
            .addOnFailureListener { e ->
                fragmentSearch.hideProgressDialog()
                Log.e(
                    fragmentSearch.javaClass.simpleName,
                    "Error while getting dashboard items list.",
                    e
                )
            }
    }

    override suspend fun filter(fragmentSearch: SearchFragment) {
        val filteredlist: ArrayList<Product> = ArrayList()
        val text: String = String()
        // running a for loop to compare elements.
        for (item in productsList) {
            // checking if the entered string matched with any item of our recycler view.
            if (item.title.toLowerCase().contains(text.toLowerCase())) {
                // if the item is matched we are
                // adding it to our filtered list.
                filteredlist.add(item)
            }
        }

        if (filteredlist.isEmpty()) {
            // if no item is added in filtered list we are
            // displaying a toast message as no data found.
            Log.e(
                fragmentSearch.javaClass.simpleName,
                "No Data Found..",

                )
        } else {
            // at last we are passing that filtered
            // list to our adapter class.
            fragmentSearch.adapter.filterList(filteredlist)
            fragmentSearch.successDashboardItemsList(filteredlist)
        }
    }
}
