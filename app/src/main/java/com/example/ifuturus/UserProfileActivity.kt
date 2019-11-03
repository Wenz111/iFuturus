package com.example.ifuturus

import android.Manifest
import android.app.Activity
import android.app.ProgressDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.text.Editable
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.webkit.MimeTypeMap
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.example.ifuturus.model.userprofilemodel
import com.google.android.gms.tasks.Continuation
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.Task
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask
import kotlinx.android.synthetic.main.activity_user_profile.*
import java.util.*
import java.util.regex.Pattern

class UserProfileActivity : AppCompatActivity(), View.OnClickListener {

    // Handle storing and retrieving of key value pairs
    private lateinit var mDatabaseReference: DatabaseReference

    // Firebase instance variables
    private lateinit var mFirebaseAuth: FirebaseAuth
    private var mFirebaseUser: FirebaseUser? = null

    // Storage Reference
    private lateinit var storageReference: StorageReference

    // Get Image Request
    private val IMAGE_REQUEST = 100
    private val PICK_IMAGE_REQUEST = 200

    // 1 Image Variable
    private var mImageUri: Uri? = null

    // Database Reference
    private lateinit var reference: DatabaseReference
//    private lateinit var  uploadTask: UploadTask

    // Store Temporary Value
    var mTempEmail : String? = null
    var mTempName : String? = null
    var mTempGender : String? = null
    var mTempDOB : String? = null
    var mTempPhoneNumber : String? = null
    var mTempIC : String? = null
    var mphotoUrl : String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_profile)

        // Request Permission
        requestAppPermissions()

        // Set OnClickListener
        editProfile_EditButton.setOnClickListener(this)
        editProfile_SaveButton.setOnClickListener(this)

        // Set Image Clickable but set to False first
        userImage.setOnClickListener(this)
        userImage.isEnabled = false

        // Get current user
        mFirebaseAuth = FirebaseAuth.getInstance()
        mFirebaseUser = mFirebaseAuth.currentUser
        mDatabaseReference = FirebaseDatabase.getInstance().getReference("userprofile")

        // Storage Reference
        storageReference = FirebaseStorage.getInstance().getReference("uploads")

        // Database Reference
        reference = FirebaseDatabase.getInstance().getReference("userprofile").child(mFirebaseUser!!.uid)

        mTempEmail = mFirebaseUser!!.email.toString()

        reference.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
                // Handle Cancel Action
            }

            override fun onDataChange(dataSnapshot: DataSnapshot) {
                // Handle Data Change Action
                val user = dataSnapshot.getValue(userprofilemodel::class.java)
                // Assign Data to User Profile
                editProfile_SetName.text = user?.name
                mTempName = user?.name
                editProfile_SetPhoneNumber.text = user?.phonenumber
                mTempPhoneNumber = user?.phonenumber
                editProfile_SetEmail.text = user?.email
                editProfile_SetGender.text = user?.gender
                mTempGender = user?.gender
                editProfile_SetDOB.text = user?.dob
                mTempDOB = user?.dob
                editProfile_SetIC.text = user?.ic
                mTempIC = user?.ic
                mphotoUrl =user?.photoUrl

                // Validate Photo
                if (user?.photoUrl.equals("default")) {
                    userImage.setImageResource(R.drawable.pikademo)
                } else {
                    Glide.with(applicationContext).load(user?.photoUrl).into(userImage)
                }
            }
        })
    }

    // Request App Permission
    private fun requestAppPermissions() {
        if (hasReadPermissions() && hasWritePermissions()) {
            return
        }

        ActivityCompat.requestPermissions(
            this,
            arrayOf(
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ), IMAGE_REQUEST
        ) // your request code
    }

    private fun hasReadPermissions(): Boolean {
        return ContextCompat.checkSelfPermission(
            baseContext,
            Manifest.permission.READ_EXTERNAL_STORAGE
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun hasWritePermissions(): Boolean {
        return ContextCompat.checkSelfPermission(
            baseContext,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun EditUserProfile() {
        // Make edit button gone and show save button
        editProfile_EditButton.visibility = View.GONE
        editProfile_SaveButton.visibility = View.VISIBLE

        userImage.isEnabled = true

        // Show User Value Gone
        editProfile_SetName.visibility = View.GONE
        editProfile_SetPhoneNumber.visibility = View.GONE
        editProfile_SetEmail.visibility = View.GONE
        editProfile_SetGender.visibility = View.GONE
        editProfile_SetDOB.visibility = View.GONE
        editProfile_SetIC.visibility = View.GONE

        // Show User Edit Field
        textInputLayout_EditName.visibility = View.VISIBLE
        textInputLayout_EditPhoneNumber.visibility = View.VISIBLE
        textInputLayout_EditGender.visibility = View.VISIBLE
        textInputLayout_EditDOB.visibility = View.VISIBLE
        editProfile_IC.visibility = View.VISIBLE

        Log.d("Test Empty Field", "mTempName: $mTempName")
        // Display Empty Field to allow user fill in
        // Name Field
        Toast.makeText(this,mTempName, Toast.LENGTH_SHORT).show()
        if (mTempName.equals("Name")) {
            editProfile_name.setText("")
        } else {
            editProfile_name.setText(mTempName)
        }
        // Phone Number Field
        if (mTempPhoneNumber.equals("Phone Number")) {
            editProfile_phoneNumber.setText("")
        } else {
            editProfile_phoneNumber.setText(mTempPhoneNumber)
        }
        // Gender Field
        if (mTempGender.equals("Gender")) {
            editProfile_gender.setText("")
        } else {
            editProfile_gender.setText(mTempGender)
        }
        // Date of Birth Field
        if (mTempDOB.equals("Date of Birth")) {
            editProfile_DOB.setText("dd/mm/yyyy")
        } else {
            editProfile_DOB.setText(mTempDOB)
        }
        // IC Field
        if (mTempIC.equals("IC")) {
            editProfile_IC.setText("")
        } else {
            editProfile_IC.setText(mTempIC)
        }
    }

    private fun validateUserProfileInput(): Boolean {
        var valid = true
        val pattern = Pattern.compile("^(\\+?6?01)[0-46-9]-*[0-9]{7,8}$")

        val user_Name = editProfile_name.text
        if (TextUtils.isEmpty(user_Name)) {
            textInputLayout_EditName.setError(resources.getString(R.string.required))
            valid = false
        } else {
            textInputLayout_EditName.setError(null)
        }

        val Phone_Number = editProfile_phoneNumber.text
        if (TextUtils.isEmpty(Phone_Number)) {
            textInputLayout_EditPhoneNumber.setError(resources.getString(R.string.required))
            valid = false
        } else if (!pattern.matcher(Phone_Number).matches()) {
            textInputLayout_EditPhoneNumber.setError(resources.getString(R.string.invalid_phone_format))
            valid = false
        } else {
            textInputLayout_EditPhoneNumber.setError(null)
        }

        return valid
    }

    private fun openImage() {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST)
    }

    private fun uploadImage() {
        Log.d("User Profile Activity", "Check mImageUrl: $mImageUri")
        if (mImageUri == null) return

        // storageReference = FirebaseStorage.getInstance().getReference("uploads")

        val filename = UUID.randomUUID().toString()
        val fileReference = FirebaseStorage.getInstance().getReference("/uploads/$filename")

        fileReference.putFile(mImageUri!!)
            .addOnSuccessListener {
                fileReference.downloadUrl.addOnSuccessListener {

                    reference = FirebaseDatabase.getInstance().getReference("userprofile").child(mFirebaseUser!!.uid)
                    val map = HashMap<String, Any>()
                    map["photoUrl"] = it.toString()
                    reference.updateChildren(map)
                }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK
            && data != null) {

            mImageUri = data.data

            val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, mImageUri)
            val bitmapDrawable = BitmapDrawable(bitmap)
            userImage.setImageDrawable(bitmapDrawable)
        }
    }

    fun SaveToDatabase() {
        // Save the data to the Database
        // Pass all the parameters to store into the database
        val changeUserProfile = userprofilemodel(
            mFirebaseUser!!.uid,
            mTempName,
            mTempEmail,
            mTempGender,
            mTempDOB,
            mTempPhoneNumber,
            mTempIC,
            mphotoUrl
        )
        mDatabaseReference.child(mFirebaseUser!!.uid).setValue(changeUserProfile)
    }

    private fun ShowUserProfile() {
        // Validate input before saving
        if (!validateUserProfileInput()) {
            return
        }

        // Save the Changes to Text View
        editProfile_SetName.setText(editProfile_name.text.toString())
        editProfile_SetPhoneNumber.setText(editProfile_phoneNumber.text.toString())
        editProfile_SetGender.setText(editProfile_gender.text.toString())
        editProfile_SetDOB.setText(editProfile_DOB.text.toString())
        editProfile_SetIC.setText(editProfile_IC.text.toString())

        // Save as Local Variable
        mTempName = editProfile_SetName.text.toString().trim()
        mTempPhoneNumber = editProfile_SetPhoneNumber.text.toString().trim()
        mTempGender = editProfile_SetGender.text.toString().trim()
        mTempDOB = editProfile_SetDOB.text.toString().trim()
        mTempIC = editProfile_SetIC.text.toString().trim()
        if (mphotoUrl == null) {
            mphotoUrl = "default"
        }

        // Save to Database
        SaveToDatabase()

        // Make edit button gone and show save button
        editProfile_EditButton.visibility = View.VISIBLE
        editProfile_SaveButton.visibility = View.GONE

        userImage.isEnabled = false

        // Show User Value Gone
        editProfile_SetName.visibility = View.VISIBLE
        editProfile_SetPhoneNumber.visibility = View.VISIBLE
        editProfile_SetEmail.visibility = View.VISIBLE
        editProfile_SetGender.visibility = View.VISIBLE
        editProfile_SetDOB.visibility = View.VISIBLE
        editProfile_SetIC.visibility = View.VISIBLE

        // Show User Edit Field
        textInputLayout_EditName.visibility = View.GONE
        textInputLayout_EditPhoneNumber.visibility = View.GONE
        textInputLayout_EditGender.visibility = View.GONE
        textInputLayout_EditDOB.visibility = View.GONE
        editProfile_IC.visibility = View.GONE
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.editProfile_EditButton -> {
                // Perform Edit Settings Function
                // Make Edit Field Visible
                EditUserProfile()
            }
            R.id.editProfile_SaveButton -> {
                ShowUserProfile()
                uploadImage()
            }
            R.id.userImage -> {
                openImage()
                Toast.makeText(
                    this,
                    resources.getString(R.string.image_clicked),
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }
}
