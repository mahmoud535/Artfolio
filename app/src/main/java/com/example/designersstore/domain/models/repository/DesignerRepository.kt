package com.example.designersstore.domain.models.repository

import android.app.Activity
import com.example.designersstore.domain.models.AddressClient
import com.example.designersstore.domain.models.Designer
import com.example.designersstore.domain.models.Product
import com.example.designersstore.presentation.ui.activitydesigners.AddEditAddressDesigners
import com.example.designersstore.presentation.ui.activitydesigners.addressdesigner.AddressList_Designer
import com.example.designersstore.presentation.ui.activitydesigners.Register_Designers_Activity
import com.example.designersstore.presentation.ui.fragmentsdesigners.SoldProductsFragment

interface DesignerRepository {


    suspend fun registerDesigner(activity: Register_Designers_Activity, designerInfo: Designer)

    suspend fun getCurrentDesignerID(): String

    suspend fun getDesignersDetails(activity: Activity)

    suspend fun getSoldProductsList(fragment: SoldProductsFragment)

    suspend fun getAllProductList(activity: Activity):List<Product>

    suspend fun updateDesignerProfileData(activity: Activity, designerHashMap: HashMap<String, Any>)

    suspend fun getAddressesListDesigners(activity: AddressList_Designer)

    suspend fun deleteAddressDesigner(activity: AddressList_Designer, addressId: String)

    suspend fun updateAddressDesigner(
        activity: AddEditAddressDesigners,
        addressInfo: AddressClient,
        addressId: String
    )

    suspend fun addAddressDesigner(
        activity: AddEditAddressDesigners,
        addressInfo: AddressClient
    )
    suspend fun deleteSoldProduct( orderId: String, fragment:SoldProductsFragment)
}