package com.example.designersstore.utils

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.provider.MediaStore
import android.webkit.MimeTypeMap

object Constants {



    //Designers
    const val DESIGNERS:String="designers"
    const val EXTRA_DESIGNERS_DETAILS: String = "extra_designer_details"
    const val DESIGNERSSTORE_PREFERENCES: String = "DesignersStorePrefs"
    const val LOGGED_IN_USERNAME: String = "logged_in_username"
    const val ADD_ADDRESS_REQUEST_CODE_DESIGNER: Int = 122
    const val EXTRA_ADDRESS_DETAILS_DESIGNER: String = "AddressDetailsDesigner"
    const val EXTRA_SELECT_ADDRESS_DESIGNER: String = "extra_select_address"
    const val HOME_DESIGNER: String = "Home"
    const val OFFICE_DESIGNER: String = "OfficeDesigner"
    const val OTHER_DESIGNER: String = "OtherDesigner"
    const val ADDRESSES_DESIGNERS: String = "designer addresses"
    const val DESIGNER_ID: String = "designer_id"


    //********************//

    const val MALE:String="male"
    const val FEMALE:String="female"
    const val PICK_IMAGE_REQUEST_CODE=1
    const val READ_STORAGE_PERMISSION_CODE=2
    const val USER_PROFILE_IMAGE:String="User_Profile_Image"
    const val FIRST_NAME:String="firstName"
    const val LAST_NAME:String="lastName"
    const val MOBILE:String="mobile"
    const val PROFESSION:String="profession"
    const val GENDER:String="gender"
    const val IMAGE:String="image"
    const val COMPLETE_PROFILE:String="profileCompleted"

    //Client
    const val USERS:String="users"
    const val EXTRA_USER_DETAILS: String = "extra_user_details"
    const val ADD_ADDRESS_REQUEST_CODE_CLIENT: Int = 121
    const val ADDRESSES_CLIENT: String = "Clients address"
    const val USER_ID: String = "user_id"
    const val EXTRA_ADDRESS_DETAILS_CLIENT: String = "AddressDetailsClient"
    const val EXTRA_SELECT_ADDRESS_CLIENT: String = "extra_select_address"
    const val HOME_CLIENT: String = "Home"
    const val OFFICE_CLIENT: String = "OfficeClient"
    const val OTHER_CLIENT: String = "OtherClient"


    //*******************//

    fun showImageChooser(activity:Activity){
        //An intent for launching the image selection of phone storage.
        val galleryIntent=Intent(
            Intent.ACTION_PICK,
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        )
        //Launches the image selection of phone storage using the constant code.
        activity.startActivityForResult(galleryIntent,PICK_IMAGE_REQUEST_CODE)
    }

    fun getFileExtension(activity: Activity, uri: Uri?): String? {
        /*
         * MimeTypeMap: Two-way map that maps MIME-types to file extensions and vice versa.
         *
         * getSingleton(): Get the singleton instance of MimeTypeMap.
         *
         * getExtensionFromMimeType: Return the registered extension for the given MIME type.
         *
         * contentResolver.getType: Return the MIME type of the given content URL.
         */
        return MimeTypeMap.getSingleton()
            .getExtensionFromMimeType(activity.contentResolver.getType(uri!!))
    }
}