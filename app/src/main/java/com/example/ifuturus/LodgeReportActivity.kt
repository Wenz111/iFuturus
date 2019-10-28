package com.example.ifuturus

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup

class LodgeReportActivity : AppCompatActivity(), View.OnClickListener {

    // Declare Button
    lateinit var mbtnAddImageorVideo : Button
    lateinit var mbtnChangeLocation : Button
    lateinit var mbtnsubmit : Button

//    // Get Value From View
//    lateinit var mchipPulic : Chip
//    lateinit var mchipPrivate : Chip
//    lateinit var meditTextComplaintNotes : EditText
//    lateinit var mlocationText : EditText
//    lateinit var mchipGroup2 : ChipGroup

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_lodge_report)

        mbtnAddImageorVideo = findViewById(R.id.btnAddImageorVideo)
        mbtnChangeLocation = findViewById(R.id.btnChangeLocation)
        mbtnsubmit = findViewById(R.id.btnSubmit)

//        mchipPulic = findViewById(R.id.chippublic)
//        mchipPrivate = findViewById(R.id.chipprivate)
//
//        meditTextComplaintNotes = findViewById(R.id.editTextComplaintNotes)
//        mlocationText = findViewById(R.id.textViewDisplayLocation)
//        mchipGroup2 = findViewById(R.id.chipGroup2)
    }

// Handle View On Click Action
    override fun onClick(v: View?) {
//    when (v?.id) {
//        R.id.btnAddImageorVideo -> {}
//        R.id.btnChangeLocation -> {}
//        R.id.btnSubmit -> {}
//    }
    }
}
