package com.example.designersstore.data.userfirestore

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class UserFireStore {

    private val mFireStore=FirebaseFirestore.getInstance()

    fun getCurrentUserID():String{
        val currentUser=FirebaseAuth.getInstance().currentUser
        var currentUserID=""
        if (currentUser != null){
            currentUserID=currentUser.uid
        }
        return currentUserID
    }

}