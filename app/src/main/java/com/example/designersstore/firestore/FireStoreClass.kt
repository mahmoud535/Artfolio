package com.example.designersstore.firestore

import android.app.Activity
import android.content.Context
import android.content.SharedPreferences
import android.net.Uri
import android.util.Log
import androidx.fragment.app.Fragment
import com.example.designersstore.models.*
import com.example.designersstore.ui.activityclient.*
import com.example.designersstore.ui.activitydesigners.*
import com.example.designersstore.ui.fragmentsclient.DashboardFragment
import com.example.designersstore.ui.fragmentsclient.OrdersFragment
import com.example.designersstore.ui.fragmentsdesigners.ProductsFragment
import com.example.designersstore.ui.fragmentsdesigners.SoldProductsFragment
import com.example.designersstore.utils.Constants
import com.google.android.gms.common.internal.safeparcel.SafeParcelable.Class
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference

class FireStoreClass {

    private val mFireStore=FirebaseFirestore.getInstance()


    /**
     * A function to make an entry of the registered user in the FireStore database.
     */
    fun registerUser(activity: Register_Client_Activity, userInfo:User){
        // TODO Step 3: Replace the hard coded string with constant value which is added in the Constants object.
        // The "users" is collection name. If the collection is already created then it will not create the same one again.
        mFireStore.collection(Constants.USERS)
        // Document ID for users fields. Here the document it is the User ID.
            .document(userInfo.id)
        // Here the userInfo are Field and the SetOption is set to merge. It is for if we wants to merge later on instead of replacing the fields.
            .set(userInfo, SetOptions.merge())
            .addOnSuccessListener {
                // Here call a function of base activity for transferring the result to it.
                activity.userRegistrationSuccess()
            }
            .addOnFailureListener { e->
                activity.hideProgressDialog()
                Log.e(
                    activity.javaClass.simpleName,
                    "Error while registering the user.",
                    e
                )
            }
    }
    //designer
    fun registerDesigner(activity: Register_Designers_Activity, designerInfo:Designer){
        // TODO Step 3: Replace the hard coded string with constant value which is added in the Constants object.
        // The "designers" is collection name. If the collection is already created then it will not create the same one again.
        mFireStore.collection(Constants.DESIGNERS)
            // Document ID for designers fields. Here the document it is the User ID.
            .document(designerInfo.id)
            // Here the designersInfo are Field and the SetOption is set to merge. It is for if we wants to merge later on instead of replacing the fields.
            .set(designerInfo, SetOptions.merge())
            .addOnSuccessListener {
                // Here call a function of base activity for transferring the result to it.
                activity.designerRegistrationSuccess()
            }
            .addOnFailureListener { e->
                activity.hideProgressDialog()
                Log.e(
                    activity.javaClass.simpleName,
                    "Error while registering the designer.",
                    e
                )
            }
    }

    fun getCurrentUserID():String{
        //An Instance of currentUser Using Firebase
        val currentUser=FirebaseAuth.getInstance().currentUser

        //A variable to assign the currentUserId if it is not null or else it will be blank.
        var currentUserID=""
        if (currentUser != null){
            currentUserID=currentUser.uid
        }
        return currentUserID
    }

    fun getCurrentDesignerID():String{
        //An Instance of currentDesigner Using Firebase
        val currentDesigner=FirebaseAuth.getInstance().currentUser

        //A variable to assign the currentDesignerId if it is not null or else it will be blank.
        var currentDesignerID=""
        if (currentDesigner != null){
            currentDesignerID=currentDesigner.uid
        }
        return currentDesignerID
    }

    fun getUserDetails(activity:Activity){
        //Here we pass the collections name from which we want the data.
        mFireStore.collection(Constants.USERS)
        // The document id to get the Fields of user.
            .document(getCurrentUserID())
            .get()
            .addOnSuccessListener { document->
                Log.i(activity.javaClass.simpleName,document.toString())

                //Here we have received the document snapshot which is converted into the User Data model object
                val user=document.toObject(User::class.java)!!

                val sharedpreferences=
                    activity.getSharedPreferences(
                        Constants.DESIGNERSSTORE_PREFERENCES,
                        Context.MODE_PRIVATE
                    )

                val editor:SharedPreferences.Editor=sharedpreferences.edit()
                //Key:logged in Username:mahmoud mohamed
                //value
                editor.putString(
                    Constants.LOGGED_IN_USERNAME,
                    "${user.firstName} ${user.lastName}"
                )
                editor.apply()

                //ToDo:pass the result to the Login Activity
                //Start
                when (activity){
                    is Login_Client_Activity ->{
                        //Call a function of base activity for transferring  the result to it.
                        activity.userLoggedInSuccess(user)
                    }
                    is SettingsActivity->{
                        activity.userDetailsSuccess(user)
                    }

                }
            }
            .addOnFailureListener { e->
                //Hide the progress dialog if there is any error And print the error in log.
                when(activity){
                    is Login_Client_Activity ->{
                        activity.hideProgressDialog()
                    }
                    is SettingsActivity->{
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

    //designer
    fun getDesignersDetails(activity:Activity){
        //Here we pass the collections name from which we want the data.
        mFireStore.collection(Constants.DESIGNERS)
            // The document id to get the Fields of user.
            .document(getCurrentUserID())
            .get()
            .addOnSuccessListener { document->
                Log.i(activity.javaClass.simpleName,document.toString())

                //Here we have received the document snapshot which is converted into the User Data model object
                val designer=document.toObject(Designer::class.java)!!

                val sharedpreferences=
                    activity.getSharedPreferences(
                        Constants.DESIGNERSSTORE_PREFERENCES,
                        Context.MODE_PRIVATE
                    )

                val editor:SharedPreferences.Editor=sharedpreferences.edit()
                //Key:logged in Username:mahmoud mohamed
                //value
                editor.putString(
                    Constants.LOGGED_IN_USERNAME_DESIGNER,
                    "${designer.firstName} ${designer.lastName}"
                )
                editor.apply()

                //ToDo:pass the result to the Login Activity
                //Start
                when (activity){
                    is Login_Designers_Activity ->{
                        //Call a function of base activity for transferring  the result to it.
                        activity.designerLoggedInSuccess(designer)
                    }

                    is Settings_Designers_Activity->{
                       activity.designerDetailsSuccess(designer)
                    }

                }
            }
            .addOnFailureListener { e->
                //Hide the progress dialog if there is any error And print the error in log.
                when(activity){
                    is Login_Designers_Activity ->{
                        activity.hideProgressDialog()
                    }
                    is Settings_Designers_Activity ->{
                        activity.hideProgressDialog()
                    }
                }
                Log.e(
                    activity.javaClass.simpleName,
                    "Error while getting Designer details.",
                    e
                )
            }


    }

    fun uploadImageToCloudStorage(activity:Activity, imageFileURI: Uri?, imageType:String){
        //getting the storage reference
        val sRef: StorageReference= FirebaseStorage.getInstance().reference.child(
            imageType+System.currentTimeMillis()+ "."
        +Constants.getFileExtension(
                activity,
                imageFileURI
        )
        )
        //adding the file to reference
        sRef.putFile(imageFileURI!!)
            .addOnSuccessListener { taskSnapshot->
                // The image upload is success
                Log.e(
                    "Firebase Image URL",
                    taskSnapshot.metadata!!.reference!!.downloadUrl.toString()
                )
                // Get the downloadable url from the task snapshot
                taskSnapshot.metadata!!.reference!!.downloadUrl
                    .addOnSuccessListener { uri->
                        Log.e("Downloadable Image URL", uri.toString())

                        // TODO Step 8: Pass the success result to base class.
                        // START
                        // Here call a function of base activity for transferring the result to it.
                        when (activity) {
                            is Client_Profile_Activity -> {
                                activity.imageUploadSuccess(uri.toString())
                            }
                            is Designers_Profile_Activity -> {
                                activity.imageUploadSuccess(uri.toString())
                            }
                            is AddproductActivity->{
                                activity.imageUploadSuccess(uri.toString())
                            }
                        }
                    }
                    .addOnFailureListener {  exception ->
                        // Hide the progress dialog if there is any error. And print the error in log.
                        when (activity) {
                            is Client_Profile_Activity -> {
                                activity.hideProgressDialog()
                            }
                            is Designers_Profile_Activity->{
                                activity.hideProgressDialog()
                            }
                            is AddproductActivity->{
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

    fun getProductsList(fragment:Fragment){
        mFireStore.collection(Constants.PRODUCTS)
            .whereEqualTo(Constants.USER_ID,getCurrentUserID())
            .get()
            .addOnSuccessListener { document->
                Log.e("Products List",document.documents.toString())
                val productsList:ArrayList<Product> =ArrayList()
                for (i in document.documents){
                    val product=i.toObject(Product::class.java)
                    product!!.product_id=i.id
                    productsList.add(product)
                }
                when(fragment){
                    is ProductsFragment->{
                        fragment.successProductsListFromFireStore(productsList)
                    }
                }
            }

    }


    fun updateAllDetails(activity: CheckoutActivity, cartList: ArrayList<CartItem>,order: Order) {

        val writeBatch = mFireStore.batch()

        // Here we will update the product stock in the products collection based to cart quantity.
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

        // Delete the list of cart items
        for (cart in cartList) {

            val documentReference = mFireStore.collection(Constants.CART_ITEMS)
                .document(cart.id)
            writeBatch.delete(documentReference)
        }

        writeBatch.commit().addOnSuccessListener {

            activity.allDetailsUpdatedSuccessfully()

        }.addOnFailureListener { e ->
            // Here call a function of base activity for transferring the result to it.
            activity.hideProgressDialog()

            Log.e(activity.javaClass.simpleName, "Error while updating all the details after order placed.", e)
        }
    }

    fun getMyOrdersList(fragment: OrdersFragment) {
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

                fragment.populateOrdersListInUI(list)
            }
            .addOnFailureListener { e ->
                // Here call a function of base activity for transferring the result to it.

                fragment.hideProgressDialog()

                Log.e(fragment.javaClass.simpleName, "Error while getting the orders list.", e)
            }
    }

    fun placeOrder(activity: CheckoutActivity, order: Order) {

        mFireStore.collection(Constants.ORDERS)
            .document()
            // Here the userInfo are Field and the SetOption is set to merge. It is for if we wants to merge
            .set(order, SetOptions.merge())
            .addOnSuccessListener {

                // TODO Step 9: Notify the success result.
                // START
                // Here call a function of base activity for transferring the result to it.
                activity.orderPlacedSuccess()
                // END
            }
            .addOnFailureListener { e ->

                // Hide the progress dialog if there is any error.
                activity.hideProgressDialog()
                Log.e(
                    activity.javaClass.simpleName,
                    "Error while placing an order.",
                    e
                )
            }
    }

    // TODO Step 6: Create a function check whether the item already exist in the cart or not.
    // START
    /**
     * A function to check whether the item already exist in the cart or not.
     */
    fun deleteProduct(fragment:ProductsFragment,productId:String){
        mFireStore.collection(Constants.PRODUCTS)
            .document(productId)
            .delete()
            .addOnSuccessListener {
                fragment.productDeleteSuccess()
            }.addOnFailureListener {
                    e->
                //Hide the progress Dialog if there is an error
                fragment.hideProgressDialog()

                Log.e(
                    fragment.requireActivity().javaClass.simpleName,
                    "Error while deleting the product.",
                    e
                )
            }
    }

    fun getProductDetails(activity: ProductDetailsActivity,productId: String){
        mFireStore.collection(Constants.PRODUCTS)
            .document(productId)
            .get()
            .addOnSuccessListener { document->
                Log.e(activity.javaClass.simpleName,document.toString())
                val product=document.toObject(Product::class.java)
                if (product!=null){
                    activity.productDetailsSuccess(product)
                }
            }
            .addOnFailureListener {
                    e ->
                //Hide the progress dialog if there is an error.
                activity.hideProgressDialog()
                Log.e(activity.javaClass.simpleName,"Error while getting the product details.",e)
            }
    }

    fun getSoldProductsList(fragment: SoldProductsFragment) {
        // The collection name for SOLD PRODUCTS
        mFireStore.collection(Constants.SOLD_PRODUCTS)
            .whereEqualTo(Constants.USER_ID, getCurrentUserID())
            .get() // Will get the documents snapshots.
            .addOnSuccessListener { document ->
                // Here we get the list of sold products in the form of documents.
                Log.e(fragment.javaClass.simpleName, document.documents.toString())

                // Here we have created a new instance for Sold Products ArrayList.
                val list: ArrayList<SoldProduct> = ArrayList()

                // A for loop as per the list of documents to convert them into Sold Products ArrayList.
                for (i in document.documents) {

                    val soldProduct = i.toObject(SoldProduct::class.java)!!
                    soldProduct.id = i.id

                    list.add(soldProduct)
                }

                fragment.successSoldProductsList(list)
            }
            .addOnFailureListener { e ->
                // Hide the progress dialog if there is any error.
                fragment.hideProgressDialog()

                Log.e(
                    fragment.javaClass.simpleName,
                    "Error while getting the list of sold products.",
                    e
                )
            }
    }

    /**
     * A function to add the item to the cart in the cloud firestore.
     *
     * @param activity
     * @param addToCart
     */
    fun addCartItems(activity: ProductDetailsActivity, addToCart: CartItem) {
        mFireStore.collection(Constants.CART_ITEMS)
            .document()
        // Here the userInfo are Field and the SetOption is set to merge. It is for if we wants to merge
            .set(addToCart, SetOptions.merge())
            .addOnSuccessListener {

                // Here call a function of base activity for transferring the result to it.
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
    fun checkIfItemExistInCart(activity: ProductDetailsActivity,ProductId:String){
        mFireStore.collection(Constants.CART_ITEMS)
            .whereEqualTo(Constants.USER_ID,getCurrentUserID())
            .whereEqualTo(Constants.PRODUCT_ID,ProductId)
            .get()
            .addOnSuccessListener { document ->
                Log.e(activity.javaClass.simpleName, document.documents.toString())

                // TODO Step 8: Notify the success result to the base class.
                // START
                // If the document size is greater than 1 it means the product is already added to the cart.
                if (document.documents.size > 0) {
                    activity.productExistsInCart()
                } else {
                    activity.hideProgressDialog()
                }
                // END
            }
            .addOnFailureListener { e->
                // Hide the progress dialog if there is an error.
                activity.hideProgressDialog()

                Log.e(
                    activity.javaClass.simpleName,
                    "Error while checking the existing cart list.",
                    e
                )
            }
    }


    fun removeItemFromCart(context: Context, cart_id: String) {

        // Cart items collection name
        mFireStore.collection(Constants.CART_ITEMS)
            .document(cart_id) // cart id
            .delete()
            .addOnSuccessListener {

                // Notify the success result of the removed cart item from the list to the base class.
                when (context) {
                    is CartListActivity -> {
                        context.itemRemovedSuccess()
                    }
                }
            }
            .addOnFailureListener { e ->

                // Hide the progress dialog if there is any error.
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

    fun getAllProductList(activity:Activity){
        mFireStore.collection(Constants.PRODUCTS)
            .get()
            .addOnSuccessListener { document->
                Log.e("Product List",document.documents.toString())
                val productsList:ArrayList<Product> = ArrayList()
                for (i in document.documents){
                    val product= i.toObject(Product::class.java)
                    product!!.product_id=i.id

                    productsList.add(product)
                }
                when(activity){
                    is CartListActivity->{
                        activity.successProductsListFromFireStore(productsList)
                    }

                    //TODO:COME********************
                    is CheckoutActivity->{
                        activity.successProductListFromFireStore(productsList)
                    }
                }
            }.addOnFailureListener { e->
                when(activity){
                    is CartListActivity->{
                        activity.hideProgressDialog()
                    }
                }

                Log.e("Get Product List","Error while getting all product list.",e)
            }
    }

    fun getCartList(activity: Activity) {
        // The collection name for PRODUCTS
        mFireStore.collection(Constants.CART_ITEMS)
            .whereEqualTo(Constants.USER_ID, getCurrentUserID())
            .get() // Will get the documents snapshots.
            .addOnSuccessListener { document ->

                // Here we get the list of cart items in the form of documents.
                Log.e(activity.javaClass.simpleName, document.documents.toString())

                // Here we have created a new instance for Cart Items ArrayList.
                val list: ArrayList<CartItem> = ArrayList()

                // A for loop as per the list of documents to convert them into Cart Items ArrayList.
                for (i in document.documents) {

                    val cartItem = i.toObject(CartItem::class.java)!!
                    cartItem.id = i.id

                    list.add(cartItem)
                }

                when (activity) {
                    is CartListActivity -> {
                        activity.successCartItemsList(list)
                    }
                    //TODO:COME********************
                    is CheckoutActivity->{
                        activity.successCartItemList(list)
                    }
                }
            }
            .addOnFailureListener { e ->
                // Hide the progress dialog if there is an error based on the activity instance.
                when (activity) {
                    is CartListActivity -> {
                        activity.hideProgressDialog()
                    }
                    //TODO:COME********************
                    is CheckoutActivity->{
                        activity.hideProgressDialog()
                    }
                }

                Log.e(activity.javaClass.simpleName, "Error while getting the cart list items.", e)
            }
    }

    fun updateMyCart(context: Context,cart_id: String,itemHashMap:HashMap<String,Any>){
        mFireStore.collection(Constants.CART_ITEMS)
            .document(cart_id)
            .update(itemHashMap)
            .addOnSuccessListener{

                when(context){
                    is CartListActivity ->{
                        context.itemUpdateSuccess()
                    }
                }
            }.addOnFailureListener {
                    e->
                //Hide the progress dialog if there is any error
                when(context){
                    is CartListActivity ->{
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

    fun uploadProductDetails(activity: AddproductActivity,productInfo:Product){
        mFireStore.collection(Constants.PRODUCTS)
            .document()
            .set(productInfo, SetOptions.merge())
            .addOnSuccessListener {
                //Here call a function of base activity for transfer the result to it .
                activity.productUploadSuccess()
            }
            .addOnFailureListener { e->
                activity.hideProgressDialog()
                Log.e(
                    activity.javaClass.simpleName,
                    "Error while uploading the product details.",
                    e
                )
            }
    }

    fun updateUserProfileData(activity: Activity,userHashMap: HashMap<String,Any>){
        mFireStore.collection(Constants.USERS)
            .document(getCurrentUserID())
            .update(userHashMap)
            .addOnSuccessListener {
                when(activity){
                    is Client_Profile_Activity ->{
                        //Hide the progress dialog if there is any error and print the error in log.
                        activity.userProfileUpdateSuccess()
                    }
                }
            }
            .addOnFailureListener { e ->
                when (activity) {
                    is Client_Profile_Activity -> {
                        //Hide the progress dialog if there is any error and print the error in log.
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

    //DESIGNER
    fun updateDesignerProfileData(activity: Activity,designerHashMap: HashMap<String,Any>){
        mFireStore.collection(Constants.DESIGNERS)
            .document(getCurrentUserID())
            .update(designerHashMap)
            .addOnSuccessListener {
                when(activity){
                    is Designers_Profile_Activity ->{
                        //Hide the progress dialog if there is any error and print the error in log.
                        activity.DesignerProfileUpdateSuccess()
                    }
                }
            }
            .addOnFailureListener { e ->
                when (activity) {
                    is Designers_Profile_Activity -> {
                        //Hide the progress dialog if there is any error and print the error in log.
                        activity.hideProgressDialog()
                    }
                }

                Log.e(
                    activity.javaClass.simpleName,
                    "Error while the designer details.",
                    e
                )
            }
    }


    fun getAddressesListClient(activity:AddressListActivity_Client){
        mFireStore.collection(Constants.ADDRESSES_CLIENT)
            .whereEqualTo(Constants.USER_ID,getCurrentUserID())
            .get()
            .addOnSuccessListener {
                    document->
                //Here we get the list of boards in the form of document
                Log.e(activity.javaClass.simpleName,document.documents.toString())
                //Here we have created a new instance for address ArrayList.
                val addressesList:ArrayList<AddressClient> =ArrayList()
                for (i in document.documents){
                    val address=i.toObject(AddressClient::class.java)!!
                    address.id=i.id
                    addressesList.add(address)
                }
                activity.successAddressListFromFirestore(addressesList)
            }.addOnFailureListener {
                e->
                //Here call a function of base Activity for transferring the result to it.
                activity.hideProgressDialog()
                Log.e(activity.javaClass.simpleName,"Error while getting the address list.",e)
            }
    }


    fun getAddressesListDesigners(activity:AddressListActivity_Designer){
        mFireStore.collection(Constants.ADDRESSES_DESIGNERS)
            .whereEqualTo(Constants.USER_ID,getCurrentUserID())
            .get()
            .addOnSuccessListener {
                    document->
                //Here we get the list of boards in the form of document
                Log.e(activity.javaClass.simpleName,document.documents.toString())
                //Here we have created a new instance for address ArrayList.
                val addressesList:ArrayList<AddressDesigner> =ArrayList()
                for (i in document.documents){
                    val address=i.toObject(AddressDesigner::class.java)!!
                    address.id=i.id
                    addressesList.add(address)
                }
                activity.successAddressListFromFirestore(addressesList)
            }.addOnFailureListener {
                    e->
                //Here call a function of base Activity for transferring the result to it.
                activity.hideProgressDialog()
                Log.e(activity.javaClass.simpleName,"Error while getting the address list.",e)
            }
    }


    fun deleteAddress (activity:AddressListActivity_Client,addressId:String){
        mFireStore.collection(Constants.ADDRESSES_CLIENT)
            .document(addressId)
            .delete()
            .addOnFailureListener {
                activity.deleteAddressSuccess()
            }
            .addOnFailureListener {
                e->
                activity.hideProgressDialog()
                Log.e(
                    activity.javaClass.simpleName,
                    "Error while deleting the address",
                    e
                )
            }
    }
    fun deleteAddressDesigner (activity:AddressListActivity_Designer,addressId:String){
        mFireStore.collection(Constants.ADDRESSES_DESIGNERS)
            .document(addressId)
            .delete()
            .addOnFailureListener {
                activity.deleteAddressSuccess()
            }
            .addOnFailureListener {
                    e->
                activity.hideProgressDialog()
                Log.e(
                    activity.javaClass.simpleName,
                    "Error while deleting the address",
                    e
                )
            }
    }

    fun updateAddressClient(
        activity: AddEditAddressActivity,
        addressInfo: AddressClient,
        addressId: String
    ) {
        mFireStore.collection(Constants.ADDRESSES_CLIENT)
            .document(addressId)
            //Here the userInfo are Field and the SetOption is set to merge.It is for if we wants to merge
            .set(addressInfo, SetOptions.merge())
            .addOnSuccessListener {
                //Here call a function of base activity for transferring the result to it
                activity.addUpdateAddressSuccess()
            }
            .addOnFailureListener { e->
                activity.hideProgressDialog()
                Log.e(
                    activity.javaClass.simpleName,
                    "Error while updating the Address.",
                    e
                )

            }
    }

    fun updateAddressDesigner(
        activity: AddEditAddressActivityDesigners,
        addressInfo: AddressDesigner,
        addressId: String
    ) {
        mFireStore.collection(Constants.ADDRESSES_DESIGNERS)
            .document(addressId)
            //Here the userInfo are Field and the SetOption is set to merge.It is for if we wants to merge
            .set(addressInfo, SetOptions.merge())
            .addOnSuccessListener {
                //Here call a function of base activity for transferring the result to it
                activity.addUpdateAddressSuccess()
            }
            .addOnFailureListener { e->
                activity.hideProgressDialog()
                Log.e(
                    activity.javaClass.simpleName,
                    "Error while updating the Address.",
                    e
                )

            }
    }

    fun addAddressClient(activity: AddEditAddressActivity, addressInfo: AddressClient) {
        // Collection name address.
        mFireStore.collection(Constants.ADDRESSES_CLIENT)
            .document()
            // Here the userInfo are Field and the SetOption is set to merge. It is for if we wants to merge
            .set(addressInfo, SetOptions.merge())
            .addOnSuccessListener {

                // TODO Step 5: Notify the success result to the base class.
                // START
                // Here call a function of base activity for transferring the result to it.
                activity.addUpdateAddressSuccess()
                // END
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

    fun addAddressDesigner(activity: AddEditAddressActivityDesigners, addressInfo: AddressDesigner) {
        // Collection name address.
        mFireStore.collection(Constants.ADDRESSES_DESIGNERS)
            .document()
            // Here the userInfo are Field and the SetOption is set to merge. It is for if we wants to merge
            .set(addressInfo, SetOptions.merge())
            .addOnSuccessListener {

                // TODO Step 5: Notify the success result to the base class.
                // START
                // Here call a function of base activity for transferring the result to it.
                activity.addUpdateAddressSuccess()
                // END
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
    //fun updateAddress(activity:AddEditAddressActivity)

    fun getDashboardItemsList(fragment: DashboardFragment){
        mFireStore.collection(Constants.PRODUCTS)
            .get()
            .addOnSuccessListener { document->
                Log.e(fragment.javaClass.simpleName,document.documents.toString())
                val productsList:ArrayList<Product> =ArrayList()
                for (i in document.documents){
                    val product=i.toObject(Product::class.java)!!
                    product.product_id=i.id
                    productsList.add(product)
                }
                fragment.successDashboardItemsList(productsList)
            }
            .addOnFailureListener { e ->
                // Hide the progress dialog if there is any error which getting the dashboard items list.
                fragment.hideProgressDialog()
                Log.e(fragment.javaClass.simpleName, "Error while getting dashboard items list.", e)
            }
    }
}