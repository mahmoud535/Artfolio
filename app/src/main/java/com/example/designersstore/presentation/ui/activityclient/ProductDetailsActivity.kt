package com.example.designersstore.presentation.ui.activityclient

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import com.example.designersstore.R
import com.example.designersstore.data.designerfirestore.DesignerFirestore
import com.example.designersstore.data.userfirestore.UserFireStore
import com.example.designersstore.data.userfirestore.UserFirestoreViewModel
import com.example.designersstore.databinding.ActivityProductDetailsBinding
import com.example.designersstore.domain.models.CartItem
import com.example.designersstore.domain.models.Product
import com.example.designersstore.domain.models.newmodel.NewUser
import com.example.designersstore.presentation.ui.activity.BaseActivity
import com.example.designersstore.presentation.ui.activityclient.cartlist.CartListActivity
import com.example.designersstore.presentation.ui.fragmentsclient.favoritelist.FavoriteListViewModel
import com.example.designersstore.presentation.utils.Constants
import com.example.designersstore.presentation.utils.GlideLoader
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class ProductDetailsActivity : BaseActivity() {

    private val viewModel: UserFirestoreViewModel by viewModels()
    private val favoriteViewModel: FavoriteListViewModel by viewModels()
    lateinit var binding: ActivityProductDetailsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_product_details)
        binding = ActivityProductDetailsBinding.inflate(layoutInflater)
        GlobalScope.launch(Dispatchers.Main) {
            actions()
        }
        setContentView(binding.root)
    }

    private fun actions() {
        GlobalScope.launch(Dispatchers.Main) {
            setupActionBar()
            getExtra()
            showAddToCartBtn()
            showFavoriteBtn()
            getProductDetails()
            setUpView()
            addToFavList()
            goToChat()
        }
    }

    private var mFavoriteOwnerId: String = ""
    private fun showFavoriteBtn() {
        if (DesignerFirestore().getCurrentDesignerID() == mFavoriteOwnerId) {
            binding.addFavorite.visibility = View.GONE
            binding.favorite.visibility = View.GONE
        } else {
            binding.addFavorite.visibility = View.VISIBLE
        }
    }

     fun showAddToCartBtn() {
        GlobalScope.launch(Dispatchers.Main) {
            GlobalScope.launch(Dispatchers.Main) {
                if (DesignerFirestore().getCurrentDesignerID() == mProductOwnerId) {
                    binding.btnAddToCart.visibility = View.GONE
                    binding.btnGoToCart.visibility = View.GONE
                } else {
                    binding.btnAddToCart.visibility = View.VISIBLE
                }
            }
        }
    }

    private var mProductId: String = ""
    private fun getExtra() {
        GlobalScope.launch(Dispatchers.Main) {
            if (intent.hasExtra(Constants.EXTRA_PRODUCT_ID)) {
                mProductId = intent.getStringExtra(Constants.EXTRA_PRODUCT_ID)!!
            }
            if (intent.hasExtra(Constants.EXTRA_PRODUCT_OWNER_ID)) {
                mProductOwnerId = intent.getStringExtra(Constants.EXTRA_PRODUCT_OWNER_ID)!!
            }
        }
    }

    private fun getProductDetails() {
        GlobalScope.launch(Dispatchers.Main) {
            showProgressDialog(resources.getString(R.string.please_wait))
            viewModel.getProductDetails(this@ProductDetailsActivity, mProductId)
        }
    }

    fun productExistsInCart() {
        hideProgressDialog()
        addToCartVisibility()
    }

    fun favoriteProductExistInCart() {
        hideProgressDialog()
        addToFavoriteVisibility()
    }

    private lateinit var mProductDetails: Product
    fun productDetailsSuccess(product: Product) {
        mProductDetails = product
        setProductInfo(product)
        if (product.stock_quantity.toInt() == 0) {
            hideProgressDialog()
            binding.btnAddToCart.visibility = View.GONE
            binding.tvProductDetailsAvailableQuantity.text =
                resources.getString(R.string.lbl_out_of_stock)
            binding.tvProductDetailsAvailableQuantity.setTextColor(
                ContextCompat.getColor(
                    this@ProductDetailsActivity,
                    R.color.colorSnackBarError
                )
            )
        } else {
            checkIfItemExistInCart(product)
        }

    }

    private fun checkIfItemExistInCart(product: Product) {
        mProductDetails = product
        if (viewModel.getCurrentUserID().toString() == product.user_id) {
            hideProgressDialog()
        } else {
            viewModel.checkIfItemExistInCart(this@ProductDetailsActivity, mProductId)
        }
    }

    private fun setProductInfo(product: Product) {
        mProductDetails = product
        GlideLoader(this@ProductDetailsActivity).loadProductPicture(
            product.image,
            binding.ivProductDetailImage
        )
        binding.tvProductDetailsTitle.text = product.title
        binding.tvProductDetailsPrice.text = "$${product.price}"
        binding.tvProductDetailsDescription.text = product.description
        binding.tvProductDetailsAvailableQuantity.text = product.stock_quantity
    }

    fun favoriteProductDetailsSuccess(product: Product) {
        mProductDetails = product
        if (viewModel.getCurrentUserID().toString() == product.user_id) {
            hideProgressDialog()
        } else {
            viewModel.checkIfFavoriteItemExitInCat(this@ProductDetailsActivity, mProductId)
        }
    }

    private fun setupActionBar() {
        GlobalScope.launch(Dispatchers.Main) {
            setSupportActionBar(binding.toolbarProductDetailsActivity)
            val actionBar = supportActionBar
            if (actionBar != null) {
                actionBar.setDisplayHomeAsUpEnabled(true)
                actionBar.setHomeAsUpIndicator(R.drawable.ic_white_color_back_24dp)
            }
            binding.toolbarProductDetailsActivity.setNavigationOnClickListener { onBackPressed() }
        }
    }

    private fun addToCart() {
        val cartItem = cartItem()
        showProgressDialog(resources.getString(R.string.please_wait))
        viewModel.addCartItems(this, cartItem)
    }

    private fun addToFavorite() {
        val favoriteItem = cartItem()
        showProgressDialog(resources.getString(R.string.please_wait))
        favoriteViewModel.addFavoriteItems(this, favoriteItem)
    }

    private var mProductOwnerId: String = ""
    private fun cartItem() = CartItem(
        UserFireStore().getCurrentUserID(),
        mProductOwnerId,
        mProductId,
        mProductDetails.title,
        mProductDetails.price,
        mProductDetails.image,
        Constants.DEFAULT_Fav_QUANTITY
    )

    fun addToCartSuccess() {
        hideProgressDialog()
        Toast.makeText(
            this@ProductDetailsActivity,
            resources.getString(R.string.success_message_item_added_to_cart),
            Toast.LENGTH_SHORT
        ).show()
        addToCartVisibility()
    }

    fun addFavoriteListSuccess() {
        hideProgressDialog()
        Toast.makeText(
            this@ProductDetailsActivity,
            resources.getString(R.string.Design_AddedTo_Favorite),
            Toast.LENGTH_SHORT
        ).show()
        addToFavoriteVisibility()
    }

    private fun addToCartVisibility() {
        binding.btnAddToCart.visibility = View.GONE
        binding.btnGoToCart.visibility = View.VISIBLE
    }

    private fun addToFavoriteVisibility() {
        binding.addFavorite.visibility = View.GONE
        binding.favorite.visibility = View.VISIBLE
    }

    private fun setUpView() {
        GlobalScope.launch(Dispatchers.Main) {
            binding.btnAddToCart.setOnClickListener {
                addToCart()
            }
            binding.btnGoToCart.setOnClickListener {
                startActivity(Intent(this@ProductDetailsActivity, CartListActivity::class.java))
            }
        }
    }

    private fun addToFavList() {
        GlobalScope.launch(Dispatchers.Main) {
            viewModel.getProductDetails(this@ProductDetailsActivity, mProductId)
            binding.addFavorite.setOnClickListener {
                addToFavorite()
            }
        }
    }

    private fun goToChat() {
        binding.btnChat.setOnClickListener {
            val user =
                NewUser(
                    mProductDetails.user_id,
                    mProductDetails.user_name,
                    mProductDetails.image
                )
            showProgressDialog(resources.getString(R.string.please_wait))
            saveUserToFirebaseDatabase(user)
        }
    }

    private fun saveUserToFirebaseDatabase(user: NewUser) {
        val uid = FirebaseAuth.getInstance().uid ?: return
        val ref = FirebaseDatabase.getInstance().getReference("/users/$uid")
        ref.setValue(user)
            .addOnSuccessListener {
                hideProgressDialog()
                //TODO: FOR the Chat**************
//                val intent = Intent(this@ProductDetailsActivity, ActivityChatLogDesginer::class.java)
//                intent.putExtra(ActivityDesignerNewMessages.USER_KEY, user)
                startActivity(intent)
                finish()
            }
            .addOnFailureListener {
                Log.d("TAG", "Failed to set value to database: ${it.message}")
            }
    }
}