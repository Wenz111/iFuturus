package com.example.ifuturus

import android.content.DialogInterface
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.util.Patterns
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.example.ifuturus.preferences.loginpreferences
import com.example.ifuturus.preferences.logintimelimit
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_login.*
import java.text.SimpleDateFormat
import java.util.*

class LoginActivity : AppCompatActivity(), View.OnClickListener {

    // [START declare_auth]
    private lateinit var auth: FirebaseAuth
    // [END declare_auth]

    private lateinit var googleSignInClient: GoogleSignInClient

    // Login with Username and Password
    // Firebase instance variables
    private lateinit var mFirebaseAuth: FirebaseAuth

    // Declare Edit Text Input
    private lateinit var mUsername_login_input: TextInputEditText
    private lateinit var mPassword_login_input: TextInputEditText

    // Declare Text Input Layout to Display Error
    private lateinit var mtextInputLayout_Username: TextInputLayout
    private lateinit var mtextInputLayout2_Password: TextInputLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        btnGoogleSignIn.setOnClickListener(this)

        // [START config_signin]
        // Configure Google Sign In
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        // [END config_signin]

        googleSignInClient = GoogleSignIn.getClient(this, gso)

        // [START initialize_auth]
        // Initialize Firebase Auth
        auth = FirebaseAuth.getInstance()
        // [END initialize_auth]

        // Login using username and password
        // Assign Edit Text Input Fields
        mUsername_login_input = findViewById(R.id.username_login_input)
        mPassword_login_input = findViewById(R.id.password_login_input)

        // Declare Buttons Click Listener
        findViewById<View>(R.id.button_login).setOnClickListener(this)
        findViewById<View>(R.id.button_sign_up).setOnClickListener(this)
        findViewById<View>(R.id.button_cancel).setOnClickListener(this)
        button_email_verification.setOnClickListener(this)
        checkBox_login_terms_of_service.setOnClickListener(this)

        // Assign Text Input Layout to XML Layout
        mtextInputLayout_Username = findViewById(R.id.textInputLayout_Username)
        mtextInputLayout2_Password = findViewById(R.id.textInputLayout2_Password)

        mFirebaseAuth = FirebaseAuth.getInstance()

        // If the Terms and Conditions Checkbox is not Checked
        acceptTermsAndConditions()
    }

    private fun loginFunction(){
        // Check username + password Input
        val email = mUsername_login_input.text.toString()
        val password = mPassword_login_input.text.toString()

        // Validation on username + password Input
        if (!validateForm()) {
            return
        }

        // Perform Login Intent
/*                mFirebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success
                    Toast.makeText(this@LoginActivity, resources.getString(R.string.login_successful),
                        Toast.LENGTH_SHORT).show()
                    startActivity(Intent(applicationContext, MainActivity::class.java))
                    finish()
                } else {
                    // If sign in fails, display error message to the user
                    Toast.makeText(this@LoginActivity, resources.getString(R.string.invalid_login_credentials),
                        Toast.LENGTH_SHORT).show()
                }
        }*/

        // Perform Login Intent with Email Validation
        mFirebaseAuth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(OnCompleteListener {
                if(it.isSuccessful) {
                    if (mFirebaseAuth.currentUser!!.isEmailVerified) {
                        val loginPreference = loginpreferences(this)
                        val loginCount = loginPreference.getLoginCount()

                        if (loginCount > 0 || loginCount == 0) {
                            // Sign in success
                            Toast.makeText(this@LoginActivity, resources.getString(R.string.login_successful),
                                Toast.LENGTH_SHORT).show()
                            val loginTimeLimitPreference = logintimelimit(this)
                            loginTimeLimitPreference.setTimeLimit("")
                            loginPreference.setLoginCount(5)
                            startActivity(Intent(applicationContext, MainActivity::class.java))
                            finish()
                        } else {
                            val loginTimeLimitPreference = logintimelimit(this)
                            val accessTimeLimitRemaining = loginTimeLimitPreference.getTimeLimit()
                            val sdf = SimpleDateFormat("dd/M/yyyy HH:mm:ss a")
                            val currentTime = sdf.format(Date())

                            Toast.makeText(this@LoginActivity, "Please wait until " + accessTimeLimitRemaining + " before you are able to retry and login again...",
                                Toast.LENGTH_LONG).show()
                            if (accessTimeLimitRemaining > currentTime) {
                                loginTimeLimitPreference.setTimeLimit("")
                                loginPreference.setLoginCount(5)
                            }
                        }
                    } else {
                        Toast.makeText(this@LoginActivity, "Please Verify your email address before you are allowed to use this application", Toast.LENGTH_LONG).show()
                    }
                } else {
                    // If sign in fails, display error message to the user
                    // If user login more than equal to 5 times then prevent user from logging in
                    // to prevent unauthorised access
                    // Create Login Preference Object
                    val loginPreference = loginpreferences(this)
                    var loginCount = loginPreference.getLoginCount()
                    if (loginCount >= 1) {
                        Toast.makeText(this@LoginActivity, resources.getString(R.string.invalid_login_credentials) + "\nYou have " + loginCount.toString() + " attempt left...",
                            Toast.LENGTH_LONG).show()
                        loginCount--
                        loginPreference.setLoginCount(loginCount)
                    } else {
                        // Lock the user for 30 minutes before
                        // the user can try login again
                        // Show Toast and display the countdown timer
                        // Get Current Time
                        val sdf = SimpleDateFormat("dd/M/yyyy HH:mm:ss a")
                        val currentTime = sdf.format(Date())

                        val accessTime = Calendar.getInstance()
                        accessTime.add(Calendar.MINUTE, 30)
                        val allowAccessTime = sdf.format(accessTime.time)

                        val loginTimeLimitPreference = logintimelimit(this)
                        val getTimeLimit = loginTimeLimitPreference.getTimeLimit()

                        Log.d("Get Time Limit", getTimeLimit)

                        if (getTimeLimit.isNullOrBlank()) {
                            Log.d("Time Limit", allowAccessTime)
                            loginTimeLimitPreference.setTimeLimit(allowAccessTime)
                        }

                        val accessTimeLimitRemaining = loginTimeLimitPreference.getTimeLimit()

                        if (accessTimeLimitRemaining > currentTime) {
                            Toast.makeText(this@LoginActivity, "Please wait until " + accessTimeLimitRemaining + " before you are able to retry and login again...",
                                Toast.LENGTH_LONG).show()
                        } else {
                            loginTimeLimitPreference.setTimeLimit("")
                            loginPreference.setLoginCount(5)
                        }
                    }
                }
            })
    }

    // Do validation on Login Credentials
    private fun validateForm(): Boolean {
        var valid = true
        val pattern = Patterns.EMAIL_ADDRESS

        val email = mUsername_login_input.text!!.toString()
        if (TextUtils.isEmpty(email)) {
            mtextInputLayout_Username.error = resources.getString(R.string.required)
            valid = false
        } else if (!pattern.matcher(email).matches()) {
            mtextInputLayout_Username.error = resources.getString(R.string.invalid_email_format)
            valid = false
        } else {
            mtextInputLayout_Username.error = null
        }

        val password = mPassword_login_input.text!!.toString()
        if (TextUtils.isEmpty(password)) {
            mtextInputLayout2_Password.error = resources.getString(R.string.required)
            valid = false
        } else if (mPassword_login_input.length() < 6) {
            mtextInputLayout2_Password.error =
                resources.getString(R.string.password_format_error_message)
            valid = false
        } else {
            mtextInputLayout2_Password.error = null
        }

        return valid
    }

    // [START onactivityresult]
    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                // Google Sign In was successful, authenticate with Firebase
                val account = task.getResult(ApiException::class.java)
                firebaseAuthWithGoogle(account!!)
            } catch (e: ApiException) {
                // Google Sign In failed, update UI appropriately
                Log.w(TAG, "Google sign in failed", e)
                // Redirect to Sign In screen
            }
        }
    }
    // [END onactivityresult]

    // [START auth_with_google]
    private fun firebaseAuthWithGoogle(acct: GoogleSignInAccount) {
        Log.d(TAG, "firebaseAuthWithGoogle:" + acct.id!!)

        val credential = GoogleAuthProvider.getCredential(acct.idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(TAG, "signInWithCredential:success")
                    val user = auth.currentUser
                    val userid = user!!.uid

                    // Create a Firebase Database Record
                    val ref: DatabaseReference
                    ref = FirebaseDatabase.getInstance().getReference()
                    ref.addListenerForSingleValueEvent(object : ValueEventListener {
                        override fun onCancelled(p0: DatabaseError) {
                            // Handle Cancel Event
                        }

                        override fun onDataChange(datasnapshot: DataSnapshot) {
                            if (datasnapshot.hasChild("userprofile/" + user.uid)) {
                                Toast.makeText(this@LoginActivity, resources.getString(R.string.login_successful),
                                    Toast.LENGTH_SHORT).show()
                                return
                            } else {
                                val reference: DatabaseReference
                                reference = FirebaseDatabase.getInstance().getReference("userprofile").child(userid)

                                val hashMap = HashMap<String, String?>()
                                hashMap["id"] = userid
                                hashMap["email"] = auth.currentUser?.email
                                hashMap["name"] = "Name"
                                hashMap["gender"] = "Gender"
                                hashMap["dob"] = "Date of Birth"
                                hashMap["ic"] = "IC"
                                hashMap["phonenumber"] = "Phone Number"
                                hashMap["photoUrl"] = "default"

                                reference.setValue(hashMap).addOnCompleteListener { task ->
                                    // Successfully Sign Up
                                    if (task.isSuccessful) {
                                        Toast.makeText(this@LoginActivity, resources.getString(R.string.account_create_successful),
                                            Toast.LENGTH_SHORT).show()
                                    }
                                }
                            }
                        }
                    })

                    // Redirect to Main Activity
                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                }
            }
    }
    // [END auth_with_google]

    // [START signin]
    private fun signIn() {
        val signInIntent = googleSignInClient.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }
    // [END signin]

    private fun signOut() {
        // Firebase sign out
        FirebaseAuth.getInstance().signOut()
    }

    private fun sendEmailVerification() {
        // Check username + password Input
        val email = mUsername_login_input.text.toString()
        val password = mPassword_login_input.text.toString()

        // Validation on username + password Input
        if (!validateForm()) {
            return
        }

        mFirebaseAuth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(OnCompleteListener {
                if(it.isSuccessful) {
                    if (!(mFirebaseAuth.currentUser!!.isEmailVerified)) {
                        mFirebaseAuth.currentUser!!.sendEmailVerification()
                        Toast.makeText(this@LoginActivity, "Email Verification Send, Please Verify your Email", Toast.LENGTH_LONG).show()
                    }
                }
            })
    }

    // Accept the Terms and Conditions of iFuturus
    private fun acceptTermsAndConditions() {
        if (checkBox_login_terms_of_service.isChecked) {
            button_login.isEnabled = true
            btnGoogleSignIn.isEnabled = true
        } else {
            button_login.isEnabled = false
            btnGoogleSignIn.isEnabled = false
            Toast.makeText(this, "Please read and agree to the Terms \n and Conditions and Privacy Policy \n before you are allow to login.", Toast.LENGTH_LONG).show()
        }
    }

    override fun onClick(v: View) {
        val i = v.id
        when (i) {
            R.id.btnGoogleSignIn -> {
                signIn()
            }
            R.id.button_email_verification -> {
                // Send Email Verification
                sendEmailVerification()
            }
            R.id.button_login -> {
                loginFunction()
            }
            R.id.button_sign_up -> {
                val intent = Intent(this, RegisterActivity::class.java)
                startActivity(intent)
            }
            R.id.checkBox_login_terms_of_service -> {
                acceptTermsAndConditions()
            }
            R.id.button_cancel -> {
                // Perform Cancel Intent
                // means Exit the App
                // Confirm to quit App
                val builder = AlertDialog.Builder(this@LoginActivity)
                builder.setTitle(
                    resources.getString(R.string.quit_dialog) + " " + this.resources.getString(
                        R.string.app_name
                    )
                )
                builder.setMessage(
                    resources.getString(R.string.confirm_quit_dialog) + " " + this.resources.getString(
                        R.string.app_name
                    ) + "?"
                )
                    .setCancelable(false)
                    .setPositiveButton(R.string.yes,
                        DialogInterface.OnClickListener { dialogInterface, i ->
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
        }
    }

    companion object {
        private const val TAG = "GoogleActivity"
        private const val RC_SIGN_IN = 9001
    }
}