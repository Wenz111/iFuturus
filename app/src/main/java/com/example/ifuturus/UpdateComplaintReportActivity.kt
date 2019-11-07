package com.example.ifuturus

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import com.example.ifuturus.model.lodgereportmodel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_update_complaint_report.*

class UpdateComplaintReportActivity : AppCompatActivity(), View.OnClickListener {

    private var reportID: String? = null

    // Firebase instance variables
    private lateinit var mFirebaseAuth: FirebaseAuth
    private var mFirebaseUser: FirebaseUser? = null

    // Store Temporary Value
    private var id: String? = null
    private var name: String? = null
    private var email: String? = null
    private var gender: String? = null
    private var complaintDetails: String? = null
    private var complaintNotes: String? = null
    private var complaintLocation: String? = null
    private var complaintCategory: String? = null
    private var complaintStatus: String? = null
    private var complaintId: String? = null
    private var complaintDate: String? = null
    private var complaintTime: String? = null
    private var photoUrl: String? = null
    private var latitude: String? = null
    private var longitude: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_update_complaint_report)

        // Set OnClickListener
        edit_my_report.setOnClickListener(this)
        myReportSaveButton.setOnClickListener(this)
        cancel_my_report.setOnClickListener(this)

        // Get current user
        mFirebaseAuth = FirebaseAuth.getInstance()
        mFirebaseUser = mFirebaseAuth.currentUser

        // Retrieve Data From Intent
        reportID = intent.getStringExtra(MySubmittedReportActivity.MY_REPORT_ID_KEY)
        supportActionBar?.title = "Edit Report: $reportID"

        loadSubmittedReport()
    }

    private fun loadSubmittedReport() {
        val ref = FirebaseDatabase.getInstance().getReference("lodgereport").child("$reportID")

        ref.addValueEventListener(object: ValueEventListener {
            override fun onDataChange(p0: DataSnapshot) {

                // Load data into the Submitted Report
                val reportDetails = p0.getValue(lodgereportmodel::class.java)
                // Assign Data to the Empty Field
                if (reportDetails != null){
                    edit_tv_report_id.setText("Report ID: ${reportDetails.complaintId}")
                    Picasso.get().load(reportDetails.photoUrl).into(edit_iv_report_image)
                    edit_tv_property_type.setText("Property Type: ${reportDetails.complaintDetails}")
                    edit_tv_report_status.setText("Report Status: ${reportDetails.complaintStatus}")
                    edit_tv_report_notes.setText("Report Notes: \n${reportDetails.complaintNotes}")
                    edit_tv_report_category.setText("Report Category: \n${reportDetails.complaintCategory}")
                    edit_tv_report_location.setText("Report Location: \n${reportDetails.complaintLocation}")
                    edit_tv_report_submitted_by.setText("Report Submitted By: ${reportDetails.name}")
                    edit_tv_report_datetime.setText("Report Submitted On: ${reportDetails.complaintDate}, ${reportDetails.complaintTime}")

                    // Assign value to Temporary Variable
                    id = reportDetails.id
                    name = reportDetails.name
                    email = reportDetails.email
                    gender = reportDetails.gender
                    complaintDetails = reportDetails.complaintDetails
                    complaintNotes = reportDetails.complaintNotes
                    complaintLocation = reportDetails.complaintLocation
                    complaintCategory = reportDetails.complaintCategory
                    complaintStatus = reportDetails.complaintStatus
                    complaintId = reportDetails.complaintId
                    complaintDate = reportDetails.complaintDate
                    complaintTime = reportDetails.complaintTime
                    photoUrl = reportDetails.photoUrl
                    latitude = reportDetails.latitude
                    longitude = reportDetails.longitude
                }
            }

            override fun onCancelled(p0: DatabaseError) {
                // Handle Error
            }
        })
    }

    private fun editReport() {
        Log.d("Edit Report", "Edit report is clicked")
        myReportTextContainer.visibility = View.VISIBLE
        cancel_my_report.visibility = View.VISIBLE
        edit_my_report.visibility = View.GONE
    }

    private fun cancelReport() {
        Log.d("Cancel Report", "Cancel report is clicked")
        myReportTextContainer.visibility = View.GONE
        cancel_my_report.visibility = View.GONE
        edit_my_report.visibility = View.VISIBLE
    }

    private fun updateReportNotes() {
        // Get new Report Notes
        val newReportNotes = myReportEditText.text.toString()
        Log.d("Report Notes Details", newReportNotes)

        if (newReportNotes == "") {
            return
        }

        // Save the new Report Notes to Firebase Database
        val reference = FirebaseDatabase.getInstance().getReference("lodgereport").child("$reportID")

        val newUpdatedReport = lodgereportmodel(
            id,
            name,
            email,
            gender,
            complaintDetails,
            newReportNotes,
            complaintLocation,
            complaintCategory,
            complaintStatus,
            complaintId,
            complaintDate,
            complaintTime,
            photoUrl,
            latitude,
            longitude
        )

        reference.setValue(newUpdatedReport)
            .addOnSuccessListener {
                Log.d("Update Report", "Successfully saved value to database")
                myReportEditText.setText("")
                cancelReport()
                Toast.makeText(this, "Report Notes has successfully been updated", Toast.LENGTH_LONG).show()
            }
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.edit_my_report -> {
                editReport()
            }
            R.id.cancel_my_report -> {
                cancelReport()
            }
            R.id.myReportSaveButton -> {
                updateReportNotes()
            }
        }
    }
}
