package com.example.designersstore.ui.fragmentsdesigners

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.designersstore.R
import com.example.designersstore.firestore.FireStoreClass
import com.example.designersstore.models.SoldProduct
import com.example.designersstore.ui.adapters.SoldProductsListAdapter
import com.example.designersstore.ui.fragmentsclient.BaseFragment
import kotlinx.android.synthetic.main.fragment_sold_products.*


class SoldProductsFragment : BaseFragment() {

 // private lateinit var notificationsViewModel: NotificationsViewModel

  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View? {

    return inflater.inflate(R.layout.fragment_sold_products, container, false)
  }

  override fun onResume() {
    super.onResume()

    getSoldProductsList()
  }

  private fun getSoldProductsList() {
    // Show the progress dialog.
    showProgressDialog(resources.getString(R.string.please_wait))

    // Call the function of Firestore class.
    FireStoreClass().getSoldProductsList(this@SoldProductsFragment)
  }

  fun successSoldProductsList(soldProductsList: ArrayList<SoldProduct>) {

    // Hide Progress dialog.
    hideProgressDialog()

    if (soldProductsList.size > 0) {
      rv_sold_product_items.visibility = View.VISIBLE
      tv_no_sold_products_found.visibility = View.GONE

      rv_sold_product_items.layoutManager = LinearLayoutManager(activity)
      rv_sold_product_items.setHasFixedSize(true)

      val soldProductsListAdapter =
        SoldProductsListAdapter(requireActivity(), soldProductsList)
      rv_sold_product_items.adapter = soldProductsListAdapter
    } else {
      rv_sold_product_items.visibility = View.GONE
      tv_no_sold_products_found.visibility = View.VISIBLE
    }
  }
}