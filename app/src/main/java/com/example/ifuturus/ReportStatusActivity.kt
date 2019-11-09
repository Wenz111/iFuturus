package com.example.ifuturus

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.ifuturus.adapter.viewallreportadapter
import com.example.ifuturus.model.lodgereportmodel
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_report_status.*

class ReportStatusActivity : AppCompatActivity(), View.OnClickListener {

    // Get Reference to the Database
    lateinit var reference: DatabaseReference

    // Initialize variables
    lateinit var mrecylerview : RecyclerView

    // Store Firebase Database data to an Array List
    lateinit var viewAllReportList : ArrayList<lodgereportmodel>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_report_status)

        // Get Reference to the Database
        reference = FirebaseDatabase.getInstance().reference.child("lodgereport")

        // Assign variable
        mrecylerview = findViewById(R.id.reyclerview)
        mrecylerview.setHasFixedSize(true)
        mrecylerview.layoutManager = LinearLayoutManager(this)

        // Set On Click Listener
        all_report_chip_default.setOnClickListener(this)
        all_report_chip_pending.setOnClickListener(this)
        all_report_chip_processing.setOnClickListener(this)
        all_report_chip_completed.setOnClickListener(this)
        all_report_chip_private.setOnClickListener(this)
        all_report_chip_public.setOnClickListener(this)

        displayAllReport()
    }

    private fun displayAllReport() {
        // Store value to Array List
        reference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(p0: DataSnapshot) {
                if (p0.exists()) {
                    viewAllReportList = ArrayList()

                    for(ds in p0.children) {

                        if (all_report_chip_default.isChecked && all_report_chip_private.isChecked) {
                            if (ds.getValue(lodgereportmodel::class.java)!!.complaintDetails == "private") {
                                viewAllReportList.add(ds.getValue(lodgereportmodel::class.java)!!)
                            }
                        } else if (all_report_chip_default.isChecked && all_report_chip_public.isChecked) {
                            if (ds.getValue(lodgereportmodel::class.java)!!.complaintDetails == "public") {
                                viewAllReportList.add(ds.getValue(lodgereportmodel::class.java)!!)
                            }
                        } else if (all_report_chip_pending.isChecked && all_report_chip_private.isChecked) {
                            if (ds.getValue(lodgereportmodel::class.java)!!.complaintDetails == "private" &&
                                ds.getValue(lodgereportmodel::class.java)!!.complaintStatus == "pending") {
                                viewAllReportList.add(ds.getValue(lodgereportmodel::class.java)!!)
                            }
                        } else if (all_report_chip_pending.isChecked && all_report_chip_public.isChecked) {
                            if (ds.getValue(lodgereportmodel::class.java)!!.complaintDetails == "public" &&
                                ds.getValue(lodgereportmodel::class.java)!!.complaintStatus == "pending") {
                                viewAllReportList.add(ds.getValue(lodgereportmodel::class.java)!!)
                            }
                        } else if (all_report_chip_processing.isChecked && all_report_chip_private.isChecked) {
                            if (ds.getValue(lodgereportmodel::class.java)!!.complaintDetails == "private" &&
                                ds.getValue(lodgereportmodel::class.java)!!.complaintStatus == "processing") {
                                viewAllReportList.add(ds.getValue(lodgereportmodel::class.java)!!)
                            }
                        } else if (all_report_chip_processing.isChecked && all_report_chip_public.isChecked) {
                            if (ds.getValue(lodgereportmodel::class.java)!!.complaintDetails == "public" &&
                                ds.getValue(lodgereportmodel::class.java)!!.complaintStatus == "processing") {
                                viewAllReportList.add(ds.getValue(lodgereportmodel::class.java)!!)
                            }
                        } else if (all_report_chip_completed.isChecked && all_report_chip_private.isChecked) {
                            if (ds.getValue(lodgereportmodel::class.java)!!.complaintDetails == "private" &&
                                ds.getValue(lodgereportmodel::class.java)!!.complaintStatus == "completed") {
                                viewAllReportList.add(ds.getValue(lodgereportmodel::class.java)!!)
                            }
                        } else if (all_report_chip_completed.isChecked && all_report_chip_public.isChecked) {
                            if (ds.getValue(lodgereportmodel::class.java)!!.complaintDetails == "public" &&
                                ds.getValue(lodgereportmodel::class.java)!!.complaintStatus == "completed") {
                                viewAllReportList.add(ds.getValue(lodgereportmodel::class.java)!!)
                            }
                        } else if (all_report_chip_default.isChecked) {
                            viewAllReportList.add(ds.getValue(lodgereportmodel::class.java)!!)
                        } else if (all_report_chip_pending.isChecked) {
                            if (ds.getValue(lodgereportmodel::class.java)!!.complaintStatus == "pending") {
                                viewAllReportList.add(ds.getValue(lodgereportmodel::class.java)!!)
                            }
                        } else if (all_report_chip_processing.isChecked) {
                            if (ds.getValue(lodgereportmodel::class.java)!!.complaintStatus == "processing") {
                                viewAllReportList.add(ds.getValue(lodgereportmodel::class.java)!!)
                            }
                        } else if (all_report_chip_completed.isChecked) {
                            if (ds.getValue(lodgereportmodel::class.java)!!.complaintStatus == "completed") {
                                viewAllReportList.add(ds.getValue(lodgereportmodel::class.java)!!)
                            }
                        } else {
                            mrecylerview.adapter = null
                        }
                    }
                    val viewAllReportAdapter = viewallreportadapter(viewAllReportList)
                    mrecylerview.adapter = viewAllReportAdapter
                }
            }

            override fun onCancelled(p0: DatabaseError) {
                // Handle Error
            }
        })
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.all_report_chip_default -> displayAllReport()
            R.id.all_report_chip_pending -> displayAllReport()
            R.id.all_report_chip_processing -> displayAllReport()
            R.id.all_report_chip_completed -> displayAllReport()
            R.id.all_report_chip_private -> displayAllReport()
            R.id.all_report_chip_public -> displayAllReport()
        }
    }
}