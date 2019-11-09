package com.example.ifuturus

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.ifuturus.adapter.trackreportadapter
import com.example.ifuturus.model.lodgereportmodel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_track_report_status.*

class TrackReportStatusActivity : AppCompatActivity(), View.OnClickListener {

    // Get Reference to the Database
    private lateinit var reference : DatabaseReference

    // Initialize variables
    private lateinit var mrecylerview : RecyclerView

    // Store Firebase Database data to an Array List
    lateinit var reportList : ArrayList<lodgereportmodel>

    // Firebase instance variables
    private lateinit var mFirebaseAuth: FirebaseAuth
    private lateinit var mFirebaseUser: FirebaseUser

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_track_report_status)

        // Get Reference to the Database
        reference = FirebaseDatabase.getInstance().reference.child("lodgereport")

        // Get current user
        mFirebaseAuth = FirebaseAuth.getInstance()
        mFirebaseUser = mFirebaseAuth.currentUser!!

        // Set OnClickListener
        chip_default.setOnClickListener(this)
        chip_pending.setOnClickListener(this)
        chip_processing.setOnClickListener(this)
        chip_completed.setOnClickListener(this)
        chip_private.setOnClickListener(this)
        chip_public.setOnClickListener(this)
        chip_user.setOnClickListener(this)
        chip_all.setOnClickListener(this)

        // Assign variable
        mrecylerview = findViewById(R.id.rv_track_report_status)
        mrecylerview.setHasFixedSize(true)
        mrecylerview.layoutManager = LinearLayoutManager(this)

        if(chip_default.isChecked && chip_user.isChecked) {
            loadDefaultFirebaseData()
        }
    }

    private fun filterFirebaseDatabase() {
        if (chip_default.isChecked) {
            displayDefaultData()
        }

        if (chip_pending.isChecked) {
            displayPendingData()
        }

        if (chip_processing.isChecked) {
            displayProcessingData()
        }

        if (chip_completed.isChecked) {
            displayCompletedData()
        }

        if (chip_private.isChecked || chip_public.isChecked || chip_user.isChecked || chip_all.isChecked) {
            displayDefaultEmptyData()
        }
    }

    private fun loadDefaultFirebaseData() {

        // Store value to Array List
        reference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(p0: DataSnapshot) {
                if (p0.exists()) {
                    reportList = ArrayList()

                    for(ds in p0.children) {
                        if(ds.getValue(lodgereportmodel::class.java)!!.id == mFirebaseUser.uid) {
                            reportList.add(ds.getValue(lodgereportmodel::class.java)!!)
                        }
                    }
                    val trackReportAdapter = trackreportadapter(reportList)
                    mrecylerview.adapter = trackReportAdapter
                }
            }

            override fun onCancelled(p0: DatabaseError) {
                // Handle Error
            }
        })
    }

    // Filter Firebase Data
    private fun displayDefaultEmptyData() {
        mrecylerview.adapter = null
    }

    // Default Logic
    // Filter Firebase Data
    private fun displayDefaultData() {

        // Store value to Array List
        reference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(p0: DataSnapshot) {
                if (p0.exists()) {
                    reportList = ArrayList()

                    for(ds in p0.children) {

                        if (chip_default.isChecked && chip_private.isChecked && chip_user.isChecked) {
                            if (ds.getValue(lodgereportmodel::class.java)!!.complaintDetails == "private" &&
                                ds.getValue(lodgereportmodel::class.java)!!.id == mFirebaseUser.uid) {
                                reportList.add(ds.getValue(lodgereportmodel::class.java)!!)
                            }
                        } else if (chip_default.isChecked && chip_public.isChecked && chip_user.isChecked) {
                            if (ds.getValue(lodgereportmodel::class.java)!!.complaintDetails == "public" &&
                                ds.getValue(lodgereportmodel::class.java)!!.id == mFirebaseUser.uid) {
                                reportList.add(ds.getValue(lodgereportmodel::class.java)!!)
                            }
                        } else if (chip_default.isChecked && chip_private.isChecked && chip_all.isChecked) {
                            if (ds.getValue(lodgereportmodel::class.java)!!.complaintDetails == "private") {
                                reportList.add(ds.getValue(lodgereportmodel::class.java)!!)
                            }
                        } else if (chip_default.isChecked && chip_public.isChecked && chip_all.isChecked) {
                            if (ds.getValue(lodgereportmodel::class.java)!!.complaintDetails == "public") {
                                reportList.add(ds.getValue(lodgereportmodel::class.java)!!)
                            }
                        } else if (chip_default.isChecked && chip_user.isChecked) {
                            if (ds.getValue(lodgereportmodel::class.java)!!.id == mFirebaseUser.uid) {
                                reportList.add(ds.getValue(lodgereportmodel::class.java)!!)
                            }
                        } else if (chip_default.isChecked && chip_all.isChecked) {
                                reportList.add(ds.getValue(lodgereportmodel::class.java)!!)
                        } else {
                            displayDefaultEmptyData()
                        }
                    }
                    val trackReportAdapter = trackreportadapter(reportList)
                    mrecylerview.adapter = trackReportAdapter
                }
            }

            override fun onCancelled(p0: DatabaseError) {
                // Handle Error
            }
        })
    }

    // Pending Logic
    // Filter Firebase Data
    private fun displayPendingData() {

        // Store value to Array List
        reference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(p0: DataSnapshot) {
                if (p0.exists()) {
                    reportList = ArrayList()

                    for(ds in p0.children) {

                        if (chip_pending.isChecked && chip_private.isChecked && chip_user.isChecked) {
                            if (ds.getValue(lodgereportmodel::class.java)!!.complaintStatus == "pending" &&
                                ds.getValue(lodgereportmodel::class.java)!!.complaintDetails == "private" &&
                                ds.getValue(lodgereportmodel::class.java)!!.id == mFirebaseUser.uid) {
                                reportList.add(ds.getValue(lodgereportmodel::class.java)!!)
                            }
                        } else if (chip_pending.isChecked && chip_public.isChecked && chip_user.isChecked){
                            if (ds.getValue(lodgereportmodel::class.java)!!.complaintStatus == "pending" &&
                                ds.getValue(lodgereportmodel::class.java)!!.complaintDetails == "public" &&
                                ds.getValue(lodgereportmodel::class.java)!!.id == mFirebaseUser.uid) {
                                reportList.add(ds.getValue(lodgereportmodel::class.java)!!)
                            }
                        } else if (chip_pending.isChecked && chip_private.isChecked && chip_all.isChecked){
                            if (ds.getValue(lodgereportmodel::class.java)!!.complaintStatus == "pending" &&
                                ds.getValue(lodgereportmodel::class.java)!!.complaintDetails == "private") {
                                reportList.add(ds.getValue(lodgereportmodel::class.java)!!)
                            }
                        } else if (chip_pending.isChecked && chip_public.isChecked && chip_all.isChecked){
                                if (ds.getValue(lodgereportmodel::class.java)!!.complaintStatus == "pending" &&
                                    ds.getValue(lodgereportmodel::class.java)!!.complaintDetails == "public") {
                                    reportList.add(ds.getValue(lodgereportmodel::class.java)!!)
                                }
                        } else if (chip_pending.isChecked && chip_user.isChecked) {
                                if (ds.getValue(lodgereportmodel::class.java)!!.complaintStatus == "pending" &&
                                    ds.getValue(lodgereportmodel::class.java)!!.id == mFirebaseUser.uid) {
                                    reportList.add(ds.getValue(lodgereportmodel::class.java)!!)
                                }
                            } else if (chip_pending.isChecked && chip_all.isChecked) {
                                if (ds.getValue(lodgereportmodel::class.java)!!.complaintStatus == "pending") {
                                    reportList.add(ds.getValue(lodgereportmodel::class.java)!!)
                                }
                            } else {
                                displayDefaultEmptyData()
                            }
                    }
                    val trackReportAdapter = trackreportadapter(reportList)
                    mrecylerview.adapter = trackReportAdapter
                }
            }

            override fun onCancelled(p0: DatabaseError) {
                // Handle Error
            }
        })
    }

    // Processing Logic
    // Filter Firebase Data
    private fun displayProcessingData() {

        // Store value to Array List
        reference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(p0: DataSnapshot) {
                if (p0.exists()) {
                    reportList = ArrayList()

                    for(ds in p0.children) {

                        if (chip_processing.isChecked && chip_private.isChecked && chip_user.isChecked) {
                            if (ds.getValue(lodgereportmodel::class.java)!!.complaintStatus == "processing" &&
                                ds.getValue(lodgereportmodel::class.java)!!.complaintDetails == "private" &&
                                ds.getValue(lodgereportmodel::class.java)!!.id == mFirebaseUser.uid) {
                                reportList.add(ds.getValue(lodgereportmodel::class.java)!!)
                            }
                        } else if (chip_processing.isChecked && chip_public.isChecked && chip_user.isChecked) {
                            if (ds.getValue(lodgereportmodel::class.java)!!.complaintStatus == "processing" &&
                                ds.getValue(lodgereportmodel::class.java)!!.complaintDetails == "public" &&
                                ds.getValue(lodgereportmodel::class.java)!!.id == mFirebaseUser.uid) {
                                reportList.add(ds.getValue(lodgereportmodel::class.java)!!)
                            }
                        } else if (chip_processing.isChecked && chip_private.isChecked && chip_all.isChecked) {
                            if (ds.getValue(lodgereportmodel::class.java)!!.complaintStatus == "processing" &&
                                ds.getValue(lodgereportmodel::class.java)!!.complaintDetails == "private") {
                                reportList.add(ds.getValue(lodgereportmodel::class.java)!!)
                            }
                        } else if (chip_processing.isChecked && chip_public.isChecked && chip_all.isChecked) {
                            if (ds.getValue(lodgereportmodel::class.java)!!.complaintStatus == "processing" &&
                                ds.getValue(lodgereportmodel::class.java)!!.complaintDetails == "public") {
                                reportList.add(ds.getValue(lodgereportmodel::class.java)!!)
                            }
                        } else if (chip_processing.isChecked && chip_user.isChecked) {
                            if (ds.getValue(lodgereportmodel::class.java)!!.complaintStatus == "processing" &&
                                ds.getValue(lodgereportmodel::class.java)!!.id == mFirebaseUser.uid) {
                                reportList.add(ds.getValue(lodgereportmodel::class.java)!!)
                            }
                        } else if (chip_processing.isChecked && chip_all.isChecked) {
                            if (ds.getValue(lodgereportmodel::class.java)!!.complaintStatus == "processing") {
                                reportList.add(ds.getValue(lodgereportmodel::class.java)!!)
                            }
                        } else {
                            displayDefaultEmptyData()
                        }
                    }
                    val trackReportAdapter = trackreportadapter(reportList)
                    mrecylerview.adapter = trackReportAdapter
                }
            }

            override fun onCancelled(p0: DatabaseError) {
                // Handle Error
            }
        })
    }

    // Completed Logic
    // Filter Firebase Data
    private fun displayCompletedData() {

        // Store value to Array List
        reference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(p0: DataSnapshot) {
                if (p0.exists()) {
                    reportList = ArrayList()

                    for(ds in p0.children) {

                        if (chip_completed.isChecked && chip_private.isChecked && chip_user.isChecked) {
                            if (ds.getValue(lodgereportmodel::class.java)!!.complaintStatus == "completed" &&
                                ds.getValue(lodgereportmodel::class.java)!!.complaintDetails == "private" &&
                                ds.getValue(lodgereportmodel::class.java)!!.id == mFirebaseUser.uid) {
                                reportList.add(ds.getValue(lodgereportmodel::class.java)!!)
                            }
                        } else if (chip_completed.isChecked && chip_public.isChecked && chip_user.isChecked) {
                            if (ds.getValue(lodgereportmodel::class.java)!!.complaintStatus == "completed" &&
                                ds.getValue(lodgereportmodel::class.java)!!.complaintDetails == "public" &&
                                ds.getValue(lodgereportmodel::class.java)!!.id == mFirebaseUser.uid) {
                                reportList.add(ds.getValue(lodgereportmodel::class.java)!!)
                            }
                        } else if (chip_completed.isChecked && chip_private.isChecked && chip_all.isChecked) {
                            if (ds.getValue(lodgereportmodel::class.java)!!.complaintStatus == "completed" &&
                                ds.getValue(lodgereportmodel::class.java)!!.complaintDetails == "private") {
                                reportList.add(ds.getValue(lodgereportmodel::class.java)!!)
                            }
                        } else if (chip_completed.isChecked && chip_public.isChecked && chip_all.isChecked) {
                            if (ds.getValue(lodgereportmodel::class.java)!!.complaintStatus == "completed" &&
                                ds.getValue(lodgereportmodel::class.java)!!.complaintDetails == "public") {
                                reportList.add(ds.getValue(lodgereportmodel::class.java)!!)
                            }
                        } else if (chip_completed.isChecked && chip_user.isChecked) {
                            if (ds.getValue(lodgereportmodel::class.java)!!.complaintStatus == "completed" &&
                                ds.getValue(lodgereportmodel::class.java)!!.id == mFirebaseUser.uid) {
                                reportList.add(ds.getValue(lodgereportmodel::class.java)!!)
                            }
                        } else if (chip_completed.isChecked && chip_all.isChecked) {
                            if (ds.getValue(lodgereportmodel::class.java)!!.complaintStatus == "completed") {
                                reportList.add(ds.getValue(lodgereportmodel::class.java)!!)
                            }
                        } else {
                            displayDefaultEmptyData()
                        }
                    }
                    val trackReportAdapter = trackreportadapter(reportList)
                    mrecylerview.adapter = trackReportAdapter
                }
            }

            override fun onCancelled(p0: DatabaseError) {
                // Handle Error
            }
        })
    }

    override fun onClick(v: View) {
        when(v.id) {
            R.id.chip_default -> {
                filterFirebaseDatabase()
            }
            R.id.chip_pending -> {
                filterFirebaseDatabase()
            }
            R.id.chip_processing -> {
                filterFirebaseDatabase()
            }
            R.id.chip_completed -> {
                filterFirebaseDatabase()
            }
            R.id.chip_private -> {
                filterFirebaseDatabase()
            }
            R.id.chip_public -> {
                filterFirebaseDatabase()
            }
            R.id.chip_user -> {
                filterFirebaseDatabase()
            }
            R.id.chip_all -> {
                filterFirebaseDatabase()
            }
        }
    }
}