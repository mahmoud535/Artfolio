package com.example.designersstore.presentation.ui.fragmentsclient.favoritelist

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.designersstore.R
import com.example.designersstore.databinding.FragmentFavoriteListBinding
import com.example.designersstore.domain.models.CartItem
import com.example.designersstore.domain.models.Product
import com.example.designersstore.presentation.ui.activityclient.ProductDetailsActivity
import com.example.designersstore.presentation.ui.fragmentsclient.BaseFragment
import com.example.designersstore.presentation.ui.fragmentsclient.favoritelist.adapter.FavListAdapter
import com.example.designersstore.presentation.ui.fragmentsclient.favoritelist.adapter.OnListItemClick
import com.example.designersstore.presentation.utils.Constants
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch


class FavoriteListFragment : BaseFragment(), OnListItemClick {

    private lateinit var binding: FragmentFavoriteListBinding
    private val viewModel: FavoriteListViewModel by viewModels()
    val adapter: FavListAdapter by lazy {
        FavListAdapter(requireContext(),FavoriteListFragment())
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentFavoriteListBinding.inflate(layoutInflater)
        return binding.root
    }

    private lateinit var mFavoriteListItems: ArrayList<CartItem>
    fun successFavoriteItemsList(favList: ArrayList<CartItem>) {
        mFavoriteListItems = favList
        if (mFavoriteListItems.size > 0) {
            visibilityItemInFavorite()
            setCartItemsListAdapter()
        } else {
            visibilityNoItemFound()
        }
    }

    private fun visibilityItemInFavorite() {
        binding.rvFavoriteItemsList.visibility = View.VISIBLE
        binding.tvNoFavoriteItemFound.visibility = View.GONE
    }

    private fun visibilityNoItemFound() {
        binding.rvFavoriteItemsList.visibility = View.GONE
        binding.tvNoFavoriteItemFound.visibility = View.VISIBLE
    }

    private fun setCartItemsListAdapter() {
        binding.rvFavoriteItemsList.layoutManager = LinearLayoutManager(requireContext())
        binding.rvFavoriteItemsList.setHasFixedSize(true)
        binding.rvFavoriteItemsList.adapter = adapter
        adapter.onListItemClick = this
    }

    private lateinit var mProductList: ArrayList<Product>
    fun successFavListFromFireStore(productsList: ArrayList<Product>) {
        hideProgressDialog()
        mProductList = productsList
        getCartItemsList()
    }

    private fun getCartItemsList() {
        viewModel.getFavoriteList(this)
    }

    private fun getProductList() {
        showProgressDialog(resources.getString(R.string.please_wait))
        viewModel.getAllFavList(this)
    }

    override fun onResume() {
        super.onResume()
        getProductList()
    }

    override fun onItemClick(cartItem: CartItem) {
        val intent = Intent(context, ProductDetailsActivity::class.java)
        intent.putExtra(Constants.EXTRA_PRODUCT_ID, cartItem.product_id)
        intent.putExtra(Constants.EXTRA_PRODUCT_OWNER_ID, cartItem.user_id)
        context?.startActivity(intent)
    }

    fun deleteProduct(productID: String) {
//        if(isAdded) {
        GlobalScope.launch(Dispatchers.Main) {
            showAlertDialogToDeleteProduct(productID)
        }
//        }
    }

    private fun showAlertDialogToDeleteProduct(productID: String) {
        GlobalScope.launch(Dispatchers.Main) {
//            val builder = AlertDialog.Builder(requireActivity())
//            builder.setTitle(resources.getString(R.string.delete_dialog_title))
//            builder.setMessage(resources.getString(R.string.delete_dialog_message))
//            builder.setIcon(android.R.drawable.ic_dialog_alert)
//            builder.setPositiveButton(resources.getString(R.string.yes)) { dialogInterface, _ ->
//                GlobalScope.launch { withContext(Dispatchers.Main) {
//                    showProgressDialog(resources.getString(R.string.please_wait))
//                }  }

                    viewModel.deleteProduct(this@FavoriteListFragment, productID)

//                dialogInterface.dismiss()
//            }
//            builder.setNegativeButton(resources.getString(R.string.no)) { dialogInterface, _ ->
//                dialogInterface.dismiss()
//            }
//            val alertDialog: AlertDialog = builder.create()
//            alertDialog.setCancelable(false)
//            alertDialog.show()
        }
    }

    fun favoriteProductDeleteSuccess() {
      //  GlobalScope.launch(Dispatchers.IO) {
        //    withContext(Dispatchers.Main) {
              //  hideProgressDialog()
//                Toast.makeText(
//                   requireContext(),
//                    resources.getString(R.string.product_delete_success_message),
//                    Toast.LENGTH_SHORT
//                ).show()
//            }
   //         getProductList()

  //      }

    }
}