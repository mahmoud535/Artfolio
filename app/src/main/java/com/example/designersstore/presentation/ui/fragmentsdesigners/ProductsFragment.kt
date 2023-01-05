package com.example.designersstore.presentation.ui.fragmentsdesigners

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.Toast
import androidx.activity.viewModels
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.designersstore.R
import com.example.designersstore.data.userfirestore.UserFireStore
import com.example.designersstore.data.userfirestore.UserFirestoreViewModel
import com.example.designersstore.databinding.FragmentSoldProductsBinding
import com.example.designersstore.databinding.ProductsFragmentBinding
import com.example.designersstore.domain.models.Product
import com.example.designersstore.presentation.ui.activitydesigners.AddproductActivity
import com.example.designersstore.presentation.ui.activitydesigners.Settings_Designers_Activity
import com.example.designersstore.presentation.ui.adapters.MyProductListAdapter
import com.example.designersstore.presentation.ui.fragmentsclient.BaseFragment
import kotlinx.android.synthetic.main.activity_chat_log_client.*
import kotlinx.android.synthetic.main.products_fragment.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class ProductsFragment : BaseFragment() {
    private val viewModel: UserFirestoreViewModel by viewModels()
    private val viewModelProduct: ProductsViewModel by viewModels()
    private val products: ArrayList<Product> = ArrayList()
    private lateinit var binding: ProductsFragmentBinding
    var productID: String = ""

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = ProductsFragmentBinding.inflate(layoutInflater)
        GlobalScope.launch(Dispatchers.IO) {
            withContext(Dispatchers.Main){
                setUpView()
            }
            refresh()
        }
        return binding.root
    }

    private suspend fun refresh(){
        GlobalScope.launch(Dispatchers.IO) {
            binding.swipeToRefresh.setOnRefreshListener {
                getProductListFromFireStore()
                Toast.makeText(requireContext(), "page refreshed!", Toast.LENGTH_SHORT).show()
                binding.swipeToRefresh.isRefreshing = false
            }
        }
    }


    fun deleteProduct(productID: String) {
        GlobalScope.launch(Dispatchers.Main) {
            showAlertDialogToDeleteProduct(productID)
        }
    }

    private fun showAlertDialogToDeleteProduct(productID: String) {
        GlobalScope.launch(Dispatchers.Main) {
            val builder = AlertDialog.Builder(requireActivity())
            builder.setTitle(resources.getString(R.string.delete_dialog_title))
            builder.setMessage(resources.getString(R.string.delete_dialog_message))
            builder.setIcon(android.R.drawable.ic_dialog_alert)
            builder.setPositiveButton(resources.getString(R.string.yes)) { dialogInterface, _ ->
                GlobalScope.launch { withContext(Dispatchers.Main) {
                    showProgressDialog(resources.getString(R.string.please_wait))
                }  }
                viewModel.deleteProduct(this@ProductsFragment, productID)
                dialogInterface.dismiss()
            }
            builder.setNegativeButton(resources.getString(R.string.no)) { dialogInterface, _ ->
                dialogInterface.dismiss()
            }
            val alertDialog: AlertDialog = builder.create()
            alertDialog.setCancelable(false)
            alertDialog.show()
        }
     //   viewModelProduct.showAlertDialogToDeleteProduct(productID)
    }

    fun productDeleteSuccess() {
        GlobalScope.launch(Dispatchers.IO) {
            withContext(Dispatchers.Main) {
                hideProgressDialog()
                Toast.makeText(
                    requireActivity(),
                    resources.getString(R.string.product_delete_success_message),
                    Toast.LENGTH_SHORT
                ).show()
            }
            getProductListFromFireStore()
        }
    }

    private fun getProductListFromFireStore() {
        GlobalScope.launch(Dispatchers.IO) {
            withContext(Dispatchers.Main) {
            showProgressDialog(resources.getString(R.string.please_wait))
        }
            viewModel.getProductsList(this@ProductsFragment)
        }
    }

    override fun onResume() {
        super.onResume()
        GlobalScope.launch(Dispatchers.IO) {
            getProductListFromFireStore()
        }
    }

    suspend fun successProductsListFromFireStore(productsList: ArrayList<Product>) {
        GlobalScope.launch(Dispatchers.Main) {
        hideProgressDialog()
        if (productsList.size > 0) {
            showRecyclerView()
            recyclerView(productsList)


        } else {
            hideRecyclerView()
        }
        }
    }

    private suspend fun recyclerView(productsList: ArrayList<Product>){
        GlobalScope.launch(Dispatchers.Main) {
            binding.rvMyProductItems.layoutManager = LinearLayoutManager(activity)
            binding.rvMyProductItems.setHasFixedSize(true)
            val adapterProducts = MyProductListAdapter(requireActivity(), productsList, this@ProductsFragment)
            binding.rvMyProductItems.adapter = adapterProducts
        }
    }

    private suspend fun showRecyclerView(){
        withContext(Dispatchers.Main) {
            binding.rvMyProductItems.visibility = View.VISIBLE
            binding.tvNoProductsFound.visibility = View.GONE
        }
    }

    private suspend fun hideRecyclerView(){
        withContext(Dispatchers.Main) {
            binding.rvMyProductItems.visibility = View.GONE
            binding.tvNoProductsFound.visibility = View.VISIBLE
        }
    }

    private  fun setUpView() {
        GlobalScope.launch(Dispatchers.Main) {
            binding.actionSettingsDesigner.setOnClickListener {
                startActivity(Intent(activity, Settings_Designers_Activity::class.java))
            }
            binding.actionAddProduct.setOnClickListener {
                startActivity(Intent(activity, AddproductActivity::class.java))
            }
        }
    }

}