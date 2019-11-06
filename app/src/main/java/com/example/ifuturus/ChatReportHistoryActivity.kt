package com.example.ifuturus

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.ifuturus.model.chatmodel
import com.example.ifuturus.model.userprofilemodel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.squareup.picasso.Picasso
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.activity_chat_report_history.*
import kotlinx.android.synthetic.main.recyclerview_chat_from_row.view.*
import kotlinx.android.synthetic.main.recyclerview_chat_to_row.view.*

class ChatReportHistoryActivity : AppCompatActivity() {

    private var reportID: String? = null

    // Firebase instance variables
    private lateinit var mFirebaseAuth: FirebaseAuth
    private var mFirebaseUser: FirebaseUser? = null
    private lateinit var referenceUserProfile: DatabaseReference

    // Store Temporary Value
    var mphotoUrl : String? = null

    companion object {
        const val TAG = "ChatLog"
    }

    val adapter = GroupAdapter<ViewHolder>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat_report_history)

        // Get current user
        mFirebaseAuth = FirebaseAuth.getInstance()
        mFirebaseUser = mFirebaseAuth.currentUser

        // Retrieve Data From Intent
        reportID = intent.getStringExtra(ChatReportListActivity.REPORT_ID_KEY)
        supportActionBar?.title = "Chat Report ID: $reportID"

    // Database User Profile Reference
        referenceUserProfile = FirebaseDatabase.getInstance().getReference("userprofile").child(mFirebaseUser!!.uid)


        referenceUserProfile.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
                // Handle Cancel Action
            }

            override fun onDataChange(dataSnapshot: DataSnapshot) {
                // Handle Data Change Action
                val user = dataSnapshot.getValue(userprofilemodel::class.java)
                // Assign Data to User Profile
                mphotoUrl = user?.photoUrl
            }
        })

        // Assign Chat Report History Recycler View
        chatHistoryRecyclerView.layoutManager = LinearLayoutManager(this)

        // Cache and Read Message from Firebase Database
        chatHistoryRecyclerView.adapter = adapter

        // Read Chat From Firebase Database
        listenForMessage()

        // Send Button OnClickListener
        chatHistorySendButton.setOnClickListener {
            Log.d(TAG, "Attempt Send Message")

            // Perform Send Message
            performSendMessage()
        }
    }

    private fun listenForMessage() {
        val ref = FirebaseDatabase.getInstance().getReference("chatreportmessages").child("$reportID")

        ref.addChildEventListener(object: ChildEventListener {
            override fun onChildAdded(p0: DataSnapshot, p1: String?) {
                val chatMessage = p0.getValue(chatmodel::class.java)

                if (chatMessage != null) {
                    Log.d(TAG, chatMessage.chatmessage)

                    if (chatMessage.userid == mFirebaseUser!!.uid) {
                        adapter.add(ChatToItem(chatMessage.chatmessage, chatMessage.photoUrl))
                    } else {
                        adapter.add(ChatFromItem(chatMessage.chatmessage, chatMessage.photoUrl))
                    }


                }
            }

            override fun onCancelled(p0: DatabaseError) {
            }

            override fun onChildChanged(p0: DataSnapshot, p1: String?) {
            }

            override fun onChildMoved(p0: DataSnapshot, p1: String?) {
            }

            override fun onChildRemoved(p0: DataSnapshot) {
            }

        })
    }

    private fun performSendMessage() {
        // Get Text from Edit Text
        val sendChatSendMessage = chatHistoryEditText.text.toString()

        Log.d("Chat Activity", "Inside Chat Message: $sendChatSendMessage")
        // If text is empty return
        if (sendChatSendMessage == "") {
            return
        }

        // Data to be saved to the database
        val timestamp: Long = System.currentTimeMillis()

        val reference = FirebaseDatabase.getInstance().getReference("chatreportmessages").child("$reportID").push()

        val chatMessage = chatmodel(
            mFirebaseUser!!.uid,
            reportID,
            sendChatSendMessage,
            timestamp,
            mphotoUrl
        )
        reference.setValue(chatMessage)
            .addOnSuccessListener {
                Log.d(TAG, "Successfully saved value to database")
                chatHistoryEditText.setText("")
            }
    }
}

class ChatFromItem(val chatMessage: String, val chatUserImage: String): Item<ViewHolder>() {
    override fun bind(viewHolder: ViewHolder, position: Int) {
        viewHolder.itemView.chatHistoryFromUserPastMessage.text = chatMessage

        // Load user image into the Circular Image View
        val targetUrl = chatUserImage
        val targetUserImage = viewHolder.itemView.chatHistoryFromUserImage

        if (targetUrl == "default") {
            Picasso.get().load(R.drawable.pikademo).into(targetUserImage)
        } else {
            Picasso.get().load(targetUrl).into(targetUserImage)
        }
    }

    override fun getLayout(): Int {
        return R.layout.recyclerview_chat_from_row
    }
}

class ChatToItem(val chatMessage: String, val chatUserImage: String): Item<ViewHolder>() {
    override fun bind(viewHolder: ViewHolder, position: Int) {
        viewHolder.itemView.chatHistoryToUserPastMessage.text = chatMessage

        // Load user image into the Circular Image View
        val url = chatUserImage
        val userImage = viewHolder.itemView.chatHistoryToUserImage

        if (url == "default") {
            Picasso.get().load(R.drawable.pikademo).into(userImage)
        } else {
            Picasso.get().load(url).into(userImage)
        }
    }

    override fun getLayout(): Int {
        return R.layout.recyclerview_chat_to_row
    }
}
