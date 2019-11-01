package com.example.ifuturus

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.Toast
import com.google.android.gms.tasks.Continuation
import com.google.android.gms.tasks.Task
import com.google.android.material.chip.Chip
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask
import kotlinx.android.synthetic.main.activity_lodge_report.*
import java.io.IOException
import java.util.*
import android.graphics.Bitmap
import android.location.*
import com.google.firebase.ml.vision.common.FirebaseVisionImage
import android.util.Log
import com.google.firebase.ml.vision.FirebaseVision
import com.google.firebase.ml.vision.label.FirebaseVisionImageLabel
import com.google.firebase.ml.vision.label.FirebaseVisionOnDeviceImageLabelerOptions
import android.os.Build
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices

private const val PERMISSION_REQUEST = 10

class LodgeReportActivity : AppCompatActivity(), View.OnClickListener {

    // Declare Variables
    private val PICK_IMAGE_REQUEST = 71
    private var filePath: Uri? = null
    private var firebaseStore: FirebaseStorage? = null
    private var storageReference: StorageReference? = null

    // Permission to Access Location
    private var permissions = arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION)

    // Get Location
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_lodge_report)

        // Set value
        firebaseStore = FirebaseStorage.getInstance()
        storageReference = FirebaseStorage.getInstance().reference

        btnAddImageorVideo.setOnClickListener(this)
        btnSubmit.setOnClickListener(this)
        chipGroup2.setOnClickListener(this)
        btnChangeLocation.setOnClickListener(this)

        // Get Location
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
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
                    Log.d("Upload Tasks", "Handle Failures")
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
                analyzeImage(bitmap)
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }

    // Image Labeling Firebase ML Kit - Set Confidence Level 0.5F
    private fun analyzeImage(bitmap: Bitmap) {
        val firebaseVisionImage = FirebaseVisionImage.fromBitmap(bitmap)
        val options = FirebaseVisionOnDeviceImageLabelerOptions.Builder()
            .setConfidenceThreshold(0.5F)
            .build()
        val labelDetector = FirebaseVision.getInstance().getOnDeviceImageLabeler(options)
        labelDetector.processImage(firebaseVisionImage)
            .addOnSuccessListener {
                val mutableImage = bitmap.copy(Bitmap.Config.ARGB_8888, true)

                labelImage(it, mutableImage)

            }
            .addOnFailureListener {
                Toast.makeText(this, "There was some error", Toast.LENGTH_SHORT).show()
            }
    }

    // Generate Image Label
    private fun labelImage(labels: List<FirebaseVisionImageLabel>?, image: Bitmap?) {
        if (labels == null || image == null) {
            Toast.makeText(this, "There was some error", Toast.LENGTH_SHORT).show()
            return
        }

        for (label in labels) {

            Log.d("Image Labels", "Text: ${label.text}, Confidence: ${label.confidence}")

            val chip = Chip(chipGroup2.context)
            chip.text = "${label.text}"

            // necessary to get single selection working
            chip.isClickable = true
            chip.isCloseIconVisible = true
            chip.setOnCloseIconClickListener {
                chipGroup2.removeView(it)
            }

            chipGroup2.addView(chip)
        }
    }

    private fun resetChipGroup2() {
        chipGroup2.removeAllViews()
    }

    // Location Permission
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISSION_REQUEST) {
            var allSuccess = true
            for (i in permissions.indices) {
                if (grantResults[i] == PackageManager.PERMISSION_DENIED) {
                    allSuccess = false
                    val requestAgain = Build.VERSION.SDK_INT >= Build.VERSION_CODES.P && shouldShowRequestPermissionRationale(permissions[i])
                    if (requestAgain) {
                        Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(this, "Go to settings and enable the permission", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    // Get Location Function
    @SuppressLint("MissingPermission")
    private fun obtieneLocalizacion(){
        // Change Button Text to Change Location
        btnChangeLocation.setText("Change Location")

        fusedLocationClient.lastLocation
            .addOnSuccessListener { location: Location? ->
                // Convert to Location Address
                val geocoder : Geocoder = Geocoder(this, Locale.getDefault())
                val addresses : List<Address>

                addresses = geocoder.getFromLocation(location!!.latitude, location!!.longitude, 1) // Here 1 represent max location result to returned, by documents it recommended 1 to 5

                // Display Location Address
                textViewDisplayLocation.setText("Latitude: ${location?.latitude}, Longitude: ${location?.longitude}")
                textViewDisplayLocation.append("\n\nLocation Address:\n${addresses.get(0).getAddressLine(0)}")
            }
    }

// Handle View On Click Action
    override fun onClick(v: View) {
    when (v.id) {
        R.id.btnAddImageorVideo -> {
            resetChipGroup2()
            launchGallery()
            displayImage()
    }
        R.id.btnChangeLocation -> {
            requestPermissions(permissions, PERMISSION_REQUEST)
            obtieneLocalizacion()
        }
        R.id.btnSubmit -> {
            uploadImage()
        }
    }
    }
}
