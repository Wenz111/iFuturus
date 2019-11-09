package com.example.ifuturus.adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.ifuturus.ChatReportHistoryActivity
import com.example.ifuturus.ChatReportListActivity
import com.example.ifuturus.R
import com.example.ifuturus.model.lodgereportmodel
import com.google.android.material.button.MaterialButton
import com.squareup.picasso.Picasso

class viewallreportadapter(var viewAllReportList: ArrayList<lodgereportmodel>) :
        RecyclerView.Adapter<viewallreportadapter.ViewAllReportViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewAllReportViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.activity_lodge_report_status_details, parent, false)
        return ViewAllReportViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ViewAllReportViewHolder, position: Int) {

        // Set Value into each view of recycler view
        holder.tvReportid.setText("Report ID: ${viewAllReportList[position].complaintId}")
        Picasso.get().load(viewAllReportList[position].photoUrl).into(holder.ivReportimage)
        holder.tvReportPropertyType.setText("Property Type: ${viewAllReportList[position].complaintDetails}")
        holder.tvReportStatus.setText("Report Status: ${viewAllReportList[position].complaintStatus}")
        holder.tvReportNotes.setText("Report Notes: \n${viewAllReportList[position].complaintNotes}")
        holder.tvReportCategory.setText("Report Category: \n${viewAllReportList[position].complaintCategory}")
        holder.tvReportLocation.setText("Report Location: \n${viewAllReportList[position].complaintLocation}")
        holder.tvReportSubmittedBy.setText("Report Submitted By: ${viewAllReportList[position].name}")
        holder.tvReportDateTime.setText("Report Submitted On: ${viewAllReportList[position].complaintDate}, ${viewAllReportList[position].complaintTime}")

        holder.mybuttonEditReport.visibility = View.GONE
        holder.mybuttonViewChat.width = 0
        holder.mybuttonViewChat.setOnClickListener {
            // Start Chat Activity
            val intent = Intent(it.context, ChatReportHistoryActivity::class.java)
            intent.putExtra(ChatReportListActivity.REPORT_ID_KEY, viewAllReportList[position].complaintId)
            it.context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int {
        return viewAllReportList.size
    }

    class ViewAllReportViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        internal var ivReportimage: ImageView = itemView.findViewById(R.id.iv_report_image)
        internal var tvReportid: TextView = itemView.findViewById(R.id.tv_report_id)
        internal var tvReportPropertyType: TextView = itemView.findViewById(R.id.tv_property_type)
        internal var tvReportStatus: TextView = itemView.findViewById(R.id.tv_report_status)
        internal var tvReportNotes: TextView = itemView.findViewById(R.id.tv_report_notes)
        internal var tvReportCategory: TextView = itemView.findViewById(R.id.tv_report_category)
        internal var tvReportLocation: TextView = itemView.findViewById(R.id.tv_report_location)
        internal var tvReportSubmittedBy: TextView = itemView.findViewById(R.id.tv_report_submitted_by)
        internal var tvReportDateTime: TextView = itemView.findViewById(R.id.tv_report_datetime)
        internal var mybuttonViewChat: MaterialButton = itemView.findViewById(R.id.button_report_chat)
        internal var mybuttonEditReport: MaterialButton = itemView.findViewById(R.id.button_report_edit)
    }
}