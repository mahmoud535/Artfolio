package com.example.designersstore.data.repositoryimp

import android.app.Activity
import android.content.Context
import android.content.SharedPreferences
import android.net.Uri
import android.util.Log
import androidx.fragment.app.Fragment
import com.example.designersstore.domain.models.repository.UserFirestoreRepository
import com.example.designersstore.domain.models.*
import com.example.designersstore.presentation.ui.activityclient.addeditaddressactivity.AddEditAddress
import com.example.designersstore.presentation.ui.activityclient.addresslistclient.AddressList_Client
import com.example.designersstore.presentation.ui.activityclient.cartlist.CartListActivity
import com.example.designersstore.presentation.ui.activityclient.checkout.CheckoutActivity
import com.example.designersstore.presentation.ui.activityclient.clientprofile.Client_Profile_Activity
import com.example.designersstore.presentation.ui.activityclient.loginclient.Login_Client_Activity
import com.example.designersstore.presentation.ui.activityclient.registerclient.Register_Client_Activity
import com.example.designersstore.presentation.ui.activityclient.settingclient.SettingsActivity
import com.example.designersstore.presentation.ui.activitydesigners.AddproductActivity
import com.example.designersstore.presentation.ui.activitydesigners.Designers_Profile_Activity
import com.example.designersstore.presentation.ui.activityclient.ProductDetailsActivity
import com.example.designersstore.presentation.ui.fragmentsclient.AccountFragment
import com.example.designersstore.presentation.ui.fragmentsclient.DashboardFragment
import com.example.designersstore.presentation.ui.fragmentsclient.OrdersFragment
import com.example.designersstore.presentation.ui.fragmentsdesigners.ProductsFragment
import com.example.designersstore.presentation.utils.Constants
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class UserFirestoreRepositoryImp(private val mFireStore: FirebaseFirestore = FirebaseFirestore.getInstance()) :
    UserFirestoreRepository {
    override suspend fun registerUser(activity: Register_Client_Activity, userInfo: User) {
        withContext(Dispatchers.IO) {
            mFireStore.collection(Constants.USERS)
                .document(userInfo.id)
                .set(userInfo, SetOptions.merge())
                .addOnSuccessListener {
                    activity.userRegistrationSuccess()
                }
                .addOnFailureListener { e ->
                    activity.hideProgressDialog()
                    Log.e(
                        activity.javaClass.simpleName,
                        "Error while registering the user.",
                        e
                    )
                }
        }
    }

    override suspend fun getCurrentUserID(): String =
        withContext(Dispatchers.IO) {
            val currentUser = FirebaseAuth.getInstance().currentUser
            var currentUserID = ""
            if (currentUser != null) {
                currentUserID = currentUser.uid
            }
            return@withContext currentUserID
        }


    override suspend fun getUserDetails(activity: Activity) {
        withContext(Dispatchers.IO) {
            mFireStore.collection(Constants.USERS)
                .document(getCurrentUserID())
                .get()
                .addOnSuccessListener { document ->
                    Log.i(activity.javaClass.simpleName, document.toString())
                    val user = document.toObject(User::class.java)!!
                    val sharedpreferences =
                        activity.getSharedPreferences(
                            Constants.DESIGNERSSTORE_PREFERENCES,
                            Context.MODE_PRIVATE
                        )
                    val editor: SharedPreferences.Editor = sharedpreferences.edit()
                    editor.putString(
                        Constants.LOGGED_IN_USERNAME,
                        "${user.firstName} ${user.lastName}"
                    )
                    editor.apply()

                    when (activity) {
                        is Login_Client_Activity -> {
                            activity.userLoggedInSuccess(user)
                        }
                        is SettingsActivity -> {
                            activity.userDetailsSuccess(user)
                        }
                    }

                }
                .addOnFailureListener { e ->
                    when (activity) {
                        is Login_Client_Activity -> {
                            activity.hideProgressDialog()
                        }
                        is SettingsActivity -> {
                            activity.hideProgressDialog()
                        }

                    }
                    Log.e(
                        activity.javaClass.simpleName,
                        "Error while getting user details.",
                        e
                    )
                }
        }
    }

    override suspend fun getAccountDetails(fragment: AccountFragment) {
        withContext(Dispatchers.IO) {
            mFireStore.collection(Constants.USERS)
                .document(getCurrentUserID())
                .get()
                .addOnSuccessListener { document ->
                    val user = document.toObject(User::class.java)!!
                    when (fragment) {
                        is AccountFragment -> {
                            fragment.userDetails(user)
                        }
                    }

                }
        }
    }

    override suspend fun uploadImageToCloudStorage(
        activity: Activity,
        imageFileURI: Uri?,
        imageType: String
    ) {
        withContext(Dispatchers.IO) {
            val sRef: StorageReference = FirebaseStorage.getInstance().reference.child(
                imageType + System.currentTimeMillis() + "."
                        + Constants.getFileExtension(
                    activity,
                    imageFileURI
                )
            )
            sRef.putFile(imageFileURI!!)
                .addOnSuccessListener { taskSnapshot ->
                    Log.e(
                        "Firebase Image URL",
                        taskSnapshot.metadata!!.reference!!.downloadUrl.toString()
                    )
                    taskSnapshot.metadata!!.reference!!.downloadUrl
                        .addOnSuccessListener { uri ->
                            Log.e("Downloadable Image URL", uri.toString())
                            when (activity) {
                                is Client_Profile_Activity -> {
                                    activity.imageUploadSuccess(uri.toString())
                                }
                                is Designers_Profile_Activity -> {
                                    activity.imageUploadSuccess(uri.toString())
                                }
                                is AddproductActivity -> {
                                    activity.imageUploadSuccess(uri.toString())
                                }
                            }
                        }
                        .addOnFailureListener { exception ->
                            when (activity) {
                                is Client_Profile_Activity -> {
                                    activity.hideProgressDialog()
                                }
                                is Designers_Profile_Activity -> {
                                    activity.hideProgressDialog()
                                }
                                is AddproductActivity -> {
                                    activity.hideProgressDialog()
                                }

                            }
                            Log.e(
                                activity.javaClass.simpleName,
                                exception.message,
                                exception
                            )
                        }
                }
        }
    }

    override suspend fun getProductsList(fragment: Fragment) {
        withContext(Dispatchers.IO) {
            mFireStore.collection(Constants.PRODUCTS)
                .whereEqualTo(Constants.USER_ID, getCurrentUserID())
                .get()
                .addOnSuccessListener { document ->
                    Log.e("Products List", document.documents.toString())
                    val productsList: ArrayList<Product> = ArrayList()
                    for (i in document.documents) {
                        val product = i.toObject(Product::class.java)
                        product!!.product_id = i.id
                        productsList.add(product)
                    }
                    when (fragment) {
                        is ProductsFragment -> {
                            GlobalScope.launch(Dispatchers.Main) {  fragment.successProductsListFromFireStore(productsList) }

                        }
                    }
                }
        }
    }

    override suspend fun updateAllDetails(
        activity: CheckoutActivity,
        cartList: ArrayList<CartItem>,
        order: Order
    ) {
        withContext(Dispatchers.IO) {
            val writeBatch = mFireStore.batch()
            for (cart in cartList) {
                //val productHashMap = HashMap<String, Any>()
//             productHashMap[Constants.STOCK_QUANTITY] =
//                     (cart.stock_quantity.toInt() - cart.cart_quantity.toInt()).toString()
                val soldProduct = SoldProduct(
                    cart.product_owner_id,
                    cart.title,
                    cart.price,
                    cart.cart_quantity,
                    cart.image,
                    order.title,
                    order.order_datetime,
                    order.sub_total_amount,
                    order.shipping_charge,
                    order.total_amount,
                    order.address
                )

                val documentReference = mFireStore.collection(Constants.SOLD_PRODUCTS)
                    .document(cart.product_id)
                writeBatch.set(documentReference, soldProduct)
            }
            for (cart in cartList) {
                val documentReference = mFireStore.collection(Constants.CART_ITEMS)
                    .document(cart.id)
                writeBatch.delete(documentReference)
            }
            writeBatch.commit().addOnSuccessListener {
                activity.allDetailsUpdatedSuccessfully()
            }.addOnFailureListener { e ->
                activity.hideProgressDialog()
                Log.e(
                    activity.javaClass.simpleName,
                    "Error while updating all the details after order placed.",
                    e
                )
            }
        }
    }

    override suspend fun getMyOrdersList(fragment: OrdersFragment) {
        withContext(Dispatchers.IO) {
            mFireStore.collection(Constants.ORDERS)
                .whereEqualTo(Constants.USER_ID, getCurrentUserID())
                .get() // Will get the documents snapshots.
                .addOnSuccessListener { document ->
                    Log.e(fragment.javaClass.simpleName, document.documents.toString())
                    val list: ArrayList<Order> = ArrayList()
                    for (i in document.documents) {
                        val orderItem = i.toObject(Order::class.java)!!
                        orderItem.id = i.id
                        list.add(orderItem)
                    }
                    GlobalScope.launch(Dispatchers.IO) {
                        fragment.populateOrdersListInUI(list)
                    }
                }
                .addOnFailureListener { e ->
                    // Here call a function of base activity for transferring the result to it.
                    fragment.hideProgressDialog()
                    Log.e(fragment.javaClass.simpleName, "Error while getting the orders list.", e)
                }
        }
    }

    override suspend fun placeOrder(activity: CheckoutActivity, order: Order) {
        withContext(Dispatchers.IO) {
            mFireStore.collection(Constants.ORDERS)
                .document()
                .set(order, SetOptions.merge())
                .addOnSuccessListener {
                    activity.orderPlacedSuccess()
                }
                .addOnFailureListener { e ->
                    activity.hideProgressDialog()
                    Log.e(
                        activity.javaClass.simpleName,
                        "Error while placing an order.",
                        e
                    )
                }
        }
    }

    override suspend fun deleteProduct(fragment: ProductsFragment, productId: String) {
        withContext(Dispatchers.IO) {
            mFireStore.collection(Constants.PRODUCTS)
                .document(productId)
                .delete()
                .addOnSuccessListener {
                    fragment.productDeleteSuccess()
                }.addOnFailureListener { e ->
                    fragment.hideProgressDialog()
                    Log.e(
                        fragment.requireActivity().javaClass.simpleName,
                        "Error while deleting the product.",
                        e
                    )
                }
        }
    }

    override suspend fun getProductDetails(activity: ProductDetailsActivity, productId: String) {
        withContext(Dispatchers.IO) {
            mFireStore.collection(Constants.PRODUCTS)
                .document(productId)
                .get()
                .addOnSuccessListener { document ->
                    Log.e(activity.javaClass.simpleName, document.toString())
                    val product = document.toObject(Product::class.java)
                    if (product != null) {
                        activity.productDetailsSuccess(product)
                        activity.favoriteProductDetailsSuccess(product)
                    }
                }
                .addOnFailureListener { e ->
                    activity.hideProgressDialog()
                    Log.e(
                        activity.javaClass.simpleName,
                        "Error while getting the product details.",
                        e
                    )
                }
        }
    }

    override suspend fun addCartItems(activity: ProductDetailsActivity, addToCart: CartItem) {
        withContext(Dispatchers.IO) {
            mFireStore.collection(Constants.CART_ITEMS)
                .document()
                .set(addToCart, SetOptions.merge())
                .addOnSuccessListener {
                    activity.addToCartSuccess()
                }
                .addOnFailureListener { e ->
                    activity.hideProgressDialog()
                    Log.e(
                        activity.javaClass.simpleName,
                        "Error while creating the document for cart item.",
                        e
                    )
                }
        }
    }


    override suspend fun checkIfItemExistInCart(
        activity: ProductDetailsActivity,
        ProductId: String
    ) {
        withContext(Dispatchers.IO) {
            mFireStore.collection(Constants.CART_ITEMS)
                .whereEqualTo(Constants.USER_ID, getCurrentUserID())
                .whereEqualTo(Constants.PRODUCT_ID, ProductId)
                .get()
                .addOnSuccessListener { document ->
                    Log.e(activity.javaClass.simpleName, document.documents.toString())
                    if (document.documents.size > 0) {
                        activity.productExistsInCart()
                    } else {
                        activity.hideProgressDialog()
                    }
                }
                .addOnFailureListener { e ->
                    activity.hideProgressDialog()
                    Log.e(
                        activity.javaClass.simpleName,
                        "Error while checking the existing cart list.",
                        e
                    )
                }
        }
    }

    override suspend fun checkIfFavoriteItemExistInCart(
        activity: ProductDetailsActivity,
        ProductId: String
    ) {
        withContext(Dispatchers.IO) {

            mFireStore.collection(Constants.FAVORITE_ITEMS)
                .whereEqualTo(Constants.USER_ID, getCurrentUserID())
                .whereEqualTo(Constants.PRODUCT_ID, ProductId)
                .get()
                .addOnSuccessListener { document ->
                    Log.e(activity.javaClass.simpleName, document.documents.toString())
                    if (document.documents.size > 0) {
                        activity.favoriteProductExistInCart()
                    } else {
                        activity.hideProgressDialog()
                    }
                }
                .addOnFailureListener { e ->
                    activity.hideProgressDialog()
                    Log.e(
                        activity.javaClass.simpleName,
                        "Error while checking the existing favorite list.",
                        e
                    )
                }
        }
    }

    override suspend fun removeItemFromCart(context: Context, cart_id: String) {
        withContext(Dispatchers.IO) {
            mFireStore.collection(Constants.CART_ITEMS)
                .document(cart_id) // cart id
                .delete()
                .addOnSuccessListener {
                    when (context) {
                        is CartListActivity -> {
                            context.itemRemovedSuccess()
                        }
                    }
                }
                .addOnFailureListener { e ->
                    when (context) {
                        is CartListActivity -> {
                            context.hideProgressDialog()
                        }
                    }
                    Log.e(
                        context.javaClass.simpleName,
                        "Error while removing the item from the cart list.",
                        e
                    )
                }
        }
    }

    override suspend fun getCartList(activity: Activity): List<CartItem> {
        withContext(Dispatchers.IO) {
            mFireStore.collection(Constants.CART_ITEMS)
                .whereEqualTo(Constants.USER_ID, getCurrentUserID())
                .get() // Will get the documents snapshots.
                .addOnSuccessListener { document ->
                    Log.e(activity.javaClass.simpleName, document.documents.toString())
                    var list: ArrayList<CartItem> = ArrayList()
                    for (i in document.documents) {
                        val cartItem = i.toObject(CartItem::class.java)!!
                        cartItem.id = i.id
                        list.add(cartItem)
                    }
                    when (activity) {
                        is CartListActivity -> {
                            GlobalScope.launch(Dispatchers.Main) {
                                activity.successCartItemsList(list)
                            }
                        }
                        is CheckoutActivity -> {
                            activity.successCartItemList(list)
                        }
                    }
                }
                .addOnFailureListener { e ->
                    when (activity) {
                        is CartListActivity -> {
                            activity.hideProgressDialog()
                        }
                        is CheckoutActivity -> {
                            activity.hideProgressDialog()
                        }
                    }
                    Log.e(
                        activity.javaClass.simpleName,
                        "Error while getting the cart list items.",
                        e
                    )
                }
        }
        return listOf()
    }




    override suspend fun updateMyCart(
        context: Context,
        cart_id: String,
        itemHashMap: HashMap<String, Any>
    ) {
        withContext(Dispatchers.IO) {

            mFireStore.collection(Constants.CART_ITEMS)
                .document(cart_id)
                .update(itemHashMap)
                .addOnSuccessListener {

                    when (context) {
                        is CartListActivity -> {
                            context.itemUpdateSuccess()
                        }
                    }
                }.addOnFailureListener { e ->
                    //Hide the progress dialog if there is any error
                    when (context) {
                        is CartListActivity -> {
                            context.hideProgressDialog()
                        }
                    }
                    Log.e(
                        context.javaClass.simpleName,
                        "Error while update the cart item.",
                        e
                    )
                }
        }
    }

    override suspend fun uploadProductDetails(activity: AddproductActivity, productInfo: Product) {
        withContext(Dispatchers.IO) {
            mFireStore.collection(Constants.PRODUCTS)
                .document()
                .set(productInfo, SetOptions.merge())
                .addOnSuccessListener {
                    activity.productUploadSuccess()
                }
                .addOnFailureListener { e ->
                    activity.hideProgressDialog()
                    Log.e(
                        activity.javaClass.simpleName,
                        "Error while uploading the product details.",
                        e
                    )
                }
        }
    }

    override suspend fun updateUserProfileData(
        activity: Activity,
        userHashMap: HashMap<String, Any>
    ) {
        withContext(Dispatchers.IO) {

            mFireStore.collection(Constants.USERS)
                .document(getCurrentUserID())
                .update(userHashMap)
                .addOnSuccessListener {
                    when (activity) {
                        is Client_Profile_Activity -> {
                            activity.userProfileUpdateSuccess()
                        }
                    }
                }
                .addOnFailureListener { e ->
                    when (activity) {
                        is Client_Profile_Activity -> {
                            activity.hideProgressDialog()
                        }
                    }

                    Log.e(
                        activity.javaClass.simpleName,
                        "Error while the user details.",
                        e
                    )
                }
        }
    }

    override suspend fun getAddressesListClient(activity: AddressList_Client) {
        withContext(Dispatchers.IO) {

            mFireStore.collection(Constants.ADDRESSES_CLIENT)
                .whereEqualTo(Constants.USER_ID, getCurrentUserID())
                .get()
                .addOnSuccessListener { document ->
                    Log.e(activity.javaClass.simpleName, document.documents.toString())
                    val addressesList: ArrayList<AddressClient> = ArrayList()
                    for (i in document.documents) {
                        val address = i.toObject(AddressClient::class.java)!!
                        address.id = i.id
                        addressesList.add(address)
                    }
                    activity.successAddressListFromFirestore(addressesList)
                }.addOnFailureListener { e ->
                    activity.hideProgressDialog()
                    Log.e(activity.javaClass.simpleName, "Error while getting the address list.", e)
                }
        }
    }

    override suspend fun deleteAddress(activity: AddressList_Client, addressId: String) {
        withContext(Dispatchers.IO) {
            mFireStore.collection(Constants.ADDRESSES_CLIENT)
                .document(addressId)
                .delete()
                .addOnFailureListener {
                    GlobalScope.launch(Dispatchers.IO) {
                        activity.deleteAddressSuccess()
                    }
                }
                .addOnFailureListener { e ->
                    activity.hideProgressDialog()
                    Log.e(
                        activity.javaClass.simpleName,
                        "Error while deleting the address",
                        e
                    )
                }
        }
    }

    override suspend fun deleteOrder(fragent: OrdersFragment, orderId: String) {
        withContext(Dispatchers.IO) {
            mFireStore.collection(Constants.ORDERS)
                .document(orderId)
                .delete()
                .addOnFailureListener {
                    GlobalScope.launch(Dispatchers.IO) {
                        fragent.deleteOrderSuccess()
                    }
                }
                .addOnFailureListener { e ->
                    fragent.hideProgressDialog()
                    Log.e(
                        fragent.javaClass.simpleName,
                        "Error while deleting the order",
                        e
                    )
                }
        }
    }

    override suspend fun updateAddressClient(
        activity: AddEditAddress,
        addressInfo: AddressClient,
        addressId: String
    ) {
        withContext(Dispatchers.IO) {
            mFireStore.collection(Constants.ADDRESSES_CLIENT)
                .document(addressId)
                .set(addressInfo, SetOptions.merge())
                .addOnSuccessListener {
                    activity.addUpdateAddressSuccess()
                }
                .addOnFailureListener { e ->
                    activity.hideProgressDialog()
                    Log.e(
                        activity.javaClass.simpleName,
                        "Error while updating the Address.",
                        e
                    )

                }
        }
    }

    override suspend fun addAddressClient(
        activity: AddEditAddress,
        addressInfo: AddressClient
    ) {
        withContext(Dispatchers.IO) {
            mFireStore.collection(Constants.ADDRESSES_CLIENT)
                .document()
                .set(addressInfo, SetOptions.merge())
                .addOnSuccessListener {
                    activity.addUpdateAddressSuccess()
                }
                .addOnFailureListener { e ->
                    activity.hideProgressDialog()
                    Log.e(
                        activity.javaClass.simpleName,
                        "Error while adding the address.",
                        e
                    )
                }
        }
    }

    override suspend fun getDashboardItemsList(fragment: DashboardFragment) {
        withContext(Dispatchers.IO) {
            mFireStore.collection(Constants.PRODUCTS)
                .get()
                .addOnSuccessListener { document ->
                    Log.e(fragment.javaClass.simpleName, document.documents.toString())
                    val productsList: ArrayList<Product> = ArrayList()
                    for (i in document.documents) {
                        val product = i.toObject(Product::class.java)!!
                        product.product_id = i.id
                        productsList.add(product)
                    }
                    GlobalScope.launch(Dispatchers.Main) {
                        fragment.adapter.setList(productsList)
                        fragment.successDashboardItemsList(productsList)
                    }
                }
                .addOnFailureListener { e ->
                    fragment.hideProgressDialog()
                    Log.e(
                        fragment.javaClass.simpleName,
                        "Error while getting dashboard items list.",
                        e
                    )
                }
        }
    }
}