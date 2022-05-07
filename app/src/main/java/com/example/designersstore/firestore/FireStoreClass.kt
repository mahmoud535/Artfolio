package com.example.designersstore.firestore

import android.app.Activity
import android.content.Context
import android.content.SharedPreferences
import android.net.Uri
import android.util.Log
import com.example.designersstore.models.AddressClient
import com.example.designersstore.models.AddressDesigner
import com.example.designersstore.models.Designer
import com.example.designersstore.models.User
import com.example.designersstore.ui.activityclient.*
import com.example.designersstore.ui.activitydesigners.*
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
                    Constants.LOGGED_IN_USERNAME,
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
                        }
                    }
                    .addOnFailureListener {  exception ->
                        // Hide the progress dialog if there is any error. And print the error in log.
                        when (activity) {
                            is Client_Profile_Activity -> {
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
            .whereEqualTo(Constants.DESIGNER_ID,getCurrentDesignerID())
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
}