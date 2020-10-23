package com.example.vickychat.views

import com.example.vickychat.R
import com.example.vickychat.models.ChatMessage
import com.example.vickychat.models.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.latest_message_row.view.*

 class LatestMessageRow (val chatMessage: ChatMessage): Item<ViewHolder>() {
     var chatPartnerUser: User? = null

     override fun bind(viewHolder: ViewHolder, position: Int) {
         viewHolder.itemView.Notification_latest_message_row.text = chatMessage.text

         val chatPartnerId: String
         if (chatMessage.fromId == FirebaseAuth.getInstance().uid) {
             chatPartnerId = chatMessage.toId
         } else {
             chatPartnerId = chatMessage.fromId
         }

         val ref = FirebaseDatabase.getInstance().getReference("/users/$chatPartnerId")
         ref.addListenerForSingleValueEvent(object : ValueEventListener {
             override fun onDataChange(snapshot: DataSnapshot) {




                 chatPartnerUser = snapshot.getValue(User::class.java)
                 viewHolder.itemView.Username_latest_message_row.text = chatPartnerUser?.username

                 val targetImageView = viewHolder.itemView.imageView_latest_message_row
                 Picasso.get().load(chatPartnerUser?.profileImageUrl).into(targetImageView)
             }

             override fun onCancelled(error: DatabaseError) {

             }


         })
     }

     override fun getLayout(): Int {
         return R.layout.latest_message_row
     }


 }