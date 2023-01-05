package com.example.designersstore.presentation.ui.fragmentsclient.favoritelist.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.designersstore.R
import com.example.designersstore.domain.models.CartItem
import com.example.designersstore.presentation.ui.fragmentsclient.favoritelist.FavoriteListFragment
import com.example.designersstore.presentation.utils.GlideLoader
import kotlinx.android.synthetic.main.item_list_layout.view.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

open class FavListAdapter(
    private val context: Context,
    private val fragment: FavoriteListFragment
) :RecyclerView.Adapter<FavListAdapter.FavoriteVewHolder>(){

    var onListItemClick: OnListItemClick? = null
    var favoriteList:ArrayList<CartItem> = ArrayList()

    fun setList(favoriteList: ArrayList<CartItem>){
        this.favoriteList = favoriteList
        notifyDataSetChanged()
    }

    inner class FavoriteVewHolder(itemView: View) :RecyclerView.ViewHolder(itemView) {
        var ivProductName:TextView = itemView.findViewById(R.id.tv_item_name)
        var ivProductPrice:TextView = itemView.findViewById(R.id.tv_item_price)
        var deleteItem:ImageButton = itemView.findViewById(R.id.ib_delete_product)
        fun bind(product:CartItem){
            GlideLoader(context).loadProductPicture(
                product.image,
                itemView.iv_item_image
            )
            ivProductName.text = product.title
            ivProductPrice.text =  product.price

            itemView.setOnClickListener {
                onListItemClick?.onItemClick(product)
            }
            deleteItem.visibility = View.VISIBLE
            deleteItem.setOnClickListener {
//                GlobalScope.launch(Dispatchers.Main) {   FavoriteListViewModel().deleteProduct(fragment,product.id)
//                    delay(5000)
//                }
                GlobalScope.launch(Dispatchers.Main) {  FavoriteListFragment().deleteProduct(product.id)}
            }
        }


    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FavoriteVewHolder {
        var view:View = LayoutInflater.from(parent.context).inflate(R.layout.item_list_layout,parent,false)
        return  FavoriteVewHolder(view)
    }

    override fun onBindViewHolder(holder: FavoriteVewHolder, position: Int) {
       var product: CartItem = favoriteList.get(position)

        holder.bind(product)
    }


    override fun getItemCount(): Int {
        return favoriteList.size
    }

}