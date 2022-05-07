package com.example.designersstore.ui.activitydesigners

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.designersstore.R
import com.example.designersstore.firestore.FireStoreClass
import com.example.designersstore.models.AddressDesigner
import com.example.designersstore.ui.activity.BaseActivity
import com.example.designersstore.ui.adapters.AddressListAdapterDesigner
import com.example.designersstore.utils.Constants
import com.example.myshoppal.utils.SwipeToDeleteCallback
import com.example.myshoppal.utils.SwipeToEditCallback
import kotlinx.android.synthetic.main.activity_address_list__designer.*

class AddressListActivity_Designer : BaseActivity() {
    private var mSelectAddress: Boolean = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_address_list__designer)

        setupActionBar()

        //  للضغط علي الزر والزهاب اللي الاكتيفيتي AddEditAddressActivity لملئ البيانات الخاصه بالعنوان
        tv_add_address_Designer.setOnClickListener {
            val intent= Intent(this@AddressListActivity_Designer,AddEditAddressActivityDesigners::class.java)
            startActivityForResult(intent, Constants.ADD_ADDRESS_REQUEST_CODE_DESIGNER)
        }

        getAddressList()
        if (intent.hasExtra(Constants.EXTRA_SELECT_ADDRESS_DESIGNER)){
            mSelectAddress=intent.getBooleanExtra(Constants.EXTRA_SELECT_ADDRESS_DESIGNER,false)
        }
        if (mSelectAddress){
            tv_title_Designer.text=resources.getString(R.string.title_select_address)
        }
    }



    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == Constants.ADD_ADDRESS_REQUEST_CODE_DESIGNER) {

                getAddressList()
            }
        } else if (resultCode == Activity.RESULT_CANCELED) {
            // A log is printed when user close or cancel the image selection.
            Log.e("Request Cancelled", "To add the address.")
        }
    }

    private fun getAddressList(){
        showProgressDialog(resources.getString(R.string.please_wait))
        FireStoreClass().getAddressesListDesigners(this)
    }

    fun successAddressListFromFirestore(addressList: ArrayList<AddressDesigner>){
        hideProgressDialog()
        if (addressList.size>0){
            rv_address_list_Designer.visibility= View.VISIBLE
            tv_no_address_found_Designer.visibility= View.GONE
            rv_address_list_Designer.layoutManager= LinearLayoutManager(this@AddressListActivity_Designer)
            rv_address_list_Designer.setHasFixedSize(true)

            val addressAdapter= AddressListAdapterDesigner(this,addressList,mSelectAddress)
            rv_address_list_Designer.adapter=addressAdapter

            if (!mSelectAddress){
                val editSwipeHandler=object : SwipeToEditCallback(this){
                    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {

                        val adapter = rv_address_list_Designer.adapter as AddressListAdapterDesigner
                        adapter.notifyEditItem(
                            this@AddressListActivity_Designer,
                            viewHolder.adapterPosition
                        )
                    }

                }

                val editItemTouchHelper = ItemTouchHelper(editSwipeHandler)
                editItemTouchHelper.attachToRecyclerView(rv_address_list_Designer)

                val deleteSwipeHandler = object : SwipeToDeleteCallback(this) {
                    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {

                        // Show the progress dialog.
                        showProgressDialog(resources.getString(R.string.please_wait))

                        FireStoreClass().deleteAddressDesigner(
                            this@AddressListActivity_Designer,
                            addressList[viewHolder.adapterPosition].id
                        )
                    }
                }
                val deleteItemTouchHelper = ItemTouchHelper(deleteSwipeHandler)
                deleteItemTouchHelper.attachToRecyclerView(rv_address_list_Designer)
            }


        }else{
            rv_address_list_Designer.visibility = View.GONE
            tv_no_address_found_Designer.visibility = View.VISIBLE
        }
    }

    fun deleteAddressSuccess() {

        // Hide progress dialog.
        hideProgressDialog()

        Toast.makeText(
            this@AddressListActivity_Designer,
            resources.getString(R.string.err_your_address_deleted_successfully),
            Toast.LENGTH_SHORT
        ).show()

        getAddressList()
    }
    private fun setupActionBar(){
        setSupportActionBar(toolbar_address_list_activity_Designer)

        val actionBar=supportActionBar
        if (actionBar!=null){
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_white_color_back_24dp)
        }

        toolbar_address_list_activity_Designer.setNavigationOnClickListener { onBackPressed() }
    }
}