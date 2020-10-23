package com.example.vickychat.messages


//import com.example.vickychat.ChatFromItem
//import com.example.vickychat.ChatToItem
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.example.vickychat.R
import com.example.vickychat.models.ChatMessage
import com.example.vickychat.models.User
import com.example.vickychat.views.ChatFromItem
import com.example.vickychat.views.ChatToItem
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.activity_chat_log.*


class ChatLogActivity : AppCompatActivity() {

    companion object {
        val TAG = "ChatLog"
    }

    val adapter = GroupAdapter<ViewHolder>()

    var toUser: User? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat_log)

        recyclerview_chat_log.adapter = adapter
        //supportActionBar?.title = "Chat Screen"

        //val username = intent.getStringExtra(NewMessageActivity.USER_KEY)
        toUser = intent.getParcelableExtra<User>(NewMessageActivity.USER_KEY)
        supportActionBar?.title = toUser?.username


        //setupDummyData()
        listenForMessages()
        send_button_chatlog.setOnClickListener {
            Log.d(TAG, "Attempt to send message...")
            performSendMessage()
        }
    }


    private fun listenForMessages() {
        val fromId = FirebaseAuth.getInstance().uid
        val toId = toUser?.uid
        val ref = FirebaseDatabase.getInstance().getReference("/user-messages/$fromId/$toId")



        ref.addChildEventListener(object : ChildEventListener {

            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {

                val chatMessage = snapshot.getValue(ChatMessage::class.java)

                if (chatMessage != null) {
                    Log.d(TAG, chatMessage.text)

                    if (chatMessage.fromId == FirebaseAuth.getInstance().uid) {
                        val currentUser = LatestMessagesActivity.currentUser ?: return
                        adapter.add(ChatFromItem(chatMessage.text, currentUser))

                    } else {
                        adapter.add(ChatToItem(chatMessage.text, toUser!!))


                    }
                }
                recyclerview_chat_log.scrollToPosition(adapter.itemCount - 1)
            }

            override fun onCancelled(error: DatabaseError) {

            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {

            }

            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {

            }

            override fun onChildRemoved(snapshot: DataSnapshot) {

            }


        })
    }


    private fun performSendMessage() {


        val text = TypeMessage_editText_chatlog.text.toString()

        val fromId = FirebaseAuth.getInstance().uid
        val user = intent.getParcelableExtra<User>(NewMessageActivity.USER_KEY)
        val toId = user.uid

        if (fromId == null) return

        //val reference = FirebaseDatabase.getInstance().getReference("/messages").push()

        val reference = FirebaseDatabase.getInstance().getReference("/user-messages/$toId/$fromId").push()

        val toReference = FirebaseDatabase.getInstance().getReference("/user-messages/$fromId/$toId").push()


        val chatMessage = ChatMessage(reference.key!!, text, fromId, toId, System.currentTimeMillis() / 1000)

        //reference.setValue(chatMessage)


        reference.setValue(chatMessage)
            .addOnSuccessListener {
                Log.d(TAG, "Saved our chat message: ${reference.key}")
                TypeMessage_editText_chatlog.text.clear()
                recyclerview_chat_log.scrollToPosition(adapter.itemCount - 1)

            }
        toReference.setValue(chatMessage)

        val latestMessageRef =  FirebaseDatabase.getInstance().getReference("/latest-messages/$fromId/$toId")

        latestMessageRef.setValue(chatMessage)

        val latestMessageToRef = FirebaseDatabase.getInstance().getReference("/latest-messages/$toId/$fromId")

        latestMessageToRef.setValue(chatMessage)

    }
}


  //  private fun setupDummyData() {
   //     val adapter = GroupAdapter<ViewHolder>()

     //   adapter.add(ChatFromItem("FROM MESSSSSSSSAAGE"))
       // adapter.add(ChatToItem("TO MESSAGE\nTOMESSAGE"))
        //adapter.add(ChatFromItem("FROM MESSSSSSSSAAGE"))
        //adapter.add(ChatToItem("TO MESSAGE\nTOMESSAGE"))
        //adapter.add(ChatFromItem("FROM MESSSSSSSSAAGE"))
        //adapter.add(ChatToItem("TO MESSAGE\nTOMESSAGE"))

        //recyclerview_chat_log.adapter = adapter
    //}

//}

//class ChatFromItem(val text: String): Item<ViewHolder>() {
    //override fun bind(viewHolder: ViewHolder, position: Int) {
      //  viewHolder.itemView.textView_from_row.text = text
    //}

    //override fun getLayout(): Int {
        //return R.layout.chat_from_row
    //}
//}

//class ChatToItem(val text: String): Item<ViewHolder>() {
    //override fun bind(viewHolder: ViewHolder, position: Int) {
        //viewHolder.itemView.textView_to_row.text = text
    //}

    //override fun getLayout(): Int {
      //  return R.layout.chat_to_row
    //}
//}





