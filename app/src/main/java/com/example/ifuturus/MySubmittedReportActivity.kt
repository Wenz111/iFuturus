package com.example.ifuturus

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.ifuturus.adapter.userreportadapter
import com.example.ifuturus.model.lodgereportmodel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_my_submitted_report.*

class MySubmittedReportActivity : AppCompatActivity(), View.OnClickListener {

    // Get Reference to the Database
    private lateinit var reference : DatabaseReference

    // Initialize variables
    lateinit var mrecyclerviewMySubmittedReport : RecyclerView

    // Store Firebase Database data to an Array List
    lateinit var userReportList : ArrayList<lodgereportmodel>

    // Firebase instance variables
    private lateinit var mFirebaseAuth: FirebaseAuth
    private lateinit var mFirebaseUser: FirebaseUser

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_submitted_report)

        // Get current user
        mFirebaseAuth = FirebaseAuth.getInstance()
        mFirebaseUser = mFirebaseAuth.currentUser!!

        // Get Reference to the Database
        reference = FirebaseDatabase.getInstance().reference.child("lodgereport")

        // Assign variable
        mrecyclerviewMySubmittedReport = findViewById(R.id.recyclerviewMySubmittedReport)
        mrecyclerviewMySubmittedReport.setHasFixedSize(true)
        mrecyclerviewMySubmittedReport.layoutManager = LinearLayoutManager(this)

        // Set OnClickListener
        user_report_chip_default.setOnClickListener(this)
        user_report_chip_pending.setOnClickListener(this)
        user_report_chip_processing.setOnClickListener(this)
        user_report_chip_completed.setOnClickListener(this)
        user_report_chip_private.setOnClickListener(this)
        user_report_chip_public.setOnClickListener(this)

        displayUserSubmittedReport()
    }

    private fun displayUserSubmittedReport() {
        // Store value to Array List
        reference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(p0: DataSnapshot) {
                if (p0.exists()) {
                    userReportList = ArrayList()

                    for(ds in p0.children) {

                        if (user_report_chip_default.isChecked && user_report_chip_private.isChecked) {
                            if(ds.getValue(lodgereportmodel::class.java)!!.id == mFirebaseUser.uid &&
                                ds.getValue(lodgereportmodel::class.java)!!.complaintDetails == "private") {
                                userReportList.add(ds.getValue(lodgereportmodel::class.java)!!)
                            }
                        } else if (user_report_chip_default.isChecked && user_report_chip_public.isChecked) {
                            if(ds.getValue(lodgereportmodel::class.java)!!.id == mFirebaseUser.uid &&
                                ds.getValue(lodgereportmodel::class.java)!!.complaintDetails == "public") {
                                userReportList.add(ds.getValue(lodgereportmodel::class.java)!!)
                            }
                        } else if (user_report_chip_pending.isChecked && user_report_chip_private.isChecked) {
                            if(ds.getValue(lodgereportmodel::class.java)!!.id == mFirebaseUser.uid &&
                                ds.getValue(lodgereportmodel::class.java)!!.complaintStatus == "pending" &&
                                ds.getValue(lodgereportmodel::class.java)!!.complaintDetails == "private") {
                                userReportList.add(ds.getValue(lodgereportmodel::class.java)!!)
                            }
                        } else if (user_report_chip_pending.isChecked && user_report_chip_public.isChecked) {
                            if(ds.getValue(lodgereportmodel::class.java)!!.id == mFirebaseUser.uid &&
                                ds.getValue(lodgereportmodel::class.java)!!.complaintStatus == "pending" &&
                                ds.getValue(lodgereportmodel::class.java)!!.complaintDetails == "public") {
                                userReportList.add(ds.getValue(lodgereportmodel::class.java)!!)
                            }
                        } else if (user_report_chip_processing.isChecked && user_report_chip_private.isChecked) {
                            if(ds.getValue(lodgereportmodel::class.java)!!.id == mFirebaseUser.uid &&
                                ds.getValue(lodgereportmodel::class.java)!!.complaintStatus == "processing" &&
                                ds.getValue(lodgereportmodel::class.java)!!.complaintDetails == "private") {
                                userReportList.add(ds.getValue(lodgereportmodel::class.java)!!)
                            }
                        } else if (user_report_chip_processing.isChecked && user_report_chip_public.isChecked) {
                            if(ds.getValue(lodgereportmodel::class.java)!!.id == mFirebaseUser.uid &&
                                ds.getValue(lodgereportmodel::class.java)!!.complaintStatus == "processing" &&
                                ds.getValue(lodgereportmodel::class.java)!!.complaintDetails == "public") {
                                userReportList.add(ds.getValue(lodgereportmodel::class.java)!!)
                            }
                        } else if (user_report_chip_completed.isChecked &&user_report_chip_private.isChecked) {
                            if(ds.getValue(lodgereportmodel::class.java)!!.id == mFirebaseUser.uid &&
                                ds.getValue(lodgereportmodel::class.java)!!.complaintStatus == "completed" &&
                                ds.getValue(lodgereportmodel::class.java)!!.complaintDetails == "private") {
                                userReportList.add(ds.getValue(lodgereportmodel::class.java)!!)
                            }
                        } else if (user_report_chip_completed.isChecked &&user_report_chip_public.isChecked) {
                            if(ds.getValue(lodgereportmodel::class.java)!!.id == mFirebaseUser.uid &&
                                ds.getValue(lodgereportmodel::class.java)!!.complaintStatus == "completed" &&
                                ds.getValue(lodgereportmodel::class.java)!!.complaintDetails == "public") {
                                userReportList.add(ds.getValue(lodgereportmodel::class.java)!!)
                            }
                        } else if (user_report_chip_default.isChecked) {
                            if(ds.getValue(lodgereportmodel::class.java)!!.id == mFirebaseUser.uid) {
                                userReportList.add(ds.getValue(lodgereportmodel::class.java)!!)
                            }
                        } else if (user_report_chip_pending.isChecked) {
                            if(ds.getValue(lodgereportmodel::class.java)!!.id == mFirebaseUser.uid &&
                                ds.getValue(lodgereportmodel::class.java)!!.complaintStatus == "pending") {
                                userReportList.add(ds.getValue(lodgereportmodel::class.java)!!)
                            }
                        } else if (user_report_chip_processing.isChecked) {
                            if(ds.getValue(lodgereportmodel::class.java)!!.id == mFirebaseUser.uid &&
                                ds.getValue(lodgereportmodel::class.java)!!.complaintStatus == "processing") {
                                userReportList.add(ds.getValue(lodgereportmodel::class.java)!!)
                            }
                        } else if (user_report_chip_completed.isChecked) {
                            if(ds.getValue(lodgereportmodel::class.java)!!.id == mFirebaseUser.uid &&
                                ds.getValue(lodgereportmodel::class.java)!!.complaintStatus == "completed") {
                                userReportList.add(ds.getValue(lodgereportmodel::class.java)!!)
                            }
                        } else {
                            mrecyclerviewMySubmittedReport.adapter = null
                        }
                    }
                    val userReportAdapter = userreportadapter(userReportList)
                    mrecyclerviewMySubmittedReport.adapter = userReportAdapter
                }
            }

            override fun onCancelled(p0: DatabaseError) {
                // Handle Error
            }
        })
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.user_report_chip_default -> displayUserSubmittedReport()
            R.id.user_report_chip_pending -> displayUserSubmittedReport()
            R.id.user_report_chip_processing -> displayUserSubmittedReport()
            R.id.user_report_chip_completed -> displayUserSubmittedReport()
            R.id.user_report_chip_private -> displayUserSubmittedReport()
            R.id.user_report_chip_public -> displayUserSubmittedReport()
        }
    }
}