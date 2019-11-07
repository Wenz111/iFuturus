package com.example.ifuturus

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.ifuturus.model.lodgereportmodel
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.squareup.picasso.Picasso

class MySubmittedReportActivity : AppCompatActivity() {

    // Firebase instance variables
    private lateinit var mFirebaseAuth: FirebaseAuth
    private var mFirebaseUser: FirebaseUser? = null

    // Get Reference to the Database
    lateinit var ref: DatabaseReference

    // Initialize variables
    lateinit var mrecyclerviewMySubmittedReport : RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_submitted_report)

        // Get current user
        mFirebaseAuth = FirebaseAuth.getInstance()
        mFirebaseUser = mFirebaseAuth.currentUser

        // Get Reference to the Database
        ref = FirebaseDatabase.getInstance().getReference().child("lodgereport")

        // Assign variable
        mrecyclerviewMySubmittedReport = findViewById(R.id.recyclerviewMySubmittedReport)
        mrecyclerviewMySubmittedReport.setHasFixedSize(true)
        mrecyclerviewMySubmittedReport.layoutManager = LinearLayoutManager(this)

        firebaseDisplayData()
    }

    companion object {
        val MY_REPORT_ID_KEY = "MY_REPORT_ID_KEY"
    }

    private fun firebaseDisplayData() {
        val option = FirebaseRecyclerOptions.Builder<lodgereportmodel>()
            .setQuery(ref, lodgereportmodel::class.java)
            .setLifecycleOwner(this)
            .build()

        val firebaseRecyclerAdapter = object: FirebaseRecyclerAdapter<lodgereportmodel, MySubmittedReportActivity.MyEditViewHolder>(option) {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MySubmittedReportActivity.MyEditViewHolder {
            val itemView = LayoutInflater.from(this@MySubmittedReportActivity).inflate(R.layout.activity_lodge_report_status_details,parent,false)
            return MySubmittedReportActivity.MyEditViewHolder(itemView)
        }

        override fun onBindViewHolder(holder: MySubmittedReportActivity.MyEditViewHolder, position: Int, model: lodgereportmodel) {
            val reportId = getRef(position).key.toString()

            Log.d("ReportStatusActivity-firebaseData Function", "Report ID: $reportId")

            ref.child(reportId).addValueEventListener(object: ValueEventListener {
                override fun onCancelled(p0: DatabaseError) {
                    Toast.makeText(this@MySubmittedReportActivity, "Error Occurred "+ p0.toException(), Toast.LENGTH_SHORT).show()
                }

                override fun onDataChange(p0: DataSnapshot) {


                    // Check if Report ID child belongs to the current Firebase User ID
                    if (model.id == mFirebaseUser!!.uid) {
                            // Set Value into each view of recycler view
                            holder.my_tvReportid.setText("Report ID: ${model.complaintId}")
                            Picasso.get().load(model.photoUrl).into(holder.my_ivReportimage)
                            holder.my_tvReportPropertyType.setText("Property Type: ${model.complaintDetails}")
                            holder.my_tvReportStatus.setText("Report Status: ${model.complaintStatus}")
                            holder.my_tvReportNotes.setText("Report Notes: \n${model.complaintNotes}")
                            holder.my_tvReportCategory.setText("Report Category: \n${model.complaintCategory}")
                            holder.my_tvReportLocation.setText("Report Location: \n${model.complaintLocation}")
                            holder.my_tvReportSubmittedBy.setText("Report Submitted By: ${model.name}")
                            holder.my_tvReportDateTime.setText("Report Submitted On: ${model.complaintDate}, ${model.complaintTime}")

                            // Test On Click Listener
                            holder.itemView.setOnClickListener {
                                //Toast.makeText(this@MySubmittedReportActivity, "Item View is Clicked: ${model.complaintId}", Toast.LENGTH_LONG).show()
                                val intent = Intent(this@MySubmittedReportActivity, UpdateComplaintReportActivity::class.java)
                                intent.putExtra(MY_REPORT_ID_KEY, model.complaintId)
                                startActivity(intent)
                            }
                        } else {
                            val params = holder.itemView.layoutParams
                            params.height = 0
                            holder.itemView.layoutParams = params
                        }
                    } // End Of DataSnapShot
                })
            }
        }
        mrecyclerviewMySubmittedReport.adapter = firebaseRecyclerAdapter
        firebaseRecyclerAdapter.startListening()
    }

    class MyEditViewHolder(itemView: View?) : RecyclerView.ViewHolder(itemView!!) {
        internal var my_ivReportimage: ImageView = itemView!!.findViewById(R.id.iv_report_image)
        internal var my_tvReportid: TextView = itemView!!.findViewById(R.id.tv_report_id)
        internal var my_tvReportPropertyType: TextView = itemView!!.findViewById(R.id.tv_property_type)
        internal var my_tvReportStatus: TextView = itemView!!.findViewById(R.id.tv_report_status)
        internal var my_tvReportNotes: TextView = itemView!!.findViewById(R.id.tv_report_notes)
        internal var my_tvReportCategory: TextView = itemView!!.findViewById(R.id.tv_report_category)
        internal var my_tvReportLocation: TextView = itemView!!.findViewById(R.id.tv_report_location)
        internal var my_tvReportSubmittedBy: TextView = itemView!!.findViewById(R.id.tv_report_submitted_by)
        internal var my_tvReportDateTime: TextView = itemView!!.findViewById(R.id.tv_report_datetime)
    }
}
