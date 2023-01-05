package com.example.designersstore.presentation.ui.fragmentsclient

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.designersstore.R
import com.example.designersstore.data.designerfirestore.DesignerViewModel
import com.example.designersstore.data.userfirestore.UserFirestoreViewModel
import com.example.designersstore.databinding.FragmentOrdersBinding
import com.example.designersstore.domain.models.Order
import com.example.designersstore.domain.models.SoldProduct
import com.example.designersstore.presentation.ui.activityclient.addresslistclient.AddressList_Client
import com.example.designersstore.presentation.ui.adapters.MyOrdersListAdapter
import com.example.myshoppal.utils.SwipeToDeleteCallback
import kotlinx.android.synthetic.main.fragment_orders.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class OrdersFragment : BaseFragment() {
    private var mSelectAddress: Boolean = false
    private val viewModel: UserFirestoreViewModel by viewModels()
    private val viewModelDesigner: DesignerViewModel by viewModels()
    private val orderViewModel:OrderViewModel by viewModels()
    private val soldProductList:ArrayList<SoldProduct> = ArrayList()
    private lateinit var binding:FragmentOrdersBinding
        val activity:AddressList_Client = AddressList_Client()
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
       // val root = inflater.inflate(R.layout.fragment_orders, container, false)
        binding = FragmentOrdersBinding.inflate(layoutInflater)

        return binding.root
    }

    fun populateOrdersListInUI(ordersList:ArrayList<Order>){
        GlobalScope.launch(Dispatchers.Main) {
        hideProgressDialog()
        if (ordersList.size > 0){
            showOrder()
            recyclerView(ordersList)
            showSwipe(ordersList)
        }
        else{
            hideOrder()
        }
        }
    }

    fun showSwipe(orderList: ArrayList<Order>){
        if (!mSelectAddress) {
            swipeToDelete(orderList)
        }
    }

    private suspend fun showOrder(){
        withContext(Dispatchers.Main) {
            binding.rvMyOrderItems.visibility = View.VISIBLE
            binding.tvNoOrdersFound.visibility = View.GONE
        }
    }

    private suspend fun hideOrder(){
        withContext(Dispatchers.Main) {
            binding.rvMyOrderItems.visibility = View.GONE
            binding.tvNoOrdersFound.visibility = View.VISIBLE
        }
    }

    private fun recyclerView(ordersList:ArrayList<Order>){
        GlobalScope.launch(Dispatchers.Main) {
            binding.rvMyOrderItems.layoutManager = LinearLayoutManager(activity)
            binding.rvMyOrderItems.setHasFixedSize(true)
            val myOrdersAdapter = MyOrdersListAdapter(requireActivity(), ordersList)
            binding.rvMyOrderItems.adapter = myOrdersAdapter


        }
    }

    private fun getMyOrdersList(){
        showProgressDialog(resources.getString(R.string.please_wait))
        viewModel.getMyOrdersList(this@OrdersFragment)
    }

    override fun onResume() {
        super.onResume()
        getMyOrdersList()
    }

     fun swipeToDelete(orderList: ArrayList<Order>) {
        val deleteSwipeHandler = object : SwipeToDeleteCallback(requireContext()) {
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                showProgressDialog(resources.getString(R.string.please_wait))
                viewModel.deleteOrder(
                    OrdersFragment(),
                    orderList[viewHolder.adapterPosition].id
                )
//                 viewModelDesigner.deleteSoldProduct(
//                     soldProductList[viewHolder.adapterPosition].id,
//                     SoldProductsFragment(),
//                 )
                Toast.makeText(
                    requireContext(),
                    "Your Order was deleted successfully",
                    Toast.LENGTH_SHORT
                ).show()
                hideProgressDialog()
                val direction = OrdersFragmentDirections.actionNavigationOrdersToNavigationDashboard()
                findNavController().navigate(direction)
            }
        }
        val deleteItemTouchHelper = ItemTouchHelper(deleteSwipeHandler)
        deleteItemTouchHelper.attachToRecyclerView(rv_my_order_items)
     //   orderViewModel.swipeToDelete(requireContext(),orderList,rv_my_order_items,OrdersFragment())
    }

    suspend fun deleteOrderSuccess() {
        GlobalScope.launch(Dispatchers.IO) {
            hideProgressDialog()
            Toast.makeText(
                requireContext(),
                resources.getString(R.string.err_your_address_deleted_successfully),
                Toast.LENGTH_SHORT
            ).show()
            getMyOrdersList()
        }
    }
}