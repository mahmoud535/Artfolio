package com.example.designersstore.presentation.ui.activityclient.orderdetails

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.designersstore.R
import com.example.designersstore.databinding.ActivityMyOrderDetailsBinding
import com.example.designersstore.domain.models.Order
import com.example.designersstore.presentation.ui.adapters.CartItemsListAdapter
import com.example.designersstore.presentation.utils.Constants
import kotlinx.android.synthetic.main.activity_my_order_details.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

class MyOrderDetailsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMyOrderDetailsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMyOrderDetailsBinding.inflate(layoutInflater)

        GlobalScope.launch(Dispatchers.Main) {
            actions()
        }

        setContentView(binding.root)
    }

    private fun actions() {
        GlobalScope.launch(Dispatchers.Main) {
            setupActionBar()
            putExtra()
        }
    }

    private fun putExtra() {
        GlobalScope.launch(Dispatchers.Main) {
            var myOrderDetails: Order = Order()
            if (intent.hasExtra(Constants.EXTRA_MY_ORDER_DETAILS)) {
                myOrderDetails =
                    intent.getParcelableExtra<Order>(Constants.EXTRA_MY_ORDER_DETAILS)!!
            }
            calculateOrderTime(myOrderDetails)
        }
    }

    private fun setupActionBar() {
        GlobalScope.launch(Dispatchers.Main) {
            setSupportActionBar(toolbar_my_order_details_activity)
            val actionBar = supportActionBar
            if (actionBar != null) {
                actionBar.setDisplayHomeAsUpEnabled(true)
                actionBar.setHomeAsUpIndicator(R.drawable.ic_white_color_back_24dp)
            }
            toolbar_my_order_details_activity.setNavigationOnClickListener { onBackPressed() }
        }
    }

    private fun calculateOrderTime(orderDetails: Order) {
        //لعمل وقت يحسم مده طب المنتج
        binding.tvOrderDetailsId.text = orderDetails.title
        val dateFormat = "dd MMM yyyy HH:mm"
        val formatter = SimpleDateFormat(dateFormat, Locale.getDefault())
        val calender: Calendar = Calendar.getInstance()
        calender.timeInMillis = orderDetails.order_datetime
        val orderDateTime = formatter.format(calender.time)
        binding.tvOrderDetailsDate.text = orderDateTime
        val diffInMilliSeconds: Long = System.currentTimeMillis() - orderDetails.order_datetime
        val diffInHours: Long = TimeUnit.MILLISECONDS.toHours(diffInMilliSeconds)
        Log.e("Difference in Hours", "$diffInHours")
        when {
            diffInHours < 1 -> {
                requestIsPending()
            }
            diffInHours < 2 -> {
                orderBeingDelivered()
            }
            else -> {
                orderHasDelivered()
            }
        }
        recyclerView(orderDetails)
        setOrderInfo(orderDetails)
    }

    private fun requestIsPending() {
        binding.tvOrderStatus.text = resources.getString(R.string.order_status_pending)
        binding.tvOrderStatus.setTextColor(
            ContextCompat.getColor(
                this@MyOrderDetailsActivity,
                R.color.colorAccent
            )
        )
    }

    private fun orderBeingDelivered() {
        binding.tvOrderStatus.text = resources.getString(R.string.order_status_in_process)
        binding.tvOrderStatus.setTextColor(
            ContextCompat.getColor(
                this@MyOrderDetailsActivity,
                R.color.colorOrderStatusInProcess
            )
        )
    }

    private fun orderHasDelivered() {
        binding.tvOrderStatus.text = resources.getString(R.string.order_status_delivered)
        binding.tvOrderStatus.setTextColor(
            ContextCompat.getColor(
                this@MyOrderDetailsActivity,
                R.color.colorOrderStatusDelivered
            )
        )
    }

    private fun recyclerView(orderDetails: Order) {
        GlobalScope.launch(Dispatchers.Main) {
            rv_my_order_items_list.layoutManager = LinearLayoutManager(this@MyOrderDetailsActivity)
            rv_my_order_items_list.setHasFixedSize(true)
            val cartListAdapter =
                CartItemsListAdapter(this@MyOrderDetailsActivity, orderDetails.items, false)
            rv_my_order_items_list.adapter = cartListAdapter
        }
    }

    private fun setOrderInfo(orderDetails: Order) {
        GlobalScope.launch(Dispatchers.Main) {
            tv_my_order_details_address_type.text = orderDetails.address.type
            tv_my_order_details_full_name.text = orderDetails.address.name
            tv_my_order_details_address.text =
                "${orderDetails.address.address}, ${orderDetails.address.zipCode}"
            tv_my_order_details_additional_note.text = orderDetails.address.additionalNote

            if (orderDetails.address.otherDetails.isNotEmpty()) {
                tv_my_order_details_other_details.visibility = View.VISIBLE
                tv_my_order_details_other_details.text = orderDetails.address.otherDetails
            } else {
                tv_my_order_details_other_details.visibility = View.GONE
            }
            tv_my_order_details_mobile_number.text = orderDetails.address.mobileNumber

            tv_order_details_sub_total.text = orderDetails.sub_total_amount
            tv_order_details_shipping_charge.text = orderDetails.shipping_charge
            tv_order_details_total_amount.text = orderDetails.total_amount
        }
    }
}