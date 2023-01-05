package com.example.designersstore.presentation.ui.fragmentsdesigners

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.viewModels
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.designersstore.R
import com.example.designersstore.data.designerfirestore.DesignerFirestore
import com.example.designersstore.data.designerfirestore.DesignerViewModel
import com.example.designersstore.domain.models.Order
import com.example.designersstore.domain.models.SoldProduct
import com.example.designersstore.presentation.ui.adapters.SoldProductsListAdapter
import com.example.designersstore.presentation.ui.fragmentsclient.BaseFragment
import com.example.designersstore.presentation.ui.fragmentsclient.OrdersFragment
import kotlinx.android.synthetic.main.fragment_sold_products.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch


class SoldProductsFragment : BaseFragment() {

 // private lateinit var notificationsViewModel: NotificationsViewModel
    private val ordersList:ArrayList<Order> = ArrayList()
    private val soldId:String = ""
 private val viewModel: DesignerViewModel by viewModels()
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
    viewModel.getSoldProductsList(this@SoldProductsFragment)
  }

  fun successSoldProductsList(soldProductsList: ArrayList<SoldProduct>) {
    hideProgressDialog()
    if (soldProductsList.size > 0) {
      rv_sold_product_items.visibility = View.VISIBLE
      tv_no_sold_products_found.visibility = View.GONE

      rv_sold_product_items.layoutManager = LinearLayoutManager(activity)
      rv_sold_product_items.setHasFixedSize(true)

      val soldProductsListAdapter =
        SoldProductsListAdapter(requireActivity(), soldProductsList)
      rv_sold_product_items.adapter = soldProductsListAdapter

       // viewModel.deleteSoldProduct(soldId,SoldProductsFragment())
    } else {
      rv_sold_product_items.visibility = View.GONE
      tv_no_sold_products_found.visibility = View.VISIBLE
    }
  }

    fun soldProductDeletedSuccess() {
        GlobalScope.launch(Dispatchers.IO) {
            hideProgressDialog()
            Toast.makeText(
                requireContext(),
                resources.getString(R.string.err_your_address_deleted_successfully),
                Toast.LENGTH_SHORT
            ).show()
            getSoldProductsList()
        }
    }
}