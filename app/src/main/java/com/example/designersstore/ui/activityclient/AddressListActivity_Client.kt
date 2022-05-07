package com.example.designersstore.ui.activityclient

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.designersstore.R
import com.example.designersstore.firestore.FireStoreClass
import com.example.designersstore.models.AddressClient
import com.example.designersstore.ui.activity.BaseActivity
import com.example.designersstore.ui.adapters.AddressListAdapter
import com.example.designersstore.utils.Constants
import com.example.myshoppal.utils.SwipeToDeleteCallback
import com.example.myshoppal.utils.SwipeToEditCallback
import kotlinx.android.synthetic.main.activity_address_list__client.*

class AddressListActivity_Client : BaseActivity() {

    private var mSelectAddress:Boolean=false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_address_list__client)
        setupActionBar()

        tv_add_address_client.setOnClickListener{
            val intent= Intent(this@AddressListActivity_Client, AddEditAddressActivity::class.java)
            startActivityForResult(intent,Constants.ADD_ADDRESS_REQUEST_CODE_CLIENT)
        }
        getAddressList()
        if (intent.hasExtra(Constants.EXTRA_SELECT_ADDRESS_CLIENT)){
            mSelectAddress=intent.getBooleanExtra(Constants.EXTRA_SELECT_ADDRESS_CLIENT,false)
        }
        if (mSelectAddress){
            tv_title_Client.text=resources.getString(R.string.title_select_address)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK){
            if (requestCode == Constants.ADD_ADDRESS_REQUEST_CODE_CLIENT){
                getAddressList()
            }
        }else if (resultCode == Activity.RESULT_CANCELED){
            // A log is printed when user close or cancel the image selection.
            Log.e("Request Cancelled", "To add the address.")
        }
    }

    private fun getAddressList(){
        showProgressDialog(resources.getString(R.string.please_wait))
        FireStoreClass().getAddressesListClient(this)
    }

    fun successAddressListFromFirestore(addressList:ArrayList<AddressClient>){
        hideProgressDialog()
        if (addressList.size>0){
            rv_address_list_client.visibility= View.VISIBLE
            tv_no_address_found_client.visibility=View.GONE
            rv_address_list_client.layoutManager=LinearLayoutManager(this@AddressListActivity_Client)
            rv_address_list_client.setHasFixedSize(true)

            val addressAdapter= AddressListAdapter(this,addressList,mSelectAddress)
            rv_address_list_client.adapter=addressAdapter

            if (!mSelectAddress){
                val editSwipeHandler=object: SwipeToEditCallback(this){
                    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {

                        val adapter = rv_address_list_client.adapter as AddressListAdapter
                        adapter.notifyEditItem(
                            this@AddressListActivity_Client,
                            viewHolder.adapterPosition
                        )
                    }
                }


                val editItemTouchHelper=ItemTouchHelper(editSwipeHandler)
                editItemTouchHelper.attachToRecyclerView(rv_address_list_client)

                val deleteSwipeHandler = object : SwipeToDeleteCallback(this) {
                    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {

                        // Show the progress dialog.
                        showProgressDialog(resources.getString(R.string.please_wait))

                        FireStoreClass().deleteAddress(
                            this@AddressListActivity_Client,
                            addressList[viewHolder.adapterPosition].id
                        )
                    }
                }
                val deleteItemTouchHelper = ItemTouchHelper(deleteSwipeHandler)
                deleteItemTouchHelper.attachToRecyclerView(rv_address_list_client)
            }
        }else{
            rv_address_list_client.visibility = View.GONE
            tv_no_address_found_client.visibility = View.VISIBLE
        }
    }

    fun deleteAddressSuccess() {

        // Hide progress dialog.
        hideProgressDialog()

        Toast.makeText(
            this@AddressListActivity_Client,
            resources.getString(R.string.err_your_address_deleted_successfully),
            Toast.LENGTH_SHORT
        ).show()

        getAddressList()
    }

    private fun setupActionBar(){
        setSupportActionBar(toolbar_address_list_activity_Client)
        val actionBar=supportActionBar
        if (actionBar!=null){
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_white_color_back_24dp)
        }
        toolbar_address_list_activity_Client.setNavigationOnClickListener { onBackPressed() }
    }
}