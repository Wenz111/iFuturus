package com.example.ifuturus

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.ifuturus.model.lodgereportmodel
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.firebase.database.*
import com.squareup.picasso.Picasso

class ReportStatusActivity : AppCompatActivity() {

    // Get Reference to the Database
    lateinit var ref: DatabaseReference

    // Initialize variables
    lateinit var mrecylerview : RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_report_status)

        // Get Reference to the Database
        ref = FirebaseDatabase.getInstance().getReference().child("lodgereport")

        // Assign variable
        mrecylerview = findViewById(R.id.reyclerview)
        mrecylerview.setHasFixedSize(true)
        mrecylerview.layoutManager = LinearLayoutManager(this)

        firebaseData()
    }

    fun firebaseData() {
        val option = FirebaseRecyclerOptions.Builder<lodgereportmodel>()
            .setQuery(ref, lodgereportmodel::class.java)
            .setLifecycleOwner(this)
            .build()


        val firebaseRecyclerAdapter = object: FirebaseRecyclerAdapter<lodgereportmodel, MyViewHolder>(option) {


            override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
                val itemView = LayoutInflater.from(this@ReportStatusActivity).inflate(R.layout.activity_lodge_report_status_details,parent,false)
                return MyViewHolder(itemView)
            }

            override fun onBindViewHolder(holder: MyViewHolder, position: Int, model: lodgereportmodel) {
                val reportId = getRef(position).key.toString()

                Log.d("ReportStatusActivity-firebaseData Function", "Place ID: $reportId")

                ref.child(reportId).addValueEventListener(object: ValueEventListener {
                    override fun onCancelled(p0: DatabaseError) {
                        Toast.makeText(this@ReportStatusActivity, "Error Occurred "+ p0.toException(), Toast.LENGTH_SHORT).show()

                    }

                    override fun onDataChange(p0: DataSnapshot) {

                        // Set Value into each view of recycler view
                        holder.txt_name.setText(model.complaintId)
                        Picasso.get().load(model.photoUrl).into(holder.img_vet)

                        // Test On Click Listener
                        holder.img_vet.setOnClickListener(View.OnClickListener {
                            Toast.makeText(this@ReportStatusActivity, "Picture is Clicked: $it", Toast.LENGTH_LONG).show()
                        })
                    }
                })
            }
        }
        mrecylerview.adapter = firebaseRecyclerAdapter
        firebaseRecyclerAdapter.startListening()
    }

    class MyViewHolder(itemView: View?) : RecyclerView.ViewHolder(itemView!!) {
        internal var txt_name: TextView = itemView!!.findViewById<TextView>(R.id.Display_title)
        internal var img_vet: ImageView = itemView!!.findViewById<ImageView>(R.id.Display_img)
    }
}
