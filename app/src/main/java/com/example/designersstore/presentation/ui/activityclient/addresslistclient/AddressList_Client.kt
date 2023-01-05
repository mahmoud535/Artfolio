package com.example.designersstore.presentation.ui.activityclient.addresslistclient

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.designersstore.R
import com.example.designersstore.data.userfirestore.UserFirestoreViewModel
import com.example.designersstore.databinding.ActivityAddressListClientBinding
import com.example.designersstore.domain.models.AddressClient
import com.example.designersstore.presentation.ui.activity.BaseActivity
import com.example.designersstore.presentation.ui.activityclient.addeditaddressactivity.AddEditAddress
import com.example.designersstore.presentation.ui.adapters.AddressListAdapter
import com.example.designersstore.presentation.utils.Constants
import com.example.myshoppal.utils.SwipeToDeleteCallback
import kotlinx.android.synthetic.main.activity_address_list__client.*
import kotlinx.coroutines.*

class AddressList_Client : BaseActivity() {

    private val viewModel: UserFirestoreViewModel by viewModels()
    private val addressListViewModel: A_LClientViewModel by viewModels()
    lateinit var binding: ActivityAddressListClientBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddressListClientBinding.inflate(layoutInflater)

        GlobalScope.launch {
            setupActionBar()
            actions()
        }
        setContentView(binding.root)
    }

    fun actions() {
        GlobalScope.launch {
            setUpView()
            getAddressList()
            parcelableAddress()
        }
    }

    private var mSelectAddress: Boolean = false
    private fun parcelableAddress() {
        GlobalScope.launch {
            if (intent.hasExtra(Constants.EXTRA_SELECT_ADDRESS_CLIENT)) {
                mSelectAddress =
                    intent.getBooleanExtra(Constants.EXTRA_SELECT_ADDRESS_CLIENT, false)
            }
            if (mSelectAddress) {
                tv_title_Client.text = resources.getString(R.string.title_select_address)
            }
        }
    }

    private fun setupActionBar() {
        GlobalScope.launch {
            setSupportActionBar(toolbar_address_list_activity_Client)
            val actionBar = supportActionBar
            if (actionBar != null) {
                actionBar.setDisplayHomeAsUpEnabled(true)
                actionBar.setHomeAsUpIndicator(R.drawable.ic_white_color_back_24dp)
            }
            toolbar_address_list_activity_Client.setNavigationOnClickListener { onBackPressed() }
        }
    }

    private fun setUpView() {
        GlobalScope.launch {
            binding.tvAddAddressClient.setOnClickListener {
                val intent =
                    Intent(this@AddressList_Client, AddEditAddress::class.java)
                startActivityForResult(intent, Constants.ADD_ADDRESS_REQUEST_CODE_CLIENT)
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        GlobalScope.launch {
            withContext(Dispatchers.Main) {
                super.onActivityResult(requestCode, resultCode, data)
                if (resultCode == Activity.RESULT_OK) {
                    if (requestCode == Constants.ADD_ADDRESS_REQUEST_CODE_CLIENT) {
                        getAddressList()
                    }
                } else if (resultCode == Activity.RESULT_CANCELED) {
                    Log.e("Request Cancelled", "To add the address.")
                }
            }
        }
    }

    private suspend fun getAddressList() {
        withContext(Dispatchers.Main) {
            showProgressDialog(resources.getString(R.string.please_wait))
        }
        viewModel.getAddressesListClient(this)
    }

    fun successAddressListFromFirestore(addressList: ArrayList<AddressClient>) {
        hideProgressDialog()
        if (addressList.size > 0) {
            showAddress()
            displayAddressInRecyclerView(addressList)
            showSwipe(addressList)
        } else {
            hideAddress()
        }
    }

    private fun showSwipe(addressList: ArrayList<AddressClient>){
        if (!mSelectAddress) {
            swipeToEdit()
            swipeToDelete(addressList)
        }
    }

    private fun showAddress(){
        rv_address_list_client.visibility = View.VISIBLE
        tv_no_address_found_client.visibility = View.GONE
    }

    private fun hideAddress(){
        rv_address_list_client.visibility = View.GONE
        tv_no_address_found_client.visibility = View.VISIBLE
    }

    private fun displayAddressInRecyclerView(addressList: ArrayList<AddressClient>) {
        rv_address_list_client.layoutManager =
            LinearLayoutManager(this@AddressList_Client)
        rv_address_list_client.setHasFixedSize(true)
        val addressAdapter = AddressListAdapter(this, addressList, mSelectAddress)
        rv_address_list_client.adapter = addressAdapter
    }

    private fun swipeToEdit() {
        addressListViewModel.createSwipeToEdit(this, rv_address_list_client, this)
    }

    private fun swipeToDelete(addressList: ArrayList<AddressClient>) {
        val deleteSwipeHandler = object : SwipeToDeleteCallback(this) {
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                showProgressDialog(resources.getString(R.string.please_wait))
                viewModel.deleteAddress(
                    this@AddressList_Client,
                    addressList[viewHolder.adapterPosition].id
                )
                finish()
                Toast.makeText(
                    this@AddressList_Client,
                    "Your Address was deleted successfully",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
        val deleteItemTouchHelper = ItemTouchHelper(deleteSwipeHandler)
        deleteItemTouchHelper.attachToRecyclerView(rv_address_list_client)

     //   addressListViewModel.createSwipeToDelete(this, addressList, rv_address_list_client, this)
    }

    suspend fun deleteAddressSuccess() {
        GlobalScope.launch(Dispatchers.IO) {
            hideProgressDialog()
            Toast.makeText(
                this@AddressList_Client,
                resources.getString(R.string.err_your_address_deleted_successfully),
                Toast.LENGTH_SHORT
            ).show()
            getAddressList()
        }
    }
}