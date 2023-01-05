package com.example.designersstore.domain.models.repository

import android.app.Activity
import android.content.Context
import android.net.Uri
import androidx.fragment.app.Fragment
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

interface UserFirestoreRepository {

    /**
     * A function to make an entry of the registered user in the FireStore database.
     */
    suspend fun registerUser(activity: Register_Client_Activity, userInfo: User)

    suspend fun getCurrentUserID(): String

    suspend fun getUserDetails(activity: Activity)

    suspend fun uploadImageToCloudStorage(activity:Activity, imageFileURI: Uri?, imageType:String)

    suspend fun getProductsList(fragment: Fragment)

    suspend fun updateAllDetails(activity: CheckoutActivity, cartList: ArrayList<CartItem>, order: Order)

    suspend fun getMyOrdersList(fragment: OrdersFragment)

    suspend fun placeOrder(activity: CheckoutActivity, order: Order)

    suspend fun deleteProduct(fragment: ProductsFragment, productId:String)

    suspend fun getProductDetails(activity: ProductDetailsActivity, productId: String)

    suspend fun addCartItems(activity: ProductDetailsActivity, addToCart: CartItem)

    suspend fun checkIfItemExistInCart(activity: ProductDetailsActivity, ProductId:String)

    suspend fun removeItemFromCart(context: Context, cart_id: String)

    suspend fun getCartList(activity: Activity) : List<CartItem>

    suspend fun updateMyCart(context: Context,cart_id: String,itemHashMap:HashMap<String,Any>)

    suspend fun uploadProductDetails(activity: AddproductActivity, productInfo: Product)

    suspend fun updateUserProfileData(activity: Activity,userHashMap: HashMap<String,Any>)

    suspend fun getAddressesListClient(activity: AddressList_Client)

    suspend fun deleteAddress (activity: AddressList_Client, addressId:String)

    suspend fun updateAddressClient(
        activity: AddEditAddress,
        addressInfo: AddressClient,
        addressId: String
    )

    suspend fun addAddressClient(activity: AddEditAddress, addressInfo: AddressClient)

    suspend fun getDashboardItemsList(fragment: DashboardFragment)

    suspend fun getAccountDetails(fragment: AccountFragment)

    suspend fun deleteOrder(fragent: OrdersFragment, addressId: String)

    suspend fun checkIfFavoriteItemExistInCart(
        activity: ProductDetailsActivity,
        ProductId: String
    )


}