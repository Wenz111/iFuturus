package com.example.ifuturus.adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.ifuturus.ChatReportHistoryActivity
import com.example.ifuturus.ChatReportListActivity.Companion.REPORT_ID_KEY
import com.example.ifuturus.R
import com.example.ifuturus.UpdateComplaintReportActivity
import com.example.ifuturus.model.lodgereportmodel
import com.google.android.material.button.MaterialButton
import com.squareup.picasso.Picasso

class userreportadapter(var userReportList: ArrayList<lodgereportmodel>) :
        RecyclerView.Adapter<userreportadapter.UserReportViewHolder>() {

    companion object {
        const val MY_REPORT_ID_KEY = "MY_REPORT_ID_KEY"
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserReportViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.activity_lodge_report_status_details, parent, false)
        return UserReportViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: UserReportViewHolder, position: Int) {
        // Set Value into each view of recycler view
        holder.my_tvReportid.setText("Report ID: ${userReportList[position].complaintId}")
        Picasso.get().load(userReportList[position].photoUrl).into(holder.my_ivReportimage)
        holder.my_tvReportPropertyType.setText("Property Type: ${userReportList[position].complaintDetails}")
        holder.my_tvReportStatus.setText("Report Status: ${userReportList[position].complaintStatus}")
        holder.my_tvReportNotes.setText("Report Notes: \n${userReportList[position].complaintNotes}")
        holder.my_tvReportCategory.setText("Report Category: \n${userReportList[position].complaintCategory}")
        holder.my_tvReportLocation.setText("Report Location: \n${userReportList[position].complaintLocation}")
        holder.my_tvReportSubmittedBy.setText("Report Submitted By: ${userReportList[position].name}")
        holder.my_tvReportDateTime.setText("Report Submitted On: ${userReportList[position].complaintDate}, ${userReportList[position].complaintTime}")

        // If Report Status is equal to Processing or Completed
        // Show View Report instead of Edit Report
        if (userReportList[position].complaintStatus == "processing" || userReportList[position].complaintStatus == "completed") {
            holder.my_buttonEditReport.setText("View Report")
        }

        // View Chat On Click Listener
        holder.my_buttonViewChat.setOnClickListener {
            // Start Chat Activity
            val intent = Intent(it.context, ChatReportHistoryActivity::class.java)
            intent.putExtra(REPORT_ID_KEY, userReportList[position].complaintId)
            it.context.startActivity(intent)
        }

        // Edit Report On Click Listener
        holder.my_buttonEditReport.setOnClickListener {
            val intent = Intent(it.context, UpdateComplaintReportActivity::class.java)
            intent.putExtra(MY_REPORT_ID_KEY, userReportList[position].complaintId)
            it.context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int {
        return userReportList.size
    }

    class UserReportViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        internal var my_ivReportimage: ImageView = itemView.findViewById(R.id.iv_report_image)
        internal var my_tvReportid: TextView = itemView.findViewById(R.id.tv_report_id)
        internal var my_tvReportPropertyType: TextView = itemView.findViewById(R.id.tv_property_type)
        internal var my_tvReportStatus: TextView = itemView.findViewById(R.id.tv_report_status)
        internal var my_tvReportNotes: TextView = itemView.findViewById(R.id.tv_report_notes)
        internal var my_tvReportCategory: TextView = itemView.findViewById(R.id.tv_report_category)
        internal var my_tvReportLocation: TextView = itemView.findViewById(R.id.tv_report_location)
        internal var my_tvReportSubmittedBy: TextView = itemView.findViewById(R.id.tv_report_submitted_by)
        internal var my_tvReportDateTime: TextView = itemView.findViewById(R.id.tv_report_datetime)
        internal var my_buttonViewChat: MaterialButton = itemView.findViewById(R.id.button_report_chat)
        internal var my_buttonEditReport: MaterialButton = itemView.findViewById(R.id.button_report_edit)
    }
}