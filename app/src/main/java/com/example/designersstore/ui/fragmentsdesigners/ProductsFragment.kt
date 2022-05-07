package com.example.designersstore.ui.fragmentsdesigners

import android.content.Intent
import android.os.Bundle
import android.view.*
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.designersstore.R
import com.example.designersstore.ui.activityclient.SettingsActivity
import com.example.designersstore.ui.activitydesigners.Settings_Designers_Activity


class ProductsFragment : Fragment() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    //If we want to see the option menu in fragment we need to add it.
    setHasOptionsMenu(true)
  }
//  private lateinit var dashboardViewModel: DashboardViewModel

  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View? {
//    dashboardViewModel =
//            ViewModelProvider(this).get(DashboardViewModel::class.java)
    val root = inflater.inflate(R.layout.products_fragment, container, false)
    val textView: TextView = root.findViewById(R.id.text_products)
//    dashboardViewModel.text.observe(viewLifecycleOwner, Observer {
//      textView.text = it
//    })
    return root
  }

  override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
    inflater.inflate(R.menu.client_settings,menu)
    super.onCreateOptionsMenu(menu, inflater)
  }

  override fun onOptionsItemSelected(item: MenuItem): Boolean {
    val id =item.itemId

    when(id){
      R.id.action_settings->{
        //TODO Step 9:Launch the SettingActivity on Click of action item.
        startActivity(Intent(activity, Settings_Designers_Activity::class.java))
        return true
      }
    }
    return super.onOptionsItemSelected(item)
  }

}