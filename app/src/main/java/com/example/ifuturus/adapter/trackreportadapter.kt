package com.example.ifuturus.adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.ifuturus.R
import com.example.ifuturus.UpdateComplaintReportActivity
import com.example.ifuturus.model.lodgereportmodel
import com.google.android.material.button.MaterialButton
import com.squareup.picasso.Picasso

class trackreportadapter(var trackReportList: ArrayList<lodgereportmodel>) :
    RecyclerView.Adapter<trackreportadapter.TrackReportViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrackReportViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.activity_track_report_status_details, parent,false)
        return TrackReportViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: TrackReportViewHolder, position: Int) {
        holder.trsReportId.setText("Report ID: ${trackReportList[position].complaintId}")
        Picasso.get().load(trackReportList[position].photoUrl).into(holder.trsReportImage)
        holder.trsReportDetails.setText("Property Type: ${trackReportList[position].complaintDetails}")
        holder.trsReportStatus.setText("Report Status: ${trackReportList[position].complaintStatus}")
        holder.trsReportDateTime.setText("Report Submitted On: \n${trackReportList[position].complaintDate}, ${trackReportList[position].complaintTime}")

        holder.trsViewReportButton.setOnClickListener {
            val intent = Intent(it.context, UpdateComplaintReportActivity::class.java)
            intent.putExtra(userreportadapter.MY_REPORT_ID_KEY, trackReportList[position].complaintId)
            it.context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int {
        return trackReportList.size
    }

    class TrackReportViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        internal var trsReportId: TextView = itemView.findViewById(R.id.tv_track_report_status_report_id)
        internal var trsReportImage: ImageView = itemView.findViewById(R.id.iv_track_report_status_report_image)
        internal var trsReportDetails: TextView = itemView.findViewById(R.id.tv_track_report_status_property_type)
        internal var trsReportStatus: TextView = itemView.findViewById(R.id.tv_track_report_status_report_status)
        internal var trsReportDateTime: TextView = itemView.findViewById(R.id.tv_track_report_status_report_datetime)
        internal var trsViewReportButton: MaterialButton = itemView.findViewById(R.id.button_track_report_status_report_view_report)
    }
}