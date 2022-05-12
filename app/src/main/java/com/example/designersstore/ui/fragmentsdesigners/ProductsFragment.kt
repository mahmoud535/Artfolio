package com.example.designersstore.ui.fragmentsdesigners

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.*
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.designersstore.R
import com.example.designersstore.firestore.FireStoreClass
import com.example.designersstore.models.Product
import com.example.designersstore.ui.activityclient.SettingsActivity
import com.example.designersstore.ui.activitydesigners.AddproductActivity
import com.example.designersstore.ui.activitydesigners.Settings_Designers_Activity
import com.example.designersstore.ui.adapters.MyProductListAdapter
import com.example.designersstore.ui.fragmentsclient.BaseFragment
import kotlinx.android.synthetic.main.products_fragment.*


class ProductsFragment : BaseFragment() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    //If we want to see the option menu in fragment we need to add it.
    setHasOptionsMenu(true)
  }

  fun deleteProduct(productID:String){
    showAlertDialogToDeleteProduct(productID)
  }

  private fun showAlertDialogToDeleteProduct(productID:String) {
    val builder = AlertDialog.Builder(requireActivity())
    //set title for alert dialog
    builder.setTitle(resources.getString(R.string.delete_dialog_title))
    //set message for alert dialog
    builder.setMessage(resources.getString(R.string.delete_dialog_message))
    builder.setIcon(android.R.drawable.ic_dialog_alert)


    //performing positive action
    builder.setPositiveButton(resources.getString(R.string.yes)) { dialogInterface, _ ->
      // TODO Step 7: Call the function to delete the product from cloud firestore.
      // START
      // Show the progress dialog.
      showProgressDialog(resources.getString(R.string.please_wait))
      // Call the function of Firestore class.
      FireStoreClass().deleteProduct(this@ProductsFragment, productID)
      // END

      dialogInterface.dismiss()
    }
    //performing negative action
    builder.setNegativeButton(resources.getString(R.string.no)) { dialogInterface, _ ->

      dialogInterface.dismiss()
    }
    // Create the AlertDialog
    val alertDialog: AlertDialog = builder.create()
    // Set other dialog properties
    alertDialog.setCancelable(false)
    alertDialog.show()

  }

  fun productDeleteSuccess(){
    hideProgressDialog()

    Toast.makeText(
      requireActivity(),
      resources.getString(R.string.product_delete_success_message),
      Toast.LENGTH_SHORT
    ).show()
    getProductListFromFireStore()
  }

  fun successProductsListFromFireStore(productsList: ArrayList<Product>){
    // Hide Progress dialog.
    hideProgressDialog()
    if (productsList.size>0){
      rv_my_product_items.visibility=View.VISIBLE
      tv_no_products_found.visibility=View.GONE

      rv_my_product_items.layoutManager=LinearLayoutManager(activity)
      rv_my_product_items.setHasFixedSize(true)
      val adapterProducts=MyProductListAdapter(requireActivity(),productsList,this)
      rv_my_product_items.adapter=adapterProducts
    }else{
      rv_my_product_items.visibility=View.GONE
      tv_no_products_found.visibility=View.VISIBLE
    }
  }

  private fun getProductListFromFireStore(){
    showProgressDialog(resources.getString(R.string.please_wait))
    FireStoreClass().getProductsList(this)
  }

  override fun onResume() {
    super.onResume()
    getProductListFromFireStore()
  }

  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View? {
    val root = inflater.inflate(R.layout.products_fragment, container, false)

    return root
  }

  override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
    inflater.inflate(R.menu.product_designer,menu)
    inflater.inflate(R.menu.add_product_menu,menu)
    super.onCreateOptionsMenu(menu, inflater)
  }

  override fun onOptionsItemSelected(item: MenuItem): Boolean {
    val id =item.itemId

    when(id){
      R.id.action_settings_designer->{
        //TODO Step 9:Launch the SettingActivity on Click of action item.
        startActivity(Intent(activity, Settings_Designers_Activity::class.java))
        return true
      }
      R.id.action_add_product->{
        startActivity(Intent(activity, AddproductActivity::class.java))
        return true
      }
    }
    return super.onOptionsItemSelected(item)
  }

}