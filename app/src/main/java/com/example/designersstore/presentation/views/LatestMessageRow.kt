package com.example.designersstore.presentation.views

import android.content.Context
import android.util.Log

import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.designersstore.R
import com.example.designersstore.domain.models.newmodel.ChatMessage
import com.example.designersstore.domain.models.newmodel.NewUser
import com.example.designersstore.presentation.utils.DateUtils
import com.xwray.groupie.Item
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.latest_message_row.view.*



class LatestMessageRow(val chatMessage: ChatMessage, val context: Context) : Item<ViewHolder>() {

    var chatPartnerUser: NewUser? = null

    fun partnerUserIsKnown():Boolean{
        return chatPartnerUser !=null
    }
    override fun getLayout(): Int {
        return R.layout.latest_message_row
    }

    override fun bind(viewHolder: ViewHolder, position: Int) {
        viewHolder.itemView.latest_message_textview.text = chatMessage.text

        val chatPartnerId: String
        Log.e("ChatDesignerFragment" , "current ${ FirebaseAuth.getInstance().uid}")
        if (chatMessage.fromId == FirebaseAuth.getInstance().uid) {
            chatPartnerId = chatMessage.toId
        } else {
            chatPartnerId = chatMessage.fromId
        }

        val ref = FirebaseDatabase.getInstance().getReference("/users/$chatPartnerId")
        ref.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {

            }

            override fun onDataChange(p0: DataSnapshot) {
                chatPartnerUser = p0.getValue(NewUser::class.java)
                Log.e("ChatFragment Row", "chat user change ${p0.value}")

                /**Detect if message user already included in users and set data
                 *  else set user name with unknown user and disable item*/
                if (chatPartnerUser != null) {
                    viewHolder.itemView.username_textview_latest_message.text =
                        chatPartnerUser?.name
                    viewHolder.itemView.latest_msg_time.text =
                        DateUtils.getFormattedTime(chatMessage.timestamp)

                    val requestOptions =
                        RequestOptions().placeholder(R.drawable.ic_launcher_background)

                    Glide.with(viewHolder.itemView.imageview_latest_message.context)
                        .load(chatPartnerUser?.profileImageUrl)
                        .apply(requestOptions)
                        .into(viewHolder.itemView.imageview_latest_message)

                }
            }

        })

    }

}