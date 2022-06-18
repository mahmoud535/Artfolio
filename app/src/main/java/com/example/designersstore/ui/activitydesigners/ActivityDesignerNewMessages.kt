package com.example.designersstore.ui.activitydesigners

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.example.designersstore.models.newmodel.NewUser
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.designersstore.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.activity_designer_new_messages.*
import kotlinx.android.synthetic.main.activity_new_messages.*
import kotlinx.android.synthetic.main.user_row_new_message.view.*

class ActivityDesignerNewMessages : AppCompatActivity() {

    companion object {


        const val USER_KEY = "USER_KEY"
        private val TAG = ActivityDesignerNewMessages::class.java.simpleName
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_designer_new_messages)

        fetchUsers()
    }

    private fun fetchUsers() {

        val ref = FirebaseDatabase.getInstance().getReference("/users")
        ref.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(databaseError: DatabaseError) {

            }

            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val adapter = GroupAdapter<ViewHolder>()

                dataSnapshot.children.forEach {
                    Log.d(ActivityDesignerNewMessages.TAG, it.toString())
                    @Suppress("NestedLambdaShadowedImplicitParameter")
                    it.getValue(NewUser::class.java)?.let {
                        if (it.uid != FirebaseAuth.getInstance().uid) {
                            adapter.add(UserItem(it, this@ActivityDesignerNewMessages))
                        }
                    }
                }

                adapter.setOnItemClickListener { item, view ->
                    val userItem = item as UserItem
                    val intent = Intent(view.context, ActivityChatLogDesginer::class.java)
                    intent.putExtra(USER_KEY, userItem.user)
                    startActivity(intent)
                    finish()
                }

                recyclerview_newmessage_desginer.adapter = adapter
            }

        })
    }
}

class UserItem(val user: NewUser, val context: Context) : Item<ViewHolder>() {

    override fun bind(viewHolder: ViewHolder, position: Int) {
        viewHolder.itemView.username_textview_new_message.text = user.name
        val requestOptions = RequestOptions().placeholder(R.drawable.ic_launcher_background)

        Glide.with(viewHolder.itemView.imageview_new_message.context)
            .load(user.profileImageUrl)
            .apply(requestOptions)
            .into(viewHolder.itemView.imageview_new_message)

    }

    override fun getLayout(): Int {
        return R.layout.user_row_new_message
    }


}