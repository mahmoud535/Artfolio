package com.example.designersstore.data.repositoryimp

import android.app.Activity
import android.content.Context
import android.widget.Toast
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.example.designersstore.data.userfirestore.UserFirestoreViewModel
import com.example.designersstore.domain.models.AddressClient
import com.example.designersstore.presentation.ui.activityclient.addresslistclient.AddressList_Client
import com.example.designersstore.domain.models.repository.AddressListClient
import com.example.designersstore.presentation.ui.adapters.AddressListAdapter
import com.example.myshoppal.utils.SwipeToDeleteCallback
import com.example.myshoppal.utils.SwipeToEditCallback

class AddressListClientImp() : AddressListClient {
    private val viewModel: UserFirestoreViewModel
    init {
        viewModel= UserFirestoreViewModel()
    }

    override  fun createSwipeToEdit(context: Context, recyclerView: RecyclerView,activity: Activity){

            val editSwipeHandler = object : SwipeToEditCallback(context) {
                override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                    when (direction) {
                        ItemTouchHelper.RIGHT -> {
                            val adapter =recyclerView.adapter as AddressListAdapter
                            adapter.notifyEditItem(
                                activity,
                                viewHolder.adapterPosition
                            )
                            Toast.makeText(context,"Edit Your Address",Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
            val touchHelper = ItemTouchHelper(editSwipeHandler)
            touchHelper.attachToRecyclerView(recyclerView)
    }

    override fun createSwipeToDelete(context: Context,addressList: ArrayList<AddressClient>,recyclerView: RecyclerView,activity: AddressList_Client) {
        val deleteSwipeHandler = object : SwipeToDeleteCallback(context) {
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
             // showProgressDialog(getcon.resources.getString(R.string.please_wait))
                when (direction) {
                    ItemTouchHelper.LEFT -> {
                        viewModel.deleteAddress(
                            activity,
                            addressList[viewHolder.adapterPosition].id
                        )
                      // finish()
                        Toast.makeText(
                            context,
                            "Your Address was deleted successfully",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

            }
        }
        val deleteItemTouchHelper = ItemTouchHelper(deleteSwipeHandler)
        deleteItemTouchHelper.attachToRecyclerView(recyclerView)
    }
}