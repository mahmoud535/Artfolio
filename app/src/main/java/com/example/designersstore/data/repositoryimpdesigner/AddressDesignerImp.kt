package com.example.designersstore.data.repositoryimpdesigner

import android.app.Activity
import android.app.Application
import android.content.Context
import android.widget.Toast
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.example.designersstore.data.designerfirestore.DesignerViewModel
import com.example.designersstore.domain.models.AddressClient
import com.example.designersstore.domain.models.repository.AddressDesignerRepo
import com.example.designersstore.presentation.ui.activitydesigners.addressdesigner.AddressList_Designer
import com.example.designersstore.presentation.ui.adapters.AddressListAdapterDesigner
import com.example.myshoppal.utils.SwipeToDeleteCallback
import com.example.myshoppal.utils.SwipeToEditCallback

class AddressDesignerImp(application: Application): AddressDesignerRepo {

    private val viewModel: DesignerViewModel
    init {
        viewModel = DesignerViewModel(application)
    }

    override fun createSwipeToEdit(
        context: Context,
        recyclerView: RecyclerView,
        activity: Activity
    ) {

        val editSwipeHandler = object : SwipeToEditCallback(context) {
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                when (direction) {
                    ItemTouchHelper.RIGHT -> {
                        val adapter =recyclerView.adapter as AddressListAdapterDesigner
                        adapter.notifyEditItem(
                            activity,
                            viewHolder.adapterPosition
                        )
                        Toast.makeText(context,"Edit Your Address", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
        val touchHelper = ItemTouchHelper(editSwipeHandler)
        touchHelper.attachToRecyclerView(recyclerView)
    }

    override fun createSwipeToDelete(
        context: Context,
        addressList: ArrayList<AddressClient>,
        recyclerView: RecyclerView,
        activity: AddressList_Designer
    ) {
        val deleteSwipeHandler = object : SwipeToDeleteCallback(context) {
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                // showProgressDialog(getcon.resources.getString(R.string.please_wait))
                when (direction) {
                    ItemTouchHelper.LEFT -> {
                        viewModel.deleteAddressDesigner(
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