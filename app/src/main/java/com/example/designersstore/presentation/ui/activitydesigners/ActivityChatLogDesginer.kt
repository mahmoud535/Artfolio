package com.example.designersstore.presentation.ui.activitydesigners

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import com.example.designersstore.domain.models.newmodel.ChatMessage
import com.example.designersstore.domain.models.newmodel.NewUser
import com.example.designersstore.presentation.utils.DateUtils
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.designersstore.R
import com.example.designersstore.presentation.ui.activityclient.ActivityChatLogClient
import com.example.designersstore.presentation.ui.fragmentsclient.ChatFragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.activity_chat_log_client.*
import kotlinx.android.synthetic.main.activity_chat_log_client.swiperefresh
import kotlinx.android.synthetic.main.activity_chat_log_desginer.*
import kotlinx.android.synthetic.main.chat_from_row.view.*
import kotlinx.android.synthetic.main.chat_to_row.view.*

class ActivityChatLogDesginer : AppCompatActivity() {
    companion object {
        val TAG = ActivityChatLogDesginer::class.java.simpleName
    }
    val adapter = GroupAdapter<ViewHolder>()

    // Bundle Data
    private val toUser: NewUser?
        get() = intent.getParcelableExtra(ActivityDesignerNewMessages.USER_KEY)



    /**you need to get current user **/
    private fun fetchCurrentUser() {
        val uid = FirebaseAuth.getInstance().uid ?: return
        val ref = FirebaseDatabase.getInstance().getReference("/users/$uid")
        ref.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(databaseError: DatabaseError) {
            }

            override fun onDataChange(dataSnapshot: DataSnapshot) {
                ChatFragment.currentUser = dataSnapshot.getValue(NewUser::class.java)
            }

        })
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat_log_desginer)

    //    fetchCurrentUser()

        recyclerview_chat_log_designer.adapter = adapter

        supportActionBar?.title = toUser?.name

        listenForMessages()

        /*this happen when partner user was deleted**/
        if(toUser==null) buttons_container.visibility = View.INVISIBLE

        send_button_chat_log_des.setOnClickListener {
            performSendMessage()
        }



//        swiperefresh.setOnRefreshListener {
//            fetchCurrentUser()
//        }
    }

    private fun listenForMessages() {
        swiperefresh.isEnabled = true
        swiperefresh.isRefreshing = true

        val fromId = FirebaseAuth.getInstance().uid ?: return
        val toId = toUser?.uid
        val ref = FirebaseDatabase.getInstance().getReference("/user-messages/$fromId/$toId")

        Log.e("ActivityDesignchat" , "from $fromId to $toId")

        ref.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(databaseError: DatabaseError) {
                //Log.d(ActivityChatLogClient.TAG, "database error: " + databaseError.message)
            }

            override fun onDataChange(dataSnapshot: DataSnapshot) {
                //Log.d(ActivityChatLogClient.TAG, "has children: " + dataSnapshot.hasChildren())
                if (!dataSnapshot.hasChildren()) {
                    swiperefresh.isRefreshing = false
                    swiperefresh.isEnabled = false
                }
            }
        })

        ref.addChildEventListener(object : ChildEventListener {
            override fun onCancelled(databaseError: DatabaseError) {
            }

            override fun onChildMoved(dataSnapshot: DataSnapshot, previousChildName: String?) {
            }

            override fun onChildChanged(dataSnapshot: DataSnapshot, previousChildName: String?) {
            }

            override fun onChildAdded(dataSnapshot: DataSnapshot, previousChildName: String?) {
                dataSnapshot.getValue(ChatMessage::class.java)?.let {
                    Log.e("ActivityDesignchat","on add ${it.fromId == FirebaseAuth.getInstance().uid} => ${dataSnapshot.value}")

                    if (it.fromId == FirebaseAuth.getInstance().uid) {
                        val currentUser = ChatFragment.currentUser ?: return
                        //   Log.e("ActivityDesignchat","on current ${currentUser}")
                        //
                        adapter.add(ChatFromItem(it.text, toUser!!, it.timestamp))
                    } else {
                        adapter.add(ChatToItem(it.text, toUser!!, it.timestamp))
                    }
                }
                recyclerview_chat_log_designer.scrollToPosition(adapter.itemCount - 1)
                swiperefresh.isRefreshing = false
                swiperefresh.isEnabled = false
            }

            override fun onChildRemoved(dataSnapshot: DataSnapshot) {
            }

        })

    }

    private fun performSendMessage() {
        val text = edittext_chat_log_des.text.toString()
        if (text.isEmpty()) {
            Toast.makeText(this, "Message cannot be empty", Toast.LENGTH_SHORT).show()
            return
        }

        val fromId = FirebaseAuth.getInstance().uid ?: return
        val toId = toUser?.uid

        val reference = FirebaseDatabase.getInstance().getReference("/user-messages/$fromId/$toId").push()
        val toReference = FirebaseDatabase.getInstance().getReference("/user-messages/$toId/$fromId").push()

        try{

            Log.e("ActivityDesignchat" , "to user ${toUser?.uid} , key ${fromId}")

            val chatMessage = ChatMessage(reference.key!!, text, fromId, toId!!, System.currentTimeMillis() / 1000)
            reference.setValue(chatMessage)
                .addOnSuccessListener {
                    Log.d(ActivityChatLogClient.TAG, "Saved our chat message: ${reference.key}")
                    edittext_chat_log_des.text.clear()

                    if(adapter.itemCount >1) {
                        recyclerview_chat_log_designer.smoothScrollToPosition(adapter.itemCount - 1)
                    }
                }

            toReference.setValue(chatMessage)


            val latestMessageRef = FirebaseDatabase.getInstance().getReference("/latest-messages/$fromId/$toId")
            latestMessageRef.setValue(chatMessage)

            val latestMessageToRef = FirebaseDatabase.getInstance().getReference("/latest-messages/$toId/$fromId")
            latestMessageToRef.setValue(chatMessage)
        }catch (e:Exception){
            e.printStackTrace()
        }
    }
}

class ChatFromItem(val text: String, val user: NewUser, val timestamp: Long) : Item<ViewHolder>() {

    override fun bind(viewHolder: ViewHolder, position: Int) {

        viewHolder.itemView.textview_from_row.text = text
        viewHolder.itemView.from_msg_time.text = DateUtils.getFormattedTimeChatLog(timestamp)

        val targetImageView = viewHolder.itemView.imageview_chat_from_row

        if (!user.profileImageUrl!!.isEmpty()) {

            val requestOptions = RequestOptions().placeholder(com.google.firebase.database.R.drawable.notification_bg)


            Glide.with(targetImageView.context)
                .load(user.profileImageUrl)
                .thumbnail(0.1f)
                .apply(requestOptions)
                .into(targetImageView)

        }
    }

    override fun getLayout(): Int {
        return R.layout.chat_from_row
    }

}

class ChatToItem(val text: String, val user: NewUser, val timestamp: Long) : Item<ViewHolder>() {

    override fun bind(viewHolder: ViewHolder, position: Int) {
        viewHolder.itemView.textview_to_row.text = text
        viewHolder.itemView.to_msg_time.text = DateUtils.getFormattedTimeChatLog(timestamp)

        val targetImageView = viewHolder.itemView.imageview_chat_to_row

        if (!user.profileImageUrl!!.isEmpty()) {

            val requestOptions = RequestOptions().placeholder(com.google.firebase.database.R.drawable.notification_icon_background)


            Glide.with(targetImageView.context)
                .load(user.profileImageUrl)
                .thumbnail(0.1f)
                .apply(requestOptions)
                .into(targetImageView)

        }
    }

    override fun getLayout(): Int {
        return R.layout.chat_to_row
    }
}