package com.example.designersstore.presentation.ui.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.designersstore.R
import com.example.designersstore.domain.models.Product
import com.example.designersstore.presentation.ui.activityclient.ProductDetailsActivity
import com.example.designersstore.presentation.utils.Constants
import com.example.designersstore.presentation.utils.GlideLoader
import kotlinx.android.synthetic.main.item_dashboard_layout.view.*
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView


open class DashboardItemsListAdapter(
    private val context: Context,
    private var list: ArrayList<Product>
) : RecyclerView.Adapter<DashboardItemsListAdapter.UserViewHolder>() {

    private var onClickListener:OnClickListener?=null

    // method for filtering our recyclerview items.
    fun filterList(filterlist: ArrayList<Product>) {
        list = filterlist
        notifyDataSetChanged()
    }

    fun setList(productList:ArrayList<Product>){
        this.list=productList
        notifyDataSetChanged()
    }

    inner class UserViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        var tv_ItemTitle: TextView = itemView.findViewById(R.id.tv_dashboard_item_title)
        var tv_ItemPrice: TextView = itemView.findViewById(R.id.tv_dashboard_item_price)

        fun bind(product: Product){

            GlideLoader(context).loadProductPicture(
                product.image,
                itemView.iv_dashboard_item_image
            )
            tv_ItemTitle.text=product.title
            tv_ItemPrice.text="$${product.price}"

            itemView.setOnClickListener {
                val intent= Intent(context, ProductDetailsActivity::class.java)
                intent.putExtra(Constants.EXTRA_PRODUCT_ID,product.product_id)
                intent.putExtra(Constants.EXTRA_PRODUCT_OWNER_ID,product.user_id)
                context.startActivity(intent)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
//        return MyViewHolder(
//            LayoutInflater.from(context).inflate(
//                R.layout.item_dashboard_layout,
//                parent,
//                false
//            )
//        )

        var view:View= LayoutInflater.from(parent.context).inflate(R.layout.item_dashboard_layout,parent,false)

        return UserViewHolder(view)
    }

    fun setOnClickListener(onClickListener: OnClickListener){
        this.onClickListener=onClickListener
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder:UserViewHolder, position: Int) {
//        val model = list[position]
//
//        if (holder is MyViewHolder) {
//
//            GlideLoader(context).loadProductPicture(
//                model.image,
//                holder.itemView.iv_dashboard_item_image
//            )
//            holder.itemView.tv_dashboard_item_title.text = model.title
//            holder.itemView.tv_dashboard_item_price.text = "$${model.price}"
//
//            holder.itemView.setOnClickListener {
//                val intent= Intent(context, ProductDetailsActivity::class.java)
//                intent.putExtra(Constants.EXTRA_PRODUCT_ID,model.product_id)
//                intent.putExtra(Constants.EXTRA_PRODUCT_OWNER_ID,model.user_id)
//                context.startActivity(intent)
////                if (onClickListener != null ){
////                    if (onClickListener != null){
////                        onClickListener!!.onClick(position,model)
////                    }
////                }
//            }
//        }

        var product: Product =list.get(position)
        holder.bind(product)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    class MyViewHolder(view: View) : RecyclerView.ViewHolder(view)

    interface OnClickListener{
        fun onClick(position: Int,product: Product)
    }


}