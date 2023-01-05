package com.example.designersstore.presentation.ui.activitydesigners.addressdesigner

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
import com.example.designersstore.data.designerfirestore.DesignerViewModel
import com.example.designersstore.databinding.ActivityAddressListDesignerBinding
import com.example.designersstore.domain.models.AddressClient
import com.example.designersstore.presentation.ui.activity.BaseActivity
import com.example.designersstore.presentation.ui.activitydesigners.AddEditAddressDesigners
import com.example.designersstore.presentation.ui.adapters.AddressListAdapterDesigner
import com.example.designersstore.presentation.utils.Constants
import com.example.myshoppal.utils.SwipeToDeleteCallback
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AddressList_Designer : BaseActivity() {

    private val viewModel: DesignerViewModel by viewModels()
    private val viewModelAddress:Addr_DsViewModel by viewModels()
    lateinit var binding: ActivityAddressListDesignerBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddressListDesignerBinding.inflate(layoutInflater)
        GlobalScope.launch {
            setupActionBar()
            actions()
        }

        setContentView(binding.root)
    }

    fun actions(){
        GlobalScope.launch {
            setUpView()
            getAddressList()
            parcelableAddress()
        }
    }

    private var mSelectAddress: Boolean = false
    private fun parcelableAddress() {
        GlobalScope.launch {
            if (intent.hasExtra(Constants.EXTRA_SELECT_ADDRESS_DESIGNER)) {
                mSelectAddress =
                    intent.getBooleanExtra(Constants.EXTRA_SELECT_ADDRESS_DESIGNER, false)
            }
            if (mSelectAddress) {
                binding.tvTitleDesigner.text = resources.getString(R.string.title_select_address)
            }
        }
    }

    private fun setupActionBar(){
        GlobalScope.launch {
            setSupportActionBar(binding.toolbarAddressListActivityDesigner)
            val actionBar = supportActionBar
            if (actionBar != null) {
                actionBar.setDisplayHomeAsUpEnabled(true)
                actionBar.setHomeAsUpIndicator(R.drawable.ic_white_color_back_24dp)
            }
            binding.toolbarAddressListActivityDesigner.setNavigationOnClickListener { onBackPressed() }
        }
    }

    private fun setUpView() {
        GlobalScope.launch {
            binding.tvAddAddressDesigner.setOnClickListener {
                val intent= Intent(this@AddressList_Designer,
                    AddEditAddressDesigners::class.java)
                startActivityForResult(intent, Constants.ADD_ADDRESS_REQUEST_CODE_CLIENT)
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        GlobalScope.launch {
            withContext(Dispatchers.Main) {
                super.onActivityResult(requestCode, resultCode, data)
                if (resultCode == RESULT_OK) {
                    if (requestCode == Constants.ADD_ADDRESS_REQUEST_CODE_CLIENT) {
                        getAddressList()
                    }
                } else if (resultCode == RESULT_CANCELED) {
                    Log.e("Request Cancelled", "To add the address.")
                }
            }
        }
    }

    private suspend fun getAddressList(){
        withContext(Dispatchers.Main) {
            showProgressDialog(resources.getString(R.string.please_wait))
        }
        viewModel.getAddressesListDesigners(this)
    }

    fun successAddressListFromFirestore(addressList: ArrayList<AddressClient>){
        hideProgressDialog()
        if (addressList.size>0){
            showAddress()
            displayAddressInRecyclerView(addressList)
            showSwipe(addressList)
        }else{
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
        binding.rvAddressListDesigner.visibility= View.VISIBLE
        binding.tvNoAddressFoundDesigner.visibility= View.GONE
    }

    private fun hideAddress(){
        binding.rvAddressListDesigner.visibility = View.GONE
        binding.tvNoAddressFoundDesigner.visibility = View.VISIBLE
    }

    private fun displayAddressInRecyclerView(addressList: ArrayList<AddressClient>) {
        binding.rvAddressListDesigner.layoutManager= LinearLayoutManager(this@AddressList_Designer)
        binding.rvAddressListDesigner.setHasFixedSize(true)
        val addressAdapter= AddressListAdapterDesigner(this,addressList,mSelectAddress)
        binding.rvAddressListDesigner.adapter=addressAdapter
    }

    private fun swipeToEdit() {
        viewModelAddress.createSwipeToEdit(this,binding.rvAddressListDesigner,this)
    }

    private fun swipeToDelete(addressList: ArrayList<AddressClient>) {
        val deleteSwipeHandler = object : SwipeToDeleteCallback(this) {
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                showProgressDialog(resources.getString(R.string.please_wait))
                viewModel.deleteAddressDesigner(
                    this@AddressList_Designer,
                    addressList[viewHolder.adapterPosition].id
                )
                finish()
                Toast.makeText(
                    this@AddressList_Designer,
                    "Your Address was deleted successfully",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
        val deleteItemTouchHelper = ItemTouchHelper(deleteSwipeHandler)
        deleteItemTouchHelper.attachToRecyclerView(binding.rvAddressListDesigner)

       //   viewModelAddress.createSwipeToDelete(this, addressList, binding.rvAddressListDesigner, this)

    }

    fun deleteAddressSuccess() {
        GlobalScope.launch(Dispatchers.IO) {
            hideProgressDialog()
            Toast.makeText(
                this@AddressList_Designer,
                resources.getString(R.string.err_your_address_deleted_successfully),
                Toast.LENGTH_SHORT
            ).show()
            getAddressList()
        }
    }

}