package com.example.designersstore.presentation.ui.fragmentsclient

import android.content.Intent
import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.example.designersstore.R
import com.example.designersstore.data.userfirestore.UserFirestoreViewModel
import com.example.designersstore.databinding.FragmentDashboardBinding
import com.example.designersstore.databinding.FragmentSearchBinding
import com.example.designersstore.domain.models.Product
import com.example.designersstore.presentation.ui.activityclient.cartlist.CartListActivity
import com.example.designersstore.presentation.ui.activityclient.settingclient.SettingsActivity
import com.example.designersstore.presentation.ui.adapters.DashboardItemsListAdapter
import com.example.designersstore.presentation.ui.fragmentsclient.searchfilter.SearchFragment
import kotlinx.android.synthetic.main.fragment_dashboard.*
import kotlinx.android.synthetic.main.products_fragment.*
import kotlinx.coroutines.*

class DashboardFragment : BaseFragment() {

    private val viewModel:UserFirestoreViewModel by viewModels()
    private lateinit var binding:FragmentDashboardBinding
    var productList:ArrayList<Product> = ArrayList()
    val adapter: DashboardItemsListAdapter by lazy{
        DashboardItemsListAdapter(requireActivity(),productList)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setHasOptionsMenu(true)
    }

    override fun onResume() {
        super.onResume()
        getDashboardItemsList()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentDashboardBinding.inflate(layoutInflater)
        setUpView()
        actionSearch()
        GlobalScope.launch(Dispatchers.IO) {
            refresh()
        }
        return binding.root
    }

    private suspend fun refresh(){
        GlobalScope.launch(Dispatchers.IO) {
            binding.swipeToRefresh.setOnRefreshListener {
                getDashboardItemsList()
                Toast.makeText(requireContext(), "page refreshed!", Toast.LENGTH_SHORT).show()
                binding.swipeToRefresh.isRefreshing = false
            }
        }
    }

    private fun setUpView(){
        GlobalScope.launch(Dispatchers.Main) {
            binding.actionCart.setOnClickListener {
                startActivity(Intent(activity, CartListActivity::class.java))
            }
        }
    }
    private fun actionSearch(){
        GlobalScope.launch(Dispatchers.Main) {
            binding.actionSearch.setOnClickListener {
                val direction =
                    DashboardFragmentDirections.actionNavigationDashboardToSearchFragment()
                findNavController().navigate(direction)
            }
        }
    }

    suspend fun successDashboardItemsList(dashboardItemsList: ArrayList<Product>){
        hideProgressDialog()
        if(dashboardItemsList . size > 0){
            rv_dashboard_items.visibility=View.VISIBLE
            tv_no_dashboard_items_found.visibility=View.GONE
            imageView3.visibility=View.GONE

            recyclerView(dashboardItemsList)
        }else{
            rv_dashboard_items.visibility=View.GONE
            tv_no_dashboard_items_found.visibility=View.VISIBLE
        }
    }

     suspend fun recyclerView(dashboardItemsList: ArrayList<Product>){
        GlobalScope.launch(Dispatchers.Main) {
            rv_dashboard_items.setHasFixedSize(true)
           // val adapter = DashboardItemsListAdapter(requireActivity(), dashboardItemsList)
            rv_dashboard_items.adapter = adapter
            rv_dashboard_items.layoutManager =
                StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
        }
    }

     fun getDashboardItemsList(){
         GlobalScope.launch(Dispatchers.IO) {
             withContext(Dispatchers.Main){ showProgressDialog(resources.getString(R.string.please_wait))}
             viewModel.getDashboardItemsList(this@DashboardFragment)
         }
    }
}