package com.example.ifuturus

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.ifuturus.model.lodgereportmodel
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.activity_chat_report_list.*
import kotlinx.android.synthetic.main.activity_chat_report_row_list.view.*

class ChatReportListActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat_report_list)

        // Assign Chat Report List Recycler View
        chatReportListRecyclerView.layoutManager = LinearLayoutManager(this)

        fetchChatReportList()
    }

    companion object {
        val REPORT_ID_KEY = "REPORT_ID_KEY"
    }

    private fun fetchChatReportList() {
        val ref = FirebaseDatabase.getInstance().getReference("lodgereport")
        ref.addListenerForSingleValueEvent(object: ValueEventListener {
            override fun onDataChange(p0: DataSnapshot) {
                val adapter = GroupAdapter<ViewHolder>()

                p0.children.forEach{
                    Log.d("Chat Report Activity List","Inside DataSnapshot: $it")

                    // Get Chat Report List
                    val chatReportList = it.getValue(lodgereportmodel::class.java)

                    // If Firebase have value
                    if (chatReportList != null) {
                        adapter.add(ReportItem(chatReportList))
                    }
                }

                adapter.setOnItemClickListener { item, view ->

                    // Get Report Id
                    val reportItem = item as ReportItem

                    // Start Chat Log Activity
                    val intent = Intent(view.context, ChatReportHistoryActivity::class.java)
                    intent.putExtra(REPORT_ID_KEY, reportItem.chatReportList.complaintId)
                    Log.d("Chat Report List Activity","Report ID: ${reportItem.chatReportList.complaintId}")
                    startActivity(intent)
                }

                chatReportListRecyclerView.adapter = adapter
            }

            override fun onCancelled(p0: DatabaseError) {

            }
        })
    }
}

class ReportItem(val chatReportList: lodgereportmodel): Item<ViewHolder>() {
    override fun bind(viewHolder: ViewHolder, position: Int) {
        // Will be Called in our list for each Report Object
        viewHolder.itemView.tv_chatReportId.text = "Report ID: \n${chatReportList.complaintId}\n\n\nClick to start chatting >>"
        Picasso.get().load(chatReportList.photoUrl).into(viewHolder.itemView.iv_chatReportImage)
    }

    override fun getLayout(): Int {
        return R.layout.activity_chat_report_row_list
    }
}