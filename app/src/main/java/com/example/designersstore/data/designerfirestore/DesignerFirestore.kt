package com.example.designersstore.data.designerfirestore

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class DesignerFirestore{
    private val mFireStore= FirebaseFirestore.getInstance()

    fun getCurrentDesignerID():String{
        //An Instance of currentDesigner Using Firebase
        val currentDesigner= FirebaseAuth.getInstance().currentUser
        //A variable to assign the currentDesignerId if it is not null or else it will be blank.
        var currentDesignerID=""
        if (currentDesigner != null){
            currentDesignerID=currentDesigner.uid
        }
        return currentDesignerID
    }
}