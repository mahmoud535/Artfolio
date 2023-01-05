package com.example.designersstore.presentation.ui.activityclient.checkout

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.designersstore.R
import com.example.designersstore.data.designerfirestore.DesignerViewModel
import com.example.designersstore.data.userfirestore.UserFireStore
import com.example.designersstore.data.userfirestore.UserFirestoreViewModel
import com.example.designersstore.domain.models.AddressClient
import com.example.designersstore.domain.models.CartItem
import com.example.designersstore.domain.models.Order
import com.example.designersstore.domain.models.Product
import com.example.designersstore.presentation.ui.activity.BaseActivity
import com.example.designersstore.presentation.ui.activityclient.DashboardActivity
import com.example.designersstore.presentation.ui.adapters.CartItemsListAdapter
import com.example.designersstore.presentation.utils.Constants
import kotlinx.android.synthetic.main.activity_checkout.*


class CheckoutActivity : BaseActivity() {

    private var mAddressDetails: AddressClient? = null
    private val viewModelUser: UserFirestoreViewModel by viewModels()
    private val viewModel: DesignerViewModel by viewModels()
    private val checkoutViewModel:CheckoutViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_checkout)

        actions()
    }

    private fun actions(){
        setupActionBar()
        parcelableAddress()
        putAddress()
        getProductList()
        setUpView()
    }

    private fun parcelableAddress(){
        if (intent.hasExtra(Constants.EXTRA_SELECT_ADDRESS)) {
            mAddressDetails =
                intent.getParcelableExtra<AddressClient>(Constants.EXTRA_SELECT_ADDRESS)
        }
    }

    private fun putAddress(){
        if (mAddressDetails != null) {
            tv_checkout_address_type.text = mAddressDetails?.type
            tv_checkout_full_name.text = mAddressDetails?.name
            tv_checkout_address.text = "${mAddressDetails!!.address}, ${mAddressDetails!!.zipCode}"
            tv_checkout_additional_note.text = mAddressDetails?.additionalNote
            if (mAddressDetails?.otherDetails!!.isNotEmpty()) {
                tv_checkout_other_details.text = mAddressDetails?.otherDetails
            }
            tv_checkout_mobile_number.text = mAddressDetails?.mobileNumber
        }
    }

    private fun setUpView(){
        btn_place_order.setOnClickListener {
            placeAnOrder()
        }
    }

    fun allDetailsUpdatedSuccessfully() {
        hideProgressDialog()
        Toast.makeText(this@CheckoutActivity, "Your order placed successfully.", Toast.LENGTH_SHORT)
            .show()
        checkoutViewModel.intentFLAG()
        finish()
    }

    private lateinit var mCartItemList: ArrayList<CartItem>
    private lateinit var mOrderDetails: Order
    fun orderPlacedSuccess() {
        viewModelUser.updateAllDetails(this, mCartItemList, mOrderDetails)
    }

    private lateinit var mProductList: ArrayList<Product>
    fun successProductListFromFireStore(productsList: ArrayList<Product>) {
        mProductList = productsList
        getCartItemList()
    }

    private fun getCartItemList() {
        viewModelUser.getCartList(this@CheckoutActivity)
    }

    //todo**************************************************************************************************
    private var mSubTotal: Double = 0.0
    private var mTotalAmount: Double = 0.0
    private fun placeAnOrder() {
        showProgressDialog(resources.getString(R.string.please_wait))
        if (mAddressDetails != null) {
            mOrderDetails = Order(
                UserFireStore().getCurrentUserID(),
                mCartItemList,
                mAddressDetails!!,
                "My order ${System.currentTimeMillis()}",
                mCartItemList[0].image,
                mSubTotal.toString(),
                "10.0", // The Shipping Charge is fixed as $10 for now in our case.
                mTotalAmount.toString(),
                System.currentTimeMillis()
            )

            //TODO:ISSUE HERE
            //checkoutViewModel.placeAnOrder()

            // TODO : Call the function to place the order in the cloud firestore.
            viewModelUser.placeOrder(this@CheckoutActivity, mOrderDetails)
        }
    }
    //todo**************************************************************************************************

    fun successCartItemList(cartList: ArrayList<CartItem>) {
        hideProgressDialog()
        for (product in mProductList) {
            for (cartItem in cartList) {
                if (product.product_id == cartItem.product_id) {
                    cartItem.stock_quantity = product.stock_quantity
                }
            }
        }
        mCartItemList = cartList
        setUpRecyclerview()
        calculatePrice()
        setPrice()
    }

    private fun setUpRecyclerview(){
        rv_cart_list_items.layoutManager = LinearLayoutManager(this@CheckoutActivity)
        rv_cart_list_items.setHasFixedSize(true)
        val cartListAdapter = CartItemsListAdapter(this@CheckoutActivity, mCartItemList, false)
        rv_cart_list_items.adapter = cartListAdapter
    }

    //todo**************************************************************************************************
    private fun calculatePrice(){
        for (item in mCartItemList) {
            val availableQuantity = item.stock_quantity.toInt()
            if (availableQuantity > 0) {
                val price = item.price.toDouble()
                val quantity = item.cart_quantity.toInt()
                mSubTotal += (price * quantity)
            }
        }
      //  checkoutViewModel.calculatePrice(mCartItemList)
        tv_checkout_sub_total.text = "ج.م$mSubTotal"
        tv_checkout_shipping_charge.text = "ج.م10.0"
    }
    //todo**************************************************************************************************

    private fun setPrice(){
        if (mSubTotal > 0) {
            ll_checkout_place_order.visibility = View.VISIBLE
            mTotalAmount = mSubTotal + 10.0
            tv_checkout_total_amount.text = "ج.م$mTotalAmount"
        } else {
            ll_checkout_place_order.visibility = View.GONE
        }
    }

    private fun getProductList() {
        showProgressDialog(resources.getString(R.string.please_wait))
        viewModel.getAllProductList(this@CheckoutActivity)
    }

    private fun setupActionBar() {
        setSupportActionBar(toolbar_checkout_activity)
        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_white_color_back_24dp)
        }
        toolbar_checkout_activity.setNavigationOnClickListener { onBackPressed() }
    }
}