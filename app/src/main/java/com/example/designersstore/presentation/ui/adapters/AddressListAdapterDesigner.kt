package com.example.designersstore.presentation.ui.adapters

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.designersstore.R
import com.example.designersstore.domain.models.AddressClient
import com.example.designersstore.presentation.ui.activitydesigners.AddEditAddressDesigners
import com.example.designersstore.presentation.utils.Constants
import kotlinx.android.synthetic.main.item_address_layout.view.*


open class AddressListAdapterDesigner(
    private val context: Context,
    private var list: ArrayList<AddressClient>,
    private var selectAddress:Boolean
): RecyclerView.Adapter<RecyclerView.ViewHolder>(){
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return MyViewHolder(
            LayoutInflater.from(context).inflate(
                R.layout.item_address_layout,
                parent,
                false
            )
        )
    }

    fun notifyEditItem(activity: Activity, position: Int){
        val intent= Intent(context, AddEditAddressDesigners::class.java)
        intent.putExtra(Constants.EXTRA_ADDRESS_DETAILS_DESIGNER,list[position])
        activity.startActivityForResult(intent, Constants.ADD_ADDRESS_REQUEST_CODE_CLIENT)
        notifyItemChanged(position)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val model = list[position]
        if (holder is MyViewHolder) {
            holder.itemView.tv_address_full_name.text = model.name
            holder.itemView.tv_address_type.text = model.type
            holder.itemView.tv_address_details.text = "${model.address}, ${model.zipCode}"
            holder.itemView.tv_address_mobile_number.text = model.mobileNumber

        }
    }

    override fun getItemCount(): Int {
        return list.size
    }

    private class MyViewHolder(view: View) : RecyclerView.ViewHolder(view)
}