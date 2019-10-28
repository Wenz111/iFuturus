package com.example.ifuturus

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.util.Patterns
import android.view.View
import android.widget.Toast
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import java.util.HashMap


class RegisterActivity : AppCompatActivity(), View.OnClickListener {

    // Declare an instance of Firebase Auth
    private var mFirebaseAuth: FirebaseAuth? = null

    // Declare TextInputLayout Field
    private var mlabel_username: TextInputLayout? = null
    private var mlabel_password: TextInputLayout? = null
    private var mlabel_confirm_password: TextInputLayout? = null

    // Declare TextInputEditText Field
    private lateinit var mtext_input_username: TextInputEditText
    private lateinit var mtext_input_password: TextInputEditText
    private lateinit var mtext_input_confirm_password: TextInputEditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        // Assign TextInputLayout Fields
        mlabel_username = findViewById(R.id.label_username)
        mlabel_password = findViewById(R.id.label_password)
        mlabel_confirm_password = findViewById(R.id.label_confirm_password)

        // Assign TextInputEditText Fields
        mtext_input_username = findViewById(R.id.text_input_username)
        mtext_input_password = findViewById(R.id.text_input_password)
        mtext_input_confirm_password = findViewById(R.id.text_input_confirm_password)

        // Button Click Listener
        findViewById<View>(R.id.button_sign_up).setOnClickListener(this)
        findViewById<View>(R.id.button_cancel).setOnClickListener(this)
        findViewById<View>(R.id.button_sign_in).setOnClickListener(this)

        // Start Auth Instance
        mFirebaseAuth = FirebaseAuth.getInstance()
    }

    private fun validateSignUpCredentials(): Boolean {
        var valid = true
        val pattern = Patterns.EMAIL_ADDRESS

        val email = mtext_input_username?.getText()!!.toString()
        if (TextUtils.isEmpty(email)) {
            mlabel_username?.setError(resources.getString(R.string.required))
            valid = false
        } else if (!pattern.matcher(email).matches()) {
            mlabel_username?.setError(resources.getString(R.string.invalid_email_format))
            valid = false
        } else {
            mlabel_username?.setError(null)
        }

        val password = mtext_input_password?.getText()!!.toString()
        if (TextUtils.isEmpty(password)) {
            mlabel_password?.setError(resources.getString(R.string.required))
            valid = false
        } else if (mtext_input_password?.length() < 6) {
            mlabel_password?.setError(resources.getString(R.string.password_format_error_message))
            valid = false
        } else {
            mlabel_password?.setError(null)
        }

        val confirmPassword = mtext_input_confirm_password?.getText()!!.toString()
        if (TextUtils.isEmpty(confirmPassword)) {
            mlabel_confirm_password?.setError(resources.getString(R.string.required))
            valid = false
        } else if (confirmPassword != password) {
            mlabel_confirm_password?.setError(resources.getString(R.string.password_not_match))
            valid = false
        } else if (mtext_input_confirm_password.length() < 6) {
            mlabel_confirm_password?.setError(resources.getString(R.string.password_format_error_message))
            valid = false
        }

        return valid
    }

        private fun createAccount(email:String, password:String) {
        if (!validateSignUpCredentials())
        {
        return
        }

         // START create user with email
        mFirebaseAuth?.createUserWithEmailAndPassword(email, password)?.addOnCompleteListener(this) { task ->
        if (task.isSuccessful) {
        // Sign in success
        val user = mFirebaseAuth?.getCurrentUser()
        val userid = user!!.uid

        val reference: DatabaseReference
        reference = FirebaseDatabase.getInstance().getReference("userprofile").child(userid)

        val hashMap = HashMap<String, String>()
        hashMap["id"] = userid
        hashMap["email"] = mtext_input_username.text!!.toString()
        hashMap["name"] = "Name"
        hashMap["gender"] = "Gender"
        hashMap["dob"] = "Date of Birth"
        hashMap["ic"] = "Identification Number"
        hashMap["phonenumber"] = "Phone Number"
        hashMap["photoUrl"] = "default"
        reference.setValue(hashMap).addOnCompleteListener { task ->
            // Successfully Sign Up
            if (task.isSuccessful) {
                Toast.makeText(this@RegisterActivity, resources.getString(R.string.account_create_successful),
                    Toast.LENGTH_SHORT).show()
                Toast.makeText(this@RegisterActivity, resources.getString(R.string.login_successful),
                    Toast.LENGTH_SHORT).show()
                val intent = Intent(this@RegisterActivity, MainActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
                finish()
            }
        }
                } else if (task.exception is FirebaseAuthUserCollisionException) {
                    Toast.makeText(this@RegisterActivity,
                        resources.getString(R.string.email_already_registered), Toast.LENGTH_SHORT).show()
                } else {
                    // If sign in fails, display a message to the user.
                    Toast.makeText(this@RegisterActivity, resources.getString(R.string.account_cannot_create),
                        Toast.LENGTH_SHORT).show()
                }
            }
        }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.button_sign_up -> {
                createAccount(
                    mtext_input_username.text!!.toString(),
                    mtext_input_confirm_password.text!!.toString()
                )
            }
            R.id.button_sign_in -> {
                val intent = Intent(this, LoginActivity::class.java)
                startActivity(intent)
            }
            R.id.button_cancel -> {
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
            }
        }
    }
}
