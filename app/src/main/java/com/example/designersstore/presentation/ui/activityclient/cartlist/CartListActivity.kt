package com.example.designersstore.presentation.ui.activityclient.cartlist

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.designersstore.R
import com.example.designersstore.data.designerfirestore.DesignerViewModel
import com.example.designersstore.data.userfirestore.UserFirestoreViewModel
import com.example.designersstore.databinding.ActivityCartListBinding
import com.example.designersstore.domain.models.CartItem
import com.example.designersstore.domain.models.Product
import com.example.designersstore.presentation.ui.activity.BaseActivity
import com.example.designersstore.presentation.ui.activityclient.addresslistclient.AddressList_Client
import com.example.designersstore.presentation.ui.adapters.CartItemsListAdapter
import com.example.designersstore.presentation.utils.Constants
import kotlinx.android.synthetic.main.activity_cart_list.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class CartListActivity : BaseActivity() {

    private val viewModelUser: UserFirestoreViewModel by viewModels()
    private val viewModel: DesignerViewModel by viewModels()
    private val cartListViewModel: CartListViewModel by viewModels()
    lateinit var binding: ActivityCartListBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cart_list)
        binding = ActivityCartListBinding.inflate(layoutInflater)

        actions()

        setContentView(binding.root)
    }

    private fun actions() {
        setupActionBar()
        clickCheckout()
    }

    private fun setupActionBar() {
        setSupportActionBar(toolbar_cart_list_activity)
        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_white_color_back_24dp)
        }
        toolbar_cart_list_activity.setNavigationOnClickListener { onBackPressed() }
    }

    private fun clickCheckout() {
        binding.btnCheckout.setOnClickListener {
            val intent = Intent(this@CartListActivity, AddressList_Client::class.java)
            intent.putExtra(Constants.EXTRA_SELECT_ADDRESS_CLIENT, true)
            startActivity(intent)
        }
    }

    private lateinit var mCartListItems: ArrayList<CartItem>
    private var subTotal: Double = 0.0
    suspend fun successCartItemsList(cartList: ArrayList<CartItem>) {
        withContext(Dispatchers.Main) {
            checkProductListAndCartItem(cartList, mProductList)
            mCartListItems = cartList
            if (mCartListItems.size > 0) {
                visibilityItemInCart()
                setCartItemsListAdapter()
                calculatePrices(mCartListItems, subTotal)
            } else {
                visibilityNoItemFoundInCart()
            }
        }
    }

    private fun visibilityItemInCart() {
        binding.rvCartItemsList.visibility = View.VISIBLE
        binding.llCheckout.visibility = View.VISIBLE
        binding.tvNoCartItemFound.visibility = View.GONE
    }

    private fun visibilityNoItemFoundInCart() {
        binding.rvCartItemsList.visibility = View.GONE
        binding.llCheckout.visibility = View.GONE
        binding.tvNoCartItemFound.visibility = View.VISIBLE
    }

    private fun setCartItemsListAdapter() {
        binding.rvCartItemsList.layoutManager = LinearLayoutManager(this@CartListActivity)
        binding.rvCartItemsList.setHasFixedSize(true)
        val cartListAdapter = CartItemsListAdapter(this@CartListActivity, mCartListItems, true)
        binding.rvCartItemsList.adapter = cartListAdapter
    }

    // todo:***********************************************************************************************************************************
    private fun calculatePrices(mCartListItems: ArrayList<CartItem>, subTotal: Double = 0.0) {
        GlobalScope.launch(Dispatchers.Main) {
            var subTotal = subTotal
            for (item in mCartListItems) {
                val availableQuantity = item.stock_quantity.toInt()
                if (availableQuantity > 0) {
                    val price = item.price.toDouble()
                    val quantity = item.cart_quantity.toInt()
                    subTotal += (price * quantity)
                }
            }
            //       cartListViewModel.calculatePrices(mCartListItems,subTotal)
            putPrices(subTotal)
        }
    }
    // todo:***********************************************************************************************************************************

    private fun putPrices(subTotal: Double = 0.0) {
        binding.tvSubTotal.text = "ج.م$subTotal"
        binding.tvShippingCharge.text = "ج.م10.0"
        if (subTotal > 0) {
            binding.llCheckout.visibility = View.VISIBLE
            val total = subTotal + 10
            binding.tvTotalAmount.text = "ج.م$total"
        } else {
            binding.llCheckout.visibility = View.GONE
        }
    }

    private lateinit var mProductList: ArrayList<Product>
    private fun checkProductListAndCartItem(
        cartList: ArrayList<CartItem>,
        mProductList: ArrayList<Product>
    ) {
        hideProgressDialog()
        cartListViewModel.checkProductListAndCartItem(cartList, mProductList)
    }

    fun successProductsListFromFireStore(productsList: ArrayList<Product>) {
        hideProgressDialog()
        mProductList = productsList
        getCartItemsList()
    }

    private fun getProductList() {
        showProgressDialog(resources.getString(R.string.please_wait))
        viewModel.getAllProductList(this)
    }

    private fun getCartItemsList() {
        viewModelUser.getCartList(this@CartListActivity)
    }

    fun itemUpdateSuccess() {
        hideProgressDialog()
        getCartItemsList()
    }

    override fun onResume() {
        super.onResume()
        getProductList()
    }

    fun itemRemovedSuccess() {
        hideProgressDialog()
        Toast.makeText(
            this@CartListActivity,
            resources.getString(R.string.msg_item_removed_successfully),
            Toast.LENGTH_SHORT
        ).show()
        getCartItemsList()
    }
}