package com.example.designersstore.presentation.ui.fragmentsdesigners

import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.app.Application
import android.content.Context
import androidx.fragment.app.viewModels
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import com.example.designersstore.R
import com.example.designersstore.data.userfirestore.UserFirestoreViewModel

class ProductsViewModel(application: Application):AndroidViewModel(application) {
    private val viewModel: UserFirestoreViewModel = UserFirestoreViewModel()
    @SuppressLint("StaticFieldLeak")
    lateinit var context:Context
    var app = application
    init {

       this.app= application
    }

    fun showAlertDialogToDeleteProduct(productID: String) {
        val builder = AlertDialog.Builder(app)
        builder.setTitle(ProductsFragment().resources.getString(R.string.delete_dialog_title))
        builder.setMessage(ProductsFragment().resources.getString(R.string.delete_dialog_message))
        builder.setIcon(android.R.drawable.ic_dialog_alert)
        builder.setPositiveButton(ProductsFragment().resources.getString(R.string.yes)) { dialogInterface, _ ->
            ProductsFragment().showProgressDialog(ProductsFragment().resources.getString(R.string.please_wait))
            viewModel.deleteProduct(ProductsFragment(), productID)
            dialogInterface.dismiss()
        }
        builder.setNegativeButton(ProductsFragment().resources.getString(R.string.no)) { dialogInterface, _ ->
            dialogInterface.dismiss()
        }
        val alertDialog: AlertDialog = builder.create()
        alertDialog.setCancelable(false)
        alertDialog.show()
    }
}