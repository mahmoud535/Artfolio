package com.example.designersstore.presentation.ui.fragmentsclient

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.viewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.designersstore.R
import com.example.designersstore.data.userfirestore.UserFirestoreViewModel
import com.example.designersstore.databinding.FragmentAccountBinding
import com.example.designersstore.domain.models.User
import com.example.designersstore.presentation.ui.activityclient.addresslistclient.AddressList_Client
import com.example.designersstore.presentation.ui.activityclient.clientprofile.Client_Profile_Activity
import com.example.designersstore.presentation.ui.activityclient.settingclient.SettingsActivity
import com.example.designersstore.presentation.utils.Constants
import com.example.designersstore.presentation.utils.GlideLoader
import kotlinx.android.synthetic.main.fragment_account.*


class AccountFragment : BaseFragment() {
    private lateinit  var mUserDetails: User
    private lateinit var binding: FragmentAccountBinding
    private val viewModel: UserFirestoreViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAccountBinding.inflate(layoutInflater)
        setUpView()
        return binding.root
    }
    private fun getUserDetails(){
        viewModel.getAccountDetails(this)
    }

    fun userDetails(user: User) {
        mUserDetails = user
        GlideLoader(requireContext()).loadUserPicture(user.image, binding.userProfileImage)
        binding.tvName.text = "${user.firstName} ${user.lastName} "
        binding.tvPhone.text = "${user.mobile}"
    }

    override fun onResume() {
        super.onResume()
        getUserDetails()
    }

    private fun setUpView() {
        actionPersonalInfo()
        switchLanguage()
        getOrderHistory()
        goToFavoriteOrder()
        gotoAddress()
        gotoSetting()
    }

    private fun actionPersonalInfo(){
        binding.PersonalInformation.setOnClickListener {
            val intent = Intent(requireContext(), Client_Profile_Activity::class.java)
            intent.putExtra(Constants.EXTRA_USER_DETAILS, mUserDetails)
            startActivity(intent)
        }
    }

    private fun switchLanguage(){
        binding.SwitchLanguage.setOnClickListener {
            val direction = AccountFragmentDirections.actionNavigationAccountToLanguageFragment()
            findNavController().navigate(direction)
        }
    }

    private fun getOrderHistory(){
        binding.orderHistory.setOnClickListener {
            val direction = AccountFragmentDirections.actionNavigationAccountToNavigationOrders()
            findNavController().navigate(direction)
        }
    }

    private fun goToFavoriteOrder(){
        binding.FavoriteOrder.setOnClickListener {
            val direction = AccountFragmentDirections.actionNavigationAccountToFavoriteListFragment()
            findNavController().navigate(direction)
        }
    }

    private fun gotoAddress(){
        binding.AddressBook.setOnClickListener {
            startActivity(Intent(activity,AddressList_Client::class.java))
        }
    }

    private fun gotoSetting(){
        binding.AccountSetting.setOnClickListener {
            startActivity(Intent(activity, SettingsActivity::class.java))
        }
    }
}