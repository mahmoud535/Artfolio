package com.example.designersstore.presentation.ui.activityclient.registerclient

import android.app.Application
import android.content.Context
import androidx.databinding.ObservableField
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.designersstore.R
import com.example.designersstore.data.userfirestore.UserFirestoreViewModel
import com.example.designersstore.domain.models.User
import com.example.designersstore.presentation.ui.activity.BaseActivity
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.android.synthetic.main.activity_register__client_.*

class RegisterClientViewModel(application: Application, base: BaseActivity):AndroidViewModel(application) {

    private var infoMutableLiveData = MutableLiveData<List<User>>()
    val infoLiveData: LiveData<List<User>> get() = infoMutableLiveData
    private val viewModel: UserFirestoreViewModel = UserFirestoreViewModel()
    var viewModelRegister = viewModel
    var baseActivity:BaseActivity = base
    init {
        this.baseActivity = base
    }

    fun registerUser(email:String, password:String,user: User) {
        var firstName:String = String()
        var laseName:String = String()
        var emaile:String = String()
        var user = user
            //create an instance and create are register a user with email and password
            FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(
                    OnCompleteListener<AuthResult> { task ->
                        if (task.isSuccessful) {
                            // Firebase register user
                            val firebaseUser: FirebaseUser = task.result!!.user!!
                            user = User(
                                firebaseUser.uid,
                                firstName,
                                laseName,
                                emaile
                            )
                           // viewModelRegister.registerUser(context,user)
                        } else {

                            baseActivity. hideProgressDialog()
                            baseActivity.showErrorSnackBar(task.exception!!.message.toString(), true)
                        }
                    })

    }
}