package com.example.ifuturus

import android.content.DialogInterface
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.example.ifuturus.model.feedbackmodel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_settings.*

class SettingsActivity : AppCompatActivity(), View.OnClickListener {

    // Firebase instance variables
    private lateinit var mFirebaseAuth: FirebaseAuth
    private var mFirebaseUser: FirebaseUser? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        // Set OnClickListener
        button_settings_feedback.setOnClickListener(this)
        button_settings_close_feedback.setOnClickListener(this)
        myFeedbackSendButton.setOnClickListener(this)
        button_settings_deleteUserProfile.setOnClickListener(this)

        // Get current user
        mFirebaseAuth = FirebaseAuth.getInstance()
        mFirebaseUser = mFirebaseAuth.currentUser
    }

    private fun sendFeedback() {
        myFeedbackContainer.visibility = View.VISIBLE
        button_settings_close_feedback.visibility = View.VISIBLE
        button_settings_feedback.visibility = View.GONE
        button_settings_deleteUserProfile.visibility = View.GONE
    }

    private fun closeFeedbackForm() {
        myFeedbackContainer.visibility = View.GONE
        button_settings_close_feedback.visibility = View.GONE
        button_settings_feedback.visibility = View.VISIBLE
        button_settings_deleteUserProfile.visibility = View.VISIBLE
    }

    private fun saveFeedbackToDatabase() {

        // Get Text from Feedback Details
        val feedbackdetails = myFeedbackEditText.text.toString()

        if(feedbackdetails == "") {
            return
        }

        val reference = FirebaseDatabase.getInstance().getReference("userfeedback").push()

        val feedbackDetails = feedbackmodel(mFirebaseUser!!.uid, feedbackdetails)

        reference.setValue(feedbackDetails)
            .addOnSuccessListener {
                Log.d("User Feedback Form", "Successfully saved value to database")
                myFeedbackEditText.setText("")
                closeFeedbackForm()
                Toast.makeText(this, "Thank you for your feedback!", Toast.LENGTH_LONG).show()
            }
    }

    private fun deleteUser() {

        // Confirm to delete User Profile
        val builder = AlertDialog.Builder(this@SettingsActivity)
        builder.setTitle("Are you sure you want to Delete this User Profile?")
        builder.setMessage("Confirm to Delete this User Profile?\nThis action cannot be undo.")
            .setCancelable(false)
            .setPositiveButton(R.string.yes,
                DialogInterface.OnClickListener { dialogInterface, i ->
                    // Delete User Profile From Firebase Database
                    mFirebaseUser!!.delete()

                    val intent = Intent(Intent.ACTION_MAIN)
                    intent.addCategory(Intent.CATEGORY_HOME)
                    startActivity(intent)
                    finish()
                })
            .setNegativeButton(R.string.no,
                DialogInterface.OnClickListener { dialogInterface, i -> dialogInterface.cancel() })
            val alertQuit = builder.create()
            alertQuit.show()
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.button_settings_feedback -> {
                sendFeedback()
            }
            R.id.myFeedbackSendButton -> {
                saveFeedbackToDatabase()
            }
            R.id.button_settings_close_feedback -> {
                closeFeedbackForm()
            }
            R.id.button_settings_deleteUserProfile -> {
                // Delete User Profile From Firebase
                deleteUser()
            }
        }
    }
}
