package com.example.designersstore.ui.fragmentsdesigners

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.designersstore.R
import com.example.designersstore.models.newmodel.ChatMessage
import com.example.designersstore.models.newmodel.NewUser
import com.example.designersstore.ui.activitydesigners.ActivityChatLogDesginer
import com.example.designersstore.ui.activitydesigners.ActivityDesignerNewMessages
import com.example.designersstore.ui.activitydesigners.ActivityDesignerNewMessages.Companion.USER_KEY
import com.example.designersstore.views.LatestMessageRow
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.fragment_chat_designers.*



class ChatDesignerFragment : Fragment() {

 // private lateinit var homeViewModel: HomeViewModel
 private val adapter = GroupAdapter<ViewHolder>()
  private val latestMessagesMap = HashMap<String, ChatMessage>()

  companion object {
    var currentUser: NewUser? = null
    // val TAG = ChatDesignerFragment::class.java.simpleName
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    designer_recyclerview_latest_messages.adapter = adapter


    fetchCurrentUser()
    listenForLatestMessages()

    adapter.setOnItemClickListener { item, _ ->
      val intent = Intent(requireContext(), ActivityChatLogDesginer::class.java)
      val row = item as LatestMessageRow
      intent.putExtra(USER_KEY, row.chatPartnerUser)
      startActivity(intent)
    }


//    new_message_fab_desgner.setOnClickListener {
//      val intent = Intent(requireContext(), ActivityDesignerNewMessages::class.java)
//      startActivity(intent)
//    }

    swiperefresh.setOnRefreshListener {
      fetchCurrentUser()
      listenForLatestMessages()
    }
  }


  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View? {
//    homeViewModel =
//            ViewModelProvider(this).get(HomeViewModel::class.java)
    val root = inflater.inflate(R.layout.fragment_chat_designers, container, false)

    return root
  }


  private fun refreshRecyclerViewMessages() {
    adapter.clear()
    latestMessagesMap.values.forEach {
      adapter.add(LatestMessageRow(it, requireContext()))
    }
    swiperefresh.isRefreshing = false
  }

  private fun listenForLatestMessages() {
    swiperefresh.isRefreshing = true
    val fromId = FirebaseAuth.getInstance().uid ?: return
    val ref = FirebaseDatabase.getInstance().getReference("/latest-messages/$fromId")

    ref.addListenerForSingleValueEvent(object : ValueEventListener {
      override fun onCancelled(databaseError: DatabaseError) {
        //Log.d(ChatFragment.TAG, "database error: " + databaseError.message)
      }

      override fun onDataChange(dataSnapshot: DataSnapshot) {
        //Log.d(ChatFragment.TAG, "has children: " + dataSnapshot.hasChildren())
        if (!dataSnapshot.hasChildren()) {
          swiperefresh.isRefreshing = false
        }
      }

    })


    ref.addChildEventListener(object : ChildEventListener {
      override fun onCancelled(databaseError: DatabaseError) {
      }

      override fun onChildMoved(dataSnapshot: DataSnapshot, previousChildName: String?) {
      }

      override fun onChildChanged(dataSnapshot: DataSnapshot, previousChildName: String?) {
        dataSnapshot.getValue(ChatMessage::class.java)?.let {
          latestMessagesMap[dataSnapshot.key!!] = it
          refreshRecyclerViewMessages()
        }
      }

      override fun onChildAdded(dataSnapshot: DataSnapshot, previousChildName: String?) {
        dataSnapshot.getValue(ChatMessage::class.java)?.let {
          latestMessagesMap[dataSnapshot.key!!] = it
          refreshRecyclerViewMessages()
        }
      }

      override fun onChildRemoved(p0: DataSnapshot) {
      }

    })
  }

  private fun fetchCurrentUser() {
    val uid = FirebaseAuth.getInstance().uid ?: return
    val ref = FirebaseDatabase.getInstance().getReference("/users/$uid")
    ref.addListenerForSingleValueEvent(object : ValueEventListener {
      override fun onCancelled(databaseError: DatabaseError) {
      }

      override fun onDataChange(dataSnapshot: DataSnapshot) {
        currentUser = dataSnapshot.getValue(NewUser::class.java)
      }

    })
  }
}