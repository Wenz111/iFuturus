package com.example.ifuturus

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
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
import com.example.ifuturus.model.userprofilemodel
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import android.provider.Settings
import com.example.ifuturus.model.lodgereportmodel
import java.text.SimpleDateFormat

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
    lateinit var locationManager: LocationManager
    private var hasGps = false
    private var hasNetwork = false
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var locationGps : Location? = null
    private var locationNetwork : Location? = null

    // Handle storing and retrieving of key value pairs
    private lateinit var mUserProfileDatabaseReference: DatabaseReference
    private lateinit var mLodgeReportDatabaseReference: DatabaseReference

    // Firebase instance variables
    private lateinit var mFirebaseAuth: FirebaseAuth
    private var mFirebaseUser: FirebaseUser? = null

    // Database Reference
    private lateinit var reference: DatabaseReference

    // Store Temporary Value
    private var mTempEmail : String? = null
    private var mTempName : String? = null
    private var mTempGender : String? = null

    // Store Category Value
    private var mTempCategory : String? = null

    // Hold Unique Report ID
    private val reportid = UUID.randomUUID().toString()

    // Hold temporary latitude and longitude
    private var latitude : String? = null
    private var longitude : String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_lodge_report)

        // Request Permission
        requestPermissions(permissions, PERMISSION_REQUEST)

        // Set value
        firebaseStore = FirebaseStorage.getInstance()
        storageReference = FirebaseStorage.getInstance().reference

        btnAddImageorVideo.setOnClickListener(this)
        btnSubmit.setOnClickListener(this)
        chipGroup.setOnClickListener(this)
        chippublic.setOnClickListener(this)
        chipprivate.setOnClickListener(this)
        chipGroup2.setOnClickListener(this)
        btnChangeLocation.setOnClickListener(this)

        // Get Location
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        // Get current user
        mFirebaseAuth = FirebaseAuth.getInstance()
        mFirebaseUser = mFirebaseAuth.currentUser
        mUserProfileDatabaseReference = FirebaseDatabase.getInstance().getReference("userprofile")
        mLodgeReportDatabaseReference = FirebaseDatabase.getInstance().getReference("lodgereport")

        // Database Reference
        reference = FirebaseDatabase.getInstance().getReference("userprofile").child(mFirebaseUser!!.uid)

        // Get User Information
        mTempEmail = mFirebaseUser!!.email.toString()

        reference.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
                // Handle Cancel Action
            }

            override fun onDataChange(dataSnapshot: DataSnapshot) {
                // Handle Data Change Action
                val user = dataSnapshot.getValue(userprofilemodel::class.java)
                // Get User Personal Information
                mTempName = user?.name
                mTempGender = user?.gender
            }
        })
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
        btnAddImageorVideo.setText("Change Image")
    }

/*    private fun addUploadRecordToDb(uri: String){
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
    }*/

    private fun uploadImage(){
        Log.d("Lodge Report Activity", "Check filepath: $filePath")
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

                    // Get Uri link
                    val downloadUri = task.result

                    // Store to the Lodge Report Real-time Database
                    mLodgeReportDatabaseReference = FirebaseDatabase.getInstance().getReference("lodgereport").child(reportid)
                    val map = HashMap<String, Any>()
                    map["photoUrl"] = downloadUri.toString()
                    mLodgeReportDatabaseReference.updateChildren(map)
                        .addOnSuccessListener {
                            Toast.makeText(this, "Complaint Report has been Successfully Submitted!", Toast.LENGTH_LONG).show()
                            val intent = Intent(this, MainActivity::class.java)
                            startActivity(intent)
                        }
                        .addOnFailureListener {
                            Toast.makeText(this, "Please try again with a stable internet connection", Toast.LENGTH_LONG).show()
                        }
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

//        mTempCategory = ""

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
//            mTempCategory += "${label.text} "
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

/*    // Get Location Function
    @SuppressLint("MissingPermission")
    private fun obtieneLocalizacion(){
        locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        hasGps = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)

        if(hasGps){
            // Change Button Text to Change Location
            btnChangeLocation.setText("Change Location")

            fusedLocationClient.lastLocation
                .addOnSuccessListener { location: Location? ->
                    // Convert to Location Address
                    val geocoder : Geocoder = Geocoder(this, Locale.getDefault())
                    val addresses : List<Address>

                    addresses = geocoder.getFromLocation(location!!.latitude, location!!.longitude, 1) // Here 1 represent max location result to returned, by documents it recommended 1 to 5

                    // Store longitude and longitude into the temporary variable
                    latitude = location.latitude.toString()
                    longitude = location.longitude.toString()

                    // Display Location Address
                    textViewDisplayLocation.setText("Latitude: ${location?.latitude}, Longitude: ${location?.longitude}")
                    textViewDisplayLocation.append("\n\nLocation Address:\n${addresses.get(0).getAddressLine(0)}")
                }
        } else {
            startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))
        }

    }*/

    @SuppressLint("MissingPermission")
    private fun getLocation() {
        locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        hasGps = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
        hasNetwork = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)

        if (hasGps || hasNetwork) {

            // Check for GPS
            if (hasGps) {
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 0F, object : LocationListener {
                    override fun onLocationChanged(location: Location?) {
                        if (location != null) {
                            locationGps = location

                            // Else get GPS Location
                            // Change Button Text to Change Location
                            btnChangeLocation.setText("Change Location")

                            // Convert to Location Address
                            val geocoder : Geocoder = Geocoder(this@LodgeReportActivity, Locale.getDefault())
                            val addresses : List<Address>

                            addresses = geocoder.getFromLocation(locationGps!!.latitude, locationGps!!.longitude, 1) // Here 1 represent max location result to returned, by documents it recommended 1 to 5

                            // Store longitude and longitude into the temporary variable
                            latitude = locationGps!!.latitude.toString()
                            longitude = locationGps!!.longitude.toString()

                            // Display Location Address
                            textViewDisplayLocation.setText("Latitude: ${locationGps?.latitude}, Longitude: ${locationGps?.longitude}")
                            textViewDisplayLocation.append("\n\nLocation Address:\n${addresses.get(0).getAddressLine(0)}")
                        }
                    }

                    override fun onStatusChanged(p0: String?, p1: Int, p2: Bundle?) {
                    }

                    override fun onProviderEnabled(p0: String?) {
                    }

                    override fun onProviderDisabled(p0: String?) {
                    }

                })

                val localGpsLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
                if (localGpsLocation != null) {
                    locationGps = localGpsLocation
                }
            }

            // Check for Network
            if (hasNetwork) {
                locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 5000, 0F, object : LocationListener {
                    override fun onLocationChanged(location: Location?) {
                        if (location != null) {
                            locationNetwork = location

                            // If Network is more accurate get Network Location
                            // Change Button Text to Change Location
                            btnChangeLocation.setText("Change Location")

                            // Convert to Location Address
                            val geocoder : Geocoder = Geocoder(this@LodgeReportActivity, Locale.getDefault())
                            val addresses : List<Address>

                            addresses = geocoder.getFromLocation(locationNetwork!!.latitude, locationNetwork!!.longitude, 1) // Here 1 represent max location result to returned, by documents it recommended 1 to 5

                            // Store longitude and longitude into the temporary variable
                            latitude = locationNetwork!!.latitude.toString()
                            longitude = locationNetwork!!.longitude.toString()

                            // Display Location Address
                            textViewDisplayLocation.setText("Latitude: ${locationNetwork?.latitude}, Longitude: ${locationNetwork?.longitude}")
                            textViewDisplayLocation.append("\n\nLocation Address:\n${addresses.get(0).getAddressLine(0)}")
                        }
                    }

                    override fun onStatusChanged(p0: String?, p1: Int, p2: Bundle?) {
                    }

                    override fun onProviderEnabled(p0: String?) {
                    }

                    override fun onProviderDisabled(p0: String?) {
                    }

                })

                val localNetworkLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
                if (localNetworkLocation != null) {
                    locationNetwork = localNetworkLocation
                }
            }

            // Check both locationGPS and locationNetwork
            // And take the accurate location
            if (locationGps != null && locationNetwork != null) {
                if (locationGps!!.accuracy > locationNetwork!!.accuracy) {
                    // If Network is more accurate get Network Location
                    // Change Button Text to Change Location
                    btnChangeLocation.setText("Change Location")

                    // Convert to Location Address
                    val geocoder : Geocoder = Geocoder(this, Locale.getDefault())
                    val addresses : List<Address>

                    addresses = geocoder.getFromLocation(locationNetwork!!.latitude, locationNetwork!!.longitude, 1) // Here 1 represent max location result to returned, by documents it recommended 1 to 5

                    // Store longitude and longitude into the temporary variable
                    latitude = locationNetwork!!.latitude.toString()
                    longitude = locationNetwork!!.longitude.toString()

                    // Display Location Address
                    textViewDisplayLocation.setText("Latitude: ${locationNetwork?.latitude}, Longitude: ${locationNetwork?.longitude}")
                    textViewDisplayLocation.append("\n\nLocation Address:\n${addresses.get(0).getAddressLine(0)}")

                } else {
                    // Else get GPS Location
                    // Change Button Text to Change Location
                    btnChangeLocation.setText("Change Location")

                    // Convert to Location Address
                    val geocoder : Geocoder = Geocoder(this, Locale.getDefault())
                    val addresses : List<Address>

                    addresses = geocoder.getFromLocation(locationGps!!.latitude, locationGps!!.longitude, 1) // Here 1 represent max location result to returned, by documents it recommended 1 to 5

                    // Store longitude and longitude into the temporary variable
                    latitude = locationGps!!.latitude.toString()
                    longitude = locationGps!!.longitude.toString()

                    // Display Location Address
                    textViewDisplayLocation.setText("Latitude: ${locationGps?.latitude}, Longitude: ${locationGps?.longitude}")
                    textViewDisplayLocation.append("\n\nLocation Address:\n${addresses.get(0).getAddressLine(0)}")
                }
            }


        } else {
            startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))
        }
    }

    private fun saveToDatabase() {
        var propertyType : String?= null

        if (chippublic.isChecked) {
            propertyType = "public"
        } else {
            propertyType = "private"
        }

        mTempCategory = ""

        for (index in 0 until chipGroup2.childCount) {
            val chip:Chip = chipGroup2.getChildAt(index) as Chip
            mTempCategory += "${chip.text} "
        }

        // Get Today Date
        val sdf = SimpleDateFormat("dd/M/yyyy")
        val currentDate = sdf.format(Date())

        // Get Current Time
        val sdf2 = SimpleDateFormat("hh:mm:ss a")
        val currentTime = sdf2.format(Date())

        // Save the data to the Database
        // Pass all the parameters to store into the database
        val saveToComplaintReportDatabase = lodgereportmodel(
            mFirebaseUser!!.uid,
            mTempName,
            mTempEmail,
            mTempGender,
            propertyType,
            editTextComplaintNotes.text.toString(),
            textViewDisplayLocation.text.toString(),
            mTempCategory,
            "pending",
            reportid,
            currentDate,
            currentTime,
            "",
            latitude,
            longitude
        )
        mLodgeReportDatabaseReference.child(reportid).setValue(saveToComplaintReportDatabase)
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
            getLocation()
        }
        R.id.btnSubmit -> {
            saveToDatabase()
            uploadImage()
            /*Toast.makeText(this, "Successfully Submitted!", Toast.LENGTH_LONG).show()
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)*/
        }
    }
    }
}
