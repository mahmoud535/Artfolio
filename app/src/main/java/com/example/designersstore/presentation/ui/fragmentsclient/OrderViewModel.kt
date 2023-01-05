package com.example.designersstore.presentation.ui.fragmentsclient

import android.content.Context
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.example.designersstore.data.userfirestore.UserFirestoreViewModel
import com.example.designersstore.domain.models.Order
import com.example.myshoppal.utils.SwipeToDeleteCallback

class OrderViewModel:ViewModel() {

    private val viewModel: UserFirestoreViewModel
    private val baseFragment :BaseFragment
    init {
        viewModel = UserFirestoreViewModel()
        baseFragment = BaseFragment()
    }

    fun swipeToDelete(context: Context, orderList: ArrayList<Order>, recyclerView: RecyclerView, fragment: OrdersFragment) {
        val deleteSwipeHandler = object : SwipeToDeleteCallback(context) {
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
//                if (true) {
//                      baseFragment.showProgressDialog(context.resources.getString(R.string.please_wait))
//                }
                viewModel.deleteOrder(
                   fragment,
                    orderList[viewHolder.adapterPosition].id
                )

                Toast.makeText(
                    context,
                    "Your Order was deleted successfully",
                    Toast.LENGTH_SHORT
                ).show()
              //  baseFragment.hideProgressDialog()
                val direction = OrdersFragmentDirections.actionNavigationOrdersToNavigationDashboard()
                Fragment().findNavController().navigate(direction)
            }
        }
        val deleteItemTouchHelper = ItemTouchHelper(deleteSwipeHandler)
        deleteItemTouchHelper.attachToRecyclerView(recyclerView)

        //   addressListViewModel.createSwipeToDelete(this, addressList, rv_address_list_client, this)
    }
}