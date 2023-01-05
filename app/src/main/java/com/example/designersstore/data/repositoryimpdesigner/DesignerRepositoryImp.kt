package com.example.designersstore.data.repositoryimpdesigner

import android.app.Activity
import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import com.example.designersstore.domain.models.repository.DesignerRepository
import com.example.designersstore.data.repositoryimp.UserFirestoreRepositoryImp
import com.example.designersstore.data.userfirestore.UserFireStore
import com.example.designersstore.domain.models.*
import com.example.designersstore.presentation.ui.activityclient.cartlist.CartListActivity
import com.example.designersstore.presentation.ui.activityclient.checkout.CheckoutActivity
import com.example.designersstore.presentation.ui.activitydesigners.*
import com.example.designersstore.presentation.ui.activitydesigners.addressdesigner.AddressList_Designer
import com.example.designersstore.presentation.ui.fragmentsdesigners.SoldProductsFragment
import com.example.designersstore.presentation.utils.Constants
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class DesignerRepositoryImp(private val mFireStore: FirebaseFirestore = FirebaseFirestore.getInstance()) :
    DesignerRepository {
    override suspend fun registerDesigner(
        activity: Register_Designers_Activity,
        designerInfo: Designer
    ) {
        withContext(Dispatchers.IO) {
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
                .addOnFailureListener { e ->
                    activity.hideProgressDialog()
                    Log.e(
                        activity.javaClass.simpleName,
                        "Error while registering the designer.",
                        e
                    )
                }
        }
    }

    override suspend fun getCurrentDesignerID(): String =
        withContext(Dispatchers.IO) {
            //An Instance of currentDesigner Using Firebase
            val currentDesigner = FirebaseAuth.getInstance().currentUser

            //A variable to assign the currentDesignerId if it is not null or else it will be blank.
            var currentDesignerID = ""
            if (currentDesigner != null) {
                currentDesignerID = currentDesigner.uid
            }
            return@withContext currentDesignerID
        }


    override suspend fun getDesignersDetails(activity: Activity) {

        withContext(Dispatchers.IO) {
            //Here we pass the collections name from which we want the data.
            mFireStore.collection(Constants.DESIGNERS)
                // The document id to get the Fields of user.
                .document(UserFirestoreRepositoryImp().getCurrentUserID())
                .get()
                .addOnSuccessListener { document ->
                    Log.i(activity.javaClass.simpleName, document.toString())

                    //Here we have received the document snapshot which is converted into the User Data model object
                    val designer = document.toObject(Designer::class.java)!!

                    val sharedpreferences =
                        activity.getSharedPreferences(
                            Constants.DESIGNERSSTORE_PREFERENCES,
                            Context.MODE_PRIVATE
                        )

                    val editor: SharedPreferences.Editor = sharedpreferences.edit()
                    //Key:logged in Username:mahmoud mohamed
                    //value
                    editor.putString(
                        Constants.LOGGED_IN_USERNAME_DESIGNER,
                        "${designer.firstName} ${designer.lastName}"
                    )
                    editor.apply()

                    //ToDo:pass the result to the Login Activity
                    //Start
                    when (activity) {
                        is Login_Designers_Activity -> {
                            //Call a function of base activity for transferring  the result to it.
                            activity.designerLoggedInSuccess(designer)
                        }

                        is Settings_Designers_Activity -> {
                            activity.designerDetailsSuccess(designer)
                        }

                    }
                }
                .addOnFailureListener { e ->
                    //Hide the progress dialog if there is any error And print the error in log.
                    when (activity) {
                        is Login_Designers_Activity -> {
                            activity.hideProgressDialog()
                        }
                        is Settings_Designers_Activity -> {
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


    }

    override suspend fun getSoldProductsList(fragment: SoldProductsFragment) {

        withContext(Dispatchers.IO) {
            // The collection name for SOLD PRODUCTS
            mFireStore.collection(Constants.SOLD_PRODUCTS)
                .whereEqualTo(Constants.USER_ID, UserFireStore().getCurrentUserID())
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

    }

    override suspend fun getAllProductList(activity: Activity):List<Product> {
         withContext(Dispatchers.IO) {
            mFireStore.collection(Constants.PRODUCTS)
                .get()
                .addOnSuccessListener { document ->
                    Log.e("Product List", document.documents.toString())
                    val productsList: ArrayList<Product> = ArrayList()
                    for (i in document.documents) {
                        val product = i.toObject(Product::class.java)
                        product!!.product_id = i.id

                        productsList.add(product)
                    }
                    when (activity) {
                        is CartListActivity -> {
                            activity.successProductsListFromFireStore(productsList)

                        }

                        //TODO:COME********************
                        is CheckoutActivity -> {
                            activity.successProductListFromFireStore(productsList)
                        }
                    }
                }.addOnFailureListener { e ->
                    when (activity) {
                        is CartListActivity -> {
                            activity.hideProgressDialog()
                        }
                    }

                    Log.e("Get Product List", "Error while getting all product list.", e)
                }
        }
        return listOf()
    }



    override suspend fun updateDesignerProfileData(
        activity: Activity,
        designerHashMap: HashMap<String, Any>
    ) {
        withContext(Dispatchers.IO) {


            mFireStore.collection(Constants.DESIGNERS)
                .document(UserFirestoreRepositoryImp().getCurrentUserID())
                .update(designerHashMap)
                .addOnSuccessListener {
                    when (activity) {
                        is Designers_Profile_Activity -> {
                            //Hide the progress dialog if there is any error and print the error in log.
                            activity.designerProfileUpdateSuccess()
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
    }

    override suspend fun getAddressesListDesigners(activity: AddressList_Designer) {
        withContext(Dispatchers.IO) {
            mFireStore.collection(Constants.ADDRESSES_DESIGNERS)
                .whereEqualTo(Constants.USER_ID, UserFirestoreRepositoryImp().getCurrentUserID())
                .get()
                .addOnSuccessListener { document ->
                    //Here we get the list of boards in the form of document
                    Log.e(activity.javaClass.simpleName, document.documents.toString())
                    //Here we have created a new instance for address ArrayList.
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

    override suspend fun deleteAddressDesigner(
        activity: AddressList_Designer,
        addressId: String
    ) {
        withContext(Dispatchers.IO) {


            mFireStore.collection(Constants.ADDRESSES_DESIGNERS)
                .document(addressId)
                .delete()
                .addOnFailureListener {
                    activity.deleteAddressSuccess()
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

    override suspend fun deleteSoldProduct( orderId: String, fragment:SoldProductsFragment){
        mFireStore.collection(Constants.SOLD_PRODUCTS)
            .document(orderId)
            .delete()
            .addOnFailureListener {
                fragment.soldProductDeletedSuccess()
            }
            .addOnFailureListener {
                fragment.hideProgressDialog()
                Log.e(
                    fragment.javaClass.simpleName,
                    "Error while deleting the address",
                )
            }
    }

    override suspend fun updateAddressDesigner(
        activity: AddEditAddressDesigners,
        addressInfo: AddressClient,
        addressId: String
    ) {
        withContext(Dispatchers.IO) {


            mFireStore.collection(Constants.ADDRESSES_DESIGNERS)
                .document(addressId)
                //Here the userInfo are Field and the SetOption is set to merge.It is for if we wants to merge
                .set(addressInfo, SetOptions.merge())
                .addOnSuccessListener {
                    //Here call a function of base activity for transferring the result to it
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

    override suspend fun addAddressDesigner(
        activity: AddEditAddressDesigners,
        addressInfo: AddressClient
    ) {
        withContext(Dispatchers.IO) {
            mFireStore.collection(Constants.ADDRESSES_DESIGNERS)
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
}