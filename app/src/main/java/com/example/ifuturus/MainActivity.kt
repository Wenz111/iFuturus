package com.example.ifuturus

import android.content.Intent
import android.os.Bundle
import androidx.navigation.ui.AppBarConfiguration
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.core.view.GravityCompat
import com.bumptech.glide.Glide
import com.example.ifuturus.model.userprofilemodel
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.nav_header_main.*

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    // Profile Picture in Navigation Header
    private lateinit var muserImageNavHeader: CircleImageView
    private lateinit var mnameNavHeader: TextView
    private lateinit var muserEmail: TextView

    private val ANONYMOUS = "anonymous"

    // Declare variable
    private var mUsername: String? = null

    // Firebase instance variables
    private lateinit var mFirebaseAuth: FirebaseAuth
    private var mFirebaseUser: FirebaseUser? = null

    // Google Sign In
    private lateinit var googleSignInClient: GoogleSignInClient

    // Database Reference
    private lateinit var reference: DatabaseReference

    // Database Listener
    private lateinit var mListener: ValueEventListener

    private lateinit var appBarConfiguration: AppBarConfiguration

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Navigation Header
        // Declare Navigation Header Image
        val navigationView2 = findViewById<NavigationView>(R.id.nav_view)
        val hView2 = navigationView2.getHeaderView(0)
        muserImageNavHeader = hView2.findViewById(R.id.userImageNavHeader)
        mnameNavHeader = hView2.findViewById(R.id.nameNavHeader)
        muserEmail = hView2.findViewById(R.id.userEmail)

        // Set default username is anonymous.
        mUsername = ANONYMOUS

        // Initialize Firebase Auth
        mFirebaseAuth = FirebaseAuth.getInstance()
        mFirebaseUser = mFirebaseAuth.currentUser

        if (mFirebaseUser != null) {
            // User is signed in
            mUsername = mFirebaseUser?.getEmail()

            // Database Reference - Assign variables to Navigation Header
            reference = FirebaseDatabase.getInstance().getReference("userprofile").child(mFirebaseUser!!.uid)

            mListener = reference.addValueEventListener(object : ValueEventListener {
                override fun onCancelled(p0: DatabaseError) {
                    // Handle Cancel Action
                }

                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    val user = dataSnapshot.getValue(userprofilemodel::class.java)
                    // Set Text View to the user's email
                    userEmail.setText(user?.email)
                    if (user?.name.equals("Name")) {
                        nameNavHeader.setText("Name")
                    } else {
                        nameNavHeader.setText(user?.name)
                    }
                    if (user?.photoUrl.equals("default")) {
                        userImageNavHeader.setImageResource(R.drawable.pikademo)
                    } else {
                        Glide.with(applicationContext).load(user?.photoUrl).into(userImageNavHeader)
                    }
                }
            })
            // Assign values to Navigation Header Ends Here
        } else {
            // No user is signed in
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
            return
        }

        // Set up Navigation Drawer
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout)
        val navView: NavigationView = findViewById(R.id.nav_view)
        val toggle = ActionBarDrawerToggle(
            this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close
        )
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()
        navView.setNavigationItemSelectedListener(this)

        // Get Log out button on Nav Header Drawer by wenz11
        val hView = navView.getHeaderView(0)
        val mBtnLogout = hView.findViewById<Button>(R.id.btnLogout)
        mBtnLogout.setOnClickListener {
                signOut()
                startActivity(Intent(applicationContext, MainActivity::class.java))
                finish()
        }
        // If user click on profile picture, Launch User Profile Activity
        val mUserImageClick = hView.findViewById<CircleImageView>(R.id.userImageNavHeader)
        mUserImageClick.setOnClickListener {
            startActivity(Intent(applicationContext, UserProfileActivity::class.java))
        }

/*        // Display notification if report status changes
        val firebaseDatabase = FirebaseDatabase.getInstance()
        val ref = firebaseDatabase.getReference()
        ref.child("lodgereport").addValueEventListener(object: ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
                // Handle Error
            }

            override fun onDataChange(p0: DataSnapshot) {
                val children = p0.children

                children.forEach{
                    val childrenDetails = it.getValue(lodgereportmodel::class.java)

                    if (childrenDetails != null) {
                        Log.d("Report ID", "${childrenDetails.complaintId}")
                        Log.d("Children Value", "${childrenDetails.complaintStatus}")

                        // If childrenDetails Status = complete
                        // Then display complete notification
                    }
                }
            }
        })*/
    }

    // Sign Out Function
    private fun signOut() {
        mFirebaseAuth.signOut()
        Toast.makeText(
            this, resources.getString(R.string.logout_successful),
            Toast.LENGTH_SHORT
        ).show()
    }

    override fun onNavigationItemSelected(menuItem: MenuItem): Boolean {
        when (menuItem.itemId) {
            R.id.nav_home -> {
                Toast.makeText(this, "Home", Toast.LENGTH_SHORT).show()
                val intent = Intent(this, DashBoardActivity::class.java)
                startActivity(intent)
            }
            R.id.nav_profile -> {
                Toast.makeText(this, "Profile", Toast.LENGTH_SHORT).show()
                val intent = Intent(this, UserProfileActivity::class.java)
                startActivity(intent)
            }
            R.id.nav_report -> {
                Toast.makeText(this, "Report", Toast.LENGTH_SHORT).show()
                val intent = Intent(this, LodgeReportActivity::class.java)
                startActivity(intent)
            }
            R.id.nav_edit_report -> {
                Toast.makeText(this, "Edit Report", Toast.LENGTH_SHORT).show()
                val intent = Intent(this, MySubmittedReportActivity::class.java)
                startActivity(intent)
            }
            R.id.nav_report_status -> {
                Toast.makeText(this, "Report Status", Toast.LENGTH_SHORT).show()
                val intent = Intent(this, ReportStatusActivity::class.java)
                startActivity(intent)
            }
            R.id.nav_chat -> {
                Toast.makeText(this, "Chat", Toast.LENGTH_SHORT).show()
                val intent = Intent(this, ChatReportListActivity::class.java)
                startActivity(intent)
            }
            R.id.nav_settings -> {
                Toast.makeText(this, "Settings", Toast.LENGTH_SHORT).show()
                val intent = Intent(this, SettingsActivity::class.java)
                startActivity(intent)
            }
            R.id.nav_aboutus -> {
                Toast.makeText(this, "About Us", Toast.LENGTH_SHORT).show()
                val intent = Intent(this, AboutUsActivity::class.java)
                startActivity(intent)
            }
        }
        drawer_layout.closeDrawer(GravityCompat.START)
        return true
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    override fun onBackPressed() {
        if (drawer_layout.isDrawerOpen(GravityCompat.START)) {
            drawer_layout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_settings -> {
                val intent = Intent(this, SettingsActivity::class.java)
                startActivity(intent)
            }
        }
        return super.onOptionsItemSelected(item)
    }
}
