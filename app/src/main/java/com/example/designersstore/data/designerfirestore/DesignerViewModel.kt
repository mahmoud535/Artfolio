package com.example.designersstore.data.designerfirestore

import android.app.Activity
import android.app.Application
import androidx.lifecycle.*
import com.example.designersstore.data.repositoryimpdesigner.DesignerRepositoryImp
import com.example.designersstore.domain.models.*
import com.example.designersstore.presentation.ui.activitydesigners.AddEditAddressDesigners
import com.example.designersstore.presentation.ui.activitydesigners.addressdesigner.AddressList_Designer
import com.example.designersstore.presentation.ui.activitydesigners.Register_Designers_Activity
import com.example.designersstore.presentation.ui.fragmentsdesigners.SoldProductsFragment
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch

class DesignerViewModel (application: Application) :AndroidViewModel(application){

    private var DesignerRepositoryImp: DesignerRepositoryImp

    private var registerDesignerMutableLiveData= MutableLiveData<List<Designer>>()
    val registerDesignerLiveData: MutableLiveData<List<Designer>> get() = registerDesignerMutableLiveData

    private var currentDesignerIDMutableLiveData= MutableLiveData<List<Designer>>()
     val designerIdLiveData: MutableLiveData<List<Designer>> get() = currentDesignerIDMutableLiveData

    private var designersDetailsMutableLiveData= MutableLiveData<List<Designer>>()
    val designersDetailsLiveData: MutableLiveData<List<Designer>> get() = designersDetailsMutableLiveData

    private var soldProductsListMutableLiveData= MutableLiveData<List<SoldProduct>>()
    val soldProductsListLiveData: MutableLiveData<List<SoldProduct>> get() = soldProductsListMutableLiveData

    private var allProductListMutableLiveData= MutableLiveData<List<Product>>()
    val allProductListLiveData: MutableLiveData<List<Product>> get() = allProductListMutableLiveData

    private var updateDesignerProfileDataMutableLiveData= MutableLiveData<List<AddressClient>>()
    val updateDesignerProfileDataLiveData: MutableLiveData<List<AddressClient>> get() = updateDesignerProfileDataMutableLiveData

    private var addressesListDesignersMutableLiveData= MutableLiveData<List<AddressClient>>()
    val addressesListDesignersLiveData: MutableLiveData<List<AddressClient>> get() = addressesListDesignersMutableLiveData

    private var deleteAddressDesignerMutableLiveData= MutableLiveData<List<AddressClient>>()
    val deleteAddressDesignerLiveData: MutableLiveData<List<AddressClient>> get() = deleteAddressDesignerMutableLiveData

    private var updateAddressDesignerMutableLiveData= MutableLiveData<List<AddressClient>>()
    val updateAddressDesignerLiveData: MutableLiveData<List<AddressClient>> get() = updateAddressDesignerMutableLiveData

    private var addAddressDesignerMutableLiveData= MutableLiveData<List<AddressClient>>()
    val addAddressDesignerLiveData: MutableLiveData<List<AddressClient>> get() = addAddressDesignerMutableLiveData

        init {
        var firestore = FirebaseFirestore.getInstance()

            DesignerRepositoryImp = DesignerRepositoryImp(firestore)
    }
    fun getCurrentDesignerID() = viewModelScope.launch {
     //   currentDesignerIDMutableLiveData.postValue(DesignerRepositoryImp.getCurrentDesignerID())
        DesignerRepositoryImp.getCurrentDesignerID()
    }
    fun registerDesigner(activity: Register_Designers_Activity, designerInfo: Designer) = viewModelScope.launch {
     //   registerDesignerMutableLiveData.postValue(DesignerRepositoryImp.registerDesigner(activity,designerInfo))
        DesignerRepositoryImp.registerDesigner(activity,designerInfo)
    }

    fun getDesignersDetails(activity: Activity) =viewModelScope.launch {
       // designersDetailsMutableLiveData.postValue(DesignerRepositoryImp.getDesignersDetails(activity))
        DesignerRepositoryImp.getDesignersDetails(activity)
    }
    fun getSoldProductsList(fragment: SoldProductsFragment) =viewModelScope.launch {
      //  soldProductsListMutableLiveData.postValue( DesignerRepositoryImp.getSoldProductsList(fragment))
        DesignerRepositoryImp.getSoldProductsList(fragment)
    }

    fun getAllProductList(activity: Activity)=viewModelScope.launch {
        allProductListMutableLiveData.postValue(DesignerRepositoryImp.getAllProductList(activity))
        //DesignerRepositoryImp.getAllProductList(activity)
    }


    fun updateDesignerProfileData(  activity: Activity,
                                    designerHashMap: HashMap<String, Any>) = viewModelScope.launch {
       // updateDesignerProfileDataMutableLiveData.postValue( DesignerRepositoryImp.updateDesignerProfileData(activity,designerHashMap))
        DesignerRepositoryImp.updateDesignerProfileData(activity,designerHashMap)
    }

    fun getAddressesListDesigners(activity: AddressList_Designer) = viewModelScope.launch {
       // addressesListDesignersMutableLiveData.postValue(DesignerRepositoryImp.getAddressesListDesigners(activity))
        DesignerRepositoryImp.getAddressesListDesigners(activity)
    }

    fun deleteAddressDesigner(activity: AddressList_Designer, addressId: String) = viewModelScope.launch {
      // deleteAddressDesignerMutableLiveData.postValue(DesignerRepositoryImp.deleteAddressDesigner(activity,addressId))
        DesignerRepositoryImp.deleteAddressDesigner(activity,addressId)
    }
    fun updateAddressDesigner(activity: AddEditAddressDesigners,
                              addressInfo: AddressClient,
                              addressId: String) = viewModelScope.launch {
       // updateAddressDesignerMutableLiveData.postValue(DesignerRepositoryImp.updateAddressDesigner(activity,addressInfo,addressId))
        DesignerRepositoryImp.updateAddressDesigner(activity,addressInfo,addressId)
    }
    fun deleteSoldProduct( orderId: String, fragment:SoldProductsFragment)= viewModelScope.launch {
        DesignerRepositoryImp.deleteSoldProduct(orderId, fragment)
    }

    fun addAddressDesigner(activity: AddEditAddressDesigners,
                           addressInfo: AddressClient) = viewModelScope.launch {
       // addAddressDesignerMutableLiveData.postValue(DesignerRepositoryImp.addAddressDesigner(activity,addressInfo))
        DesignerRepositoryImp.addAddressDesigner(activity,addressInfo)
    }
}




