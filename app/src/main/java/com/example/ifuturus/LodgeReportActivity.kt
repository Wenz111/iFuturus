package com.example.ifuturus

import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.google.android.gms.tasks.Continuation
import com.google.android.gms.tasks.Task
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask
import kotlinx.android.synthetic.main.activity_lodge_report.*
import java.io.IOException
import java.util.*

class LodgeReportActivity : AppCompatActivity(), View.OnClickListener {

    // Declare Variables
    private val PICK_IMAGE_REQUEST = 71
    private var filePath: Uri? = null
    private var firebaseStore: FirebaseStorage? = null
    private var storageReference: StorageReference? = null

//    // Get Value From View
//    lateinit var mchipPulic : Chip
//    lateinit var mchipPrivate : Chip
//    lateinit var meditTextComplaintNotes : EditText
//    lateinit var mlocationText : EditText
//    lateinit var mchipGroup2 : ChipGroup

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_lodge_report)

        // Set value
        firebaseStore = FirebaseStorage.getInstance()
        storageReference = FirebaseStorage.getInstance().reference

        btnAddImageorVideo.setOnClickListener(this)
        btnSubmit.setOnClickListener(this)

//        mchipPulic = findViewById(R.id.chippublic)
//        mchipPrivate = findViewById(R.id.chipprivate)
//
//        meditTextComplaintNotes = findViewById(R.id.editTextComplaintNotes)
//        mlocationText = findViewById(R.id.textViewDisplayLocation)
//        mchipGroup2 = findViewById(R.id.chipGroup2)
    }

    // Call Gallery
    private fun launchGallery() {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST)
    }

    // Display Image and Replace Add Image button to change/update existing image
    private fun displayImage() {
        // Show Images
        imageView.visibility = View.VISIBLE

        // Change Add Image button to Change/Update Image
        btnAddImageorVideo.setText("Change Image or Video")
    }

    private fun addUploadRecordToDb(uri: String){
        val db = FirebaseFirestore.getInstance()

        val data = HashMap<String, Any>()
        data["imageUrl"] = uri

        db.collection("posts")
            .add(data)
            .addOnSuccessListener { documentReference ->
                Toast.makeText(this, "Saved to DB", Toast.LENGTH_LONG).show()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Error saving to DB", Toast.LENGTH_LONG).show()
            }
    }

    private fun uploadImage(){
        Toast.makeText(this, "filePath = $filePath", Toast.LENGTH_SHORT).show()
        if(filePath != null){
            val ref = storageReference?.child("lodgereport/" + UUID.randomUUID().toString())
            val uploadTask = ref?.putFile(filePath!!)

            val urlTask = uploadTask?.continueWithTask(Continuation<UploadTask.TaskSnapshot, Task<Uri>> { task ->
                if (!task.isSuccessful) {
                    task.exception?.let {
                        throw it
                    }
                }
                return@Continuation ref.downloadUrl
            })?.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val downloadUri = task.result
                    addUploadRecordToDb(downloadUri.toString())
                } else {
                    // Handle failures
                }
            }?.addOnFailureListener{

            }
        }else{
            Toast.makeText(this, "Please Upload an Image", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK) {
            if(data == null || data.data == null){
                return
            }

            filePath = data.data
            try {
                val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, filePath)
                imageView.setImageBitmap(bitmap)
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }

// Handle View On Click Action
    override fun onClick(v: View) {
    when (v.id) {
        R.id.btnAddImageorVideo -> {
            launchGallery()
            displayImage()
    }
//        R.id.btnChangeLocation -> {}
        R.id.btnSubmit -> {
            uploadImage()
        }
    }
    }
}
