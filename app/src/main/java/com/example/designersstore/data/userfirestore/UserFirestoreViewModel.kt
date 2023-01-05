package com.example.designersstore.data.userfirestore

import android.app.Activity
import android.content.Context
import android.net.Uri
import androidx.fragment.app.Fragment
import androidx.lifecycle.*
import com.example.designersstore.data.repositoryimp.UserFirestoreRepositoryImp
import com.example.designersstore.domain.models.*
import com.example.designersstore.presentation.ui.activityclient.addeditaddressactivity.AddEditAddress
import com.example.designersstore.presentation.ui.activityclient.addresslistclient.AddressList_Client
import com.example.designersstore.presentation.ui.activityclient.checkout.CheckoutActivity
import com.example.designersstore.presentation.ui.activityclient.registerclient.Register_Client_Activity
import com.example.designersstore.presentation.ui.activitydesigners.AddproductActivity
import com.example.designersstore.presentation.ui.activityclient.ProductDetailsActivity
import com.example.designersstore.presentation.ui.fragmentsclient.AccountFragment
import com.example.designersstore.presentation.ui.fragmentsclient.DashboardFragment
import com.example.designersstore.presentation.ui.fragmentsclient.OrdersFragment
import com.example.designersstore.presentation.ui.fragmentsdesigners.ProductsFragment
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch


class UserFirestoreViewModel() : ViewModel() {

    private var userFirestoreRepositoryImp: UserFirestoreRepositoryImp


    init {
        var firestore = FirebaseFirestore.getInstance()

        userFirestoreRepositoryImp = UserFirestoreRepositoryImp(firestore)
    }

    private val cartListMutableLiveData=MutableLiveData<List<CartItem>>()//Task<QuerySnapshot>
    val cartListLiveData: MutableLiveData<List<CartItem>> get() = cartListMutableLiveData

    fun registerUser(activity: Register_Client_Activity, userInfo: User) = viewModelScope.launch {
        userFirestoreRepositoryImp.registerUser(activity, userInfo)
    }

    fun getCurrentUserID() = viewModelScope.launch {
        userFirestoreRepositoryImp.getCurrentUserID()
    }

    fun getUserDetails(activity: Activity) = viewModelScope.launch {
        userFirestoreRepositoryImp.getUserDetails(activity)
    }

    fun getAccountDetails(fragment: AccountFragment) = viewModelScope.launch {
        userFirestoreRepositoryImp.getAccountDetails(fragment)
    }

    fun uploadImageToCloudStorage(
        activity: Activity,
        imageFileURI: Uri?,
        imageType: String
    ) = viewModelScope.launch {
        userFirestoreRepositoryImp.uploadImageToCloudStorage(activity, imageFileURI, imageType)
    }

    fun getProductsList(fragment: Fragment) = viewModelScope.launch {
        userFirestoreRepositoryImp.getProductsList(fragment)
    }

    fun updateAllDetails(
        activity: CheckoutActivity,
        cartList: ArrayList<CartItem>,
        order: Order
    ) = viewModelScope.launch {
        userFirestoreRepositoryImp.updateAllDetails(activity, cartList, order)
    }

    fun getMyOrdersList(fragment: OrdersFragment) = viewModelScope.launch {
        userFirestoreRepositoryImp.getMyOrdersList(fragment)
    }

    fun placeOrder(activity: CheckoutActivity, order: Order) = viewModelScope.launch {
        userFirestoreRepositoryImp.placeOrder(activity, order)
    }

    fun deleteProduct(fragment: ProductsFragment, productId: String) = viewModelScope.launch {
        userFirestoreRepositoryImp.deleteProduct(fragment, productId)
    }

    fun getProductDetails(activity: ProductDetailsActivity, productId: String) =
        viewModelScope.launch {
            userFirestoreRepositoryImp.getProductDetails(activity, productId)
        }

    fun addCartItems(activity: ProductDetailsActivity, addToCart: CartItem) =
        viewModelScope.launch {
            userFirestoreRepositoryImp.addCartItems(activity, addToCart)
        }

    fun checkIfItemExistInCart(activity: ProductDetailsActivity, ProductId: String) =
        viewModelScope.launch {
            userFirestoreRepositoryImp.checkIfItemExistInCart(activity, ProductId)
        }

    fun checkIfFavoriteItemExitInCat(activity: ProductDetailsActivity, ProductId: String){
        viewModelScope.launch {
            userFirestoreRepositoryImp.checkIfFavoriteItemExistInCart(activity, ProductId)
        }
    }
    //todo: ****************
    fun removeItemFromCart(context: Context, cart_id: String) = viewModelScope.launch {
        userFirestoreRepositoryImp.removeItemFromCart(context, cart_id)
    }

    fun getCartList(activity: Activity) = viewModelScope.launch {
        cartListMutableLiveData.postValue(userFirestoreRepositoryImp.getCartList(activity) )
        //userFirestoreRepositoryImp.getCartList(activity)
    }

    //todo: ****************
    fun updateMyCart(
        context: Context,
        cart_id: String,
        itemHashMap: HashMap<String, Any>
    ) = viewModelScope.launch {
        userFirestoreRepositoryImp.updateMyCart(context, cart_id, itemHashMap)
    }

    fun uploadProductDetails(activity: AddproductActivity, productInfo: Product) =
        viewModelScope.launch {
            userFirestoreRepositoryImp.uploadProductDetails(activity, productInfo)
        }

    fun updateUserProfileData(activity: Activity, userHashMap: HashMap<String, Any>) =
        viewModelScope.launch {
            userFirestoreRepositoryImp.updateUserProfileData(activity, userHashMap)
        }

    fun getAddressesListClient(activity: AddressList_Client) =
        viewModelScope.launch {
            userFirestoreRepositoryImp.getAddressesListClient(activity)
        }

    fun deleteAddress(activity: AddressList_Client, addressId: String) =
        viewModelScope.launch {
            userFirestoreRepositoryImp.deleteAddress(activity, addressId)
        }

    fun deleteOrder(fragment: OrdersFragment, orderId: String)=
        viewModelScope.launch {
            userFirestoreRepositoryImp.deleteOrder(fragment,orderId)
        }

    fun updateAddressClient(
        activity: AddEditAddress,
        addressInfo: AddressClient,
        addressId: String
    ) = viewModelScope.launch {
        userFirestoreRepositoryImp.updateAddressClient(activity, addressInfo, addressId)
    }

    fun addAddressClient(activity: AddEditAddress, addressInfo: AddressClient) =
        viewModelScope.launch {
            userFirestoreRepositoryImp.addAddressClient(activity, addressInfo)
        }

    fun getDashboardItemsList(fragment: DashboardFragment) = viewModelScope.launch {
        userFirestoreRepositoryImp.getDashboardItemsList(fragment)
    }


}