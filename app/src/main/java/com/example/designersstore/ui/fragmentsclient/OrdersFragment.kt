package com.example.designersstore.ui.fragmentsclient

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.designersstore.R
import com.example.designersstore.firestore.FireStoreClass
import com.example.designersstore.models.Order
import com.example.designersstore.ui.adapters.MyOrdersListAdapter
import kotlinx.android.synthetic.main.fragment_orders.*

class OrdersFragment : BaseFragment() {

//    private lateinit var notificationsViewModel: NotificationsViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val root = inflater.inflate(R.layout.fragment_orders, container, false)

        return root
    }

    fun populateOrdersListInUI(ordersList:ArrayList<Order>){
        hideProgressDialog()

        if (ordersList.size > 0){
            rv_my_order_items.visibility=View.VISIBLE
            tv_no_orders_found.visibility=View.GONE

            rv_my_order_items.layoutManager= LinearLayoutManager(activity)
            rv_my_order_items.setHasFixedSize(true)

            val myOrdersAdapter= MyOrdersListAdapter(requireActivity(),ordersList)
            rv_my_order_items.adapter=myOrdersAdapter
        }
        else{
            rv_my_order_items.visibility=View.GONE
            tv_no_orders_found.visibility=View.VISIBLE
        }
    }

    private fun getMyOrdersList(){
        showProgressDialog(resources.getString(R.string.please_wait))

        FireStoreClass().getMyOrdersList(this@OrdersFragment)
    }
    override fun onResume() {
        super.onResume()

        getMyOrdersList()
    }
}