package com.example.designersstore.presentation.ui.fragmentsclient.searchfilter

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.example.designersstore.R
import com.example.designersstore.data.repositoryimp.SearchImp
import com.example.designersstore.data.userfirestore.UserFirestoreViewModel
import com.example.designersstore.databinding.FragmentSearchBinding
import com.example.designersstore.domain.models.Product
import com.example.designersstore.presentation.ui.activityclient.cartlist.CartListActivity
import com.example.designersstore.presentation.ui.adapters.DashboardItemsListAdapter
import com.example.designersstore.presentation.ui.fragmentsclient.BaseFragment
import com.example.designersstore.presentation.ui.fragmentsclient.DashboardFragment
import com.example.designersstore.presentation.ui.fragmentsclient.DashboardFragmentDirections
import kotlinx.android.synthetic.main.fragment_dashboard.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch


class SearchFragment : BaseFragment() {
    var productList: ArrayList<Product> = ArrayList()
    val adapter: SearchItemsListAdapter by lazy {
        SearchItemsListAdapter(requireActivity(), productList)
    }
    lateinit var binding: FragmentSearchBinding
    private val viewModel: SearchViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSearchBinding.inflate(layoutInflater)
       binding.searchView.setOnClickListener {
           viewModel.filter(SearchFragment())
       }
        return binding.root
    }

    override fun onResume() {
        super.onResume()
        getDashboardItemsList()
    }

    fun successDashboardItemsList(dashboardItemsList: ArrayList<Product>) {
        GlobalScope.launch(Dispatchers.Main) {
            hideProgressDialog()
            if (dashboardItemsList.size > 0) {
                binding.rvDashboardItems.visibility = View.VISIBLE
                binding.tvNoDashboardItemsFound.visibility = View.GONE
                binding.imageView3.visibility = View.GONE

                recyclerView()
            } else {
                binding.rvDashboardItems.visibility = View.GONE
                binding.tvNoDashboardItemsFound.visibility = View.VISIBLE
            }
        }
    }

    fun recyclerView() {
        GlobalScope.launch(Dispatchers.Main) {
            binding.rvDashboardItems.setHasFixedSize(true)
            // val adapter = DashboardItemsListAdapter(requireActivity(), dashboardItemsList)
            binding.rvDashboardItems.adapter = adapter
            binding.rvDashboardItems.layoutManager =
                StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)

        }
    }

    private fun getDashboardItemsList() {
        showProgressDialog(resources.getString(R.string.please_wait))
        viewModel.getSearchItemsList(this@SearchFragment)
    }

    fun filter(): Boolean {
        // getting search view of our item.
        val searchView: android.widget.SearchView = binding.searchView as android.widget.SearchView

        // below line is to call set on query text listener method.
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener,
            android.widget.SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(p0: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(msg: String): Boolean {
                // inside on query text change method we are
                // calling a method to filter our recycler view.

                return false
            }
        })
        return true

    }

    private fun filter(text: String) {
        // creating a new array list to filter our data.
        val filteredlist: ArrayList<Product> = ArrayList()

        // running a for loop to compare elements.
        for (item in productList) {
            // checking if the entered string matched with any item of our recycler view.
            if (item.title.toLowerCase().contains(text.toLowerCase())) {
                // if the item is matched we are
                // adding it to our filtered list.
                filteredlist.add(item)
            }
        }
        if (filteredlist.isEmpty()) {
            // if no item is added in filtered list we are
            // displaying a toast message as no data found.
            Toast.makeText(requireContext(), "No Data Found..", Toast.LENGTH_SHORT).show()
        } else {
            // at last we are passing that filtered
            // list to our adapter class.
            adapter.setList(filteredlist)
        }
    }

}