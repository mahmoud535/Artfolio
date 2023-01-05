package com.example.designersstore.data.repositoryimp

import android.app.Activity
import com.example.designersstore.domain.models.AddressClient
import com.example.designersstore.domain.models.repository.AddEditAddressRepository
import com.example.designersstore.presentation.utils.Constants

class AddEditAddressImp(): AddEditAddressRepository {

    private var mAddressDetails: AddressClient? = null
    override suspend fun parcelableAddress(activity:Activity) {
        if (activity.intent.hasExtra(Constants.EXTRA_ADDRESS_DETAILS_CLIENT)) {
            mAddressDetails =
                activity. intent.getParcelableExtra(Constants.EXTRA_ADDRESS_DETAILS_CLIENT)!!
        }
    }
}