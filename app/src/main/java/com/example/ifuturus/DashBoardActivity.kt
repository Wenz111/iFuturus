package com.example.ifuturus

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.example.ifuturus.model.lodgereportmodel
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.activity_dash_board.*

class DashBoardActivity : AppCompatActivity() {

    private var categoryPotholes = 0
    private var categoryTrafficLight = 0
    private var categoryBuilding = 0
    private var categoryGarbage = 0
    private var categoryPlants = 0
    private var categoryFurniture = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dash_board)

        countCategory()
    }

    private fun countCategory() {
        val ref = FirebaseDatabase.getInstance().getReference("lodgereport")

        ref.addListenerForSingleValueEvent(object: ValueEventListener {
            override fun onDataChange(p0: DataSnapshot) {
                // Get DataSnapshot Value

                p0.children.forEach {
                    Log.d("DashBoard Activity","Inside DataSnapshot: $it")

                    // Get Report Details
                    val reportDetails = it.getValue(lodgereportmodel::class.java)

                    // If Report Details is not empty
                    if (reportDetails != null) {
                        // Count the Category of Potholes
                        if (reportDetails.complaintCategory.contains("Sand") ||
                            reportDetails.complaintCategory.contains("Soil") ||
                            reportDetails.complaintCategory.contains("Road") ||
                            reportDetails.complaintCategory.contains("Vehicle") ||
                            reportDetails.complaintCategory.contains("Wheel") ||
                            reportDetails.complaintCategory.contains("Car") ||
                            reportDetails.complaintCategory.contains("Van") ||
                            reportDetails.complaintCategory.contains("Asphalt")) {
                            categoryPotholes += 1
                        } // End If

                        // Count the Category of Traffic Light or Light
                        if (reportDetails.complaintCategory.contains("Vehicle") ||
                            reportDetails.complaintCategory.contains("Infrastructure") ||
                            reportDetails.complaintCategory.contains("Road") ||
                            reportDetails.complaintCategory.contains("Bumper") ||
                            reportDetails.complaintCategory.contains("Windshield") ||
                            reportDetails.complaintCategory.contains("Vehicle") ||
                            reportDetails.complaintCategory.contains("Wheel") ||
                            reportDetails.complaintCategory.contains("Car") ||
                            reportDetails.complaintCategory.contains("Sky") ||
                            reportDetails.complaintCategory.contains("Metal") ||
                            reportDetails.complaintCategory.contains("Asphalt")) {
                            categoryTrafficLight += 1
                        } // End If

                        // Count the Category of Parking or Building
                        if (reportDetails.complaintCategory.contains("Building") ||
                            reportDetails.complaintCategory.contains("Roof") ||
                            reportDetails.complaintCategory.contains("Vehicle") ||
                            reportDetails.complaintCategory.contains("Car") ||
                            reportDetails.complaintCategory.contains("Bumper") ||
                            reportDetails.complaintCategory.contains("Wheel") ||
                            reportDetails.complaintCategory.contains("Tire") ||
                            reportDetails.complaintCategory.contains("Wheel") ||
                            reportDetails.complaintCategory.contains("Sky") ||
                            reportDetails.complaintCategory.contains("Plant") ||
                            reportDetails.complaintCategory.contains("Sky") ||
                            reportDetails.complaintCategory.contains("Factory") ||
                            reportDetails.complaintCategory.contains("Skyscraper")) {
                            categoryBuilding += 1
                        } // End If

                        // Count the Category of Garbage
                        if (reportDetails.complaintCategory.contains("Tableware") ||
                            reportDetails.complaintCategory.contains("Product") ||
                            reportDetails.complaintCategory.contains("Food") ||
                            reportDetails.complaintCategory.contains("Metal")) {
                            categoryGarbage += 1
                        } // End If

                        // Count the Category of Plants/Grass
                        if (reportDetails.complaintCategory.contains("Flowerpot") ||
                            reportDetails.complaintCategory.contains("Plant") ||
                            reportDetails.complaintCategory.contains("Bird") ||
                            reportDetails.complaintCategory.contains("Insect") ||
                            reportDetails.complaintCategory.contains("Food") ||
                            reportDetails.complaintCategory.contains("Fruit") ||
                            reportDetails.complaintCategory.contains("Vegetable") ||
                            reportDetails.complaintCategory.contains("Branch") ||
                            reportDetails.complaintCategory.contains("Sky") ||
                            reportDetails.complaintCategory.contains("Trunk") ||
                            reportDetails.complaintCategory.contains("Forest") ||
                            reportDetails.complaintCategory.contains("Twig") ||
                            reportDetails.complaintCategory.contains("Jungle") ||
                            reportDetails.complaintCategory.contains("Garden") ||
                            reportDetails.complaintCategory.contains("Flower")) {
                            categoryPlants += 1
                        } // End If

                        // Count the Category of Furniture
                        if (reportDetails.complaintCategory.contains("Toy") ||
                            reportDetails.complaintCategory.contains("Space") ||
                            reportDetails.complaintCategory.contains("Bag") ||
                            reportDetails.complaintCategory.contains("Handbag") ||
                            reportDetails.complaintCategory.contains("Handbag") ||
                            reportDetails.complaintCategory.contains("Product") ||
                            reportDetails.complaintCategory.contains("Chair") ||
                            reportDetails.complaintCategory.contains("Room") ||
                            reportDetails.complaintCategory.contains("Fun") ||
                            reportDetails.complaintCategory.contains("Shoe") ||
                            reportDetails.complaintCategory.contains("Couch") ||
                            reportDetails.complaintCategory.contains("Desk") ||
                            reportDetails.complaintCategory.contains("Shelf") ||
                            reportDetails.complaintCategory.contains("Jeans") ||
                            reportDetails.complaintCategory.contains("Jacket")) {
                            categoryFurniture += 1
                        } // End If
                    }
                }
                // Assign the value to display
                textViewCategory1Count.text = "Total Report Made: $categoryPotholes"
                textViewCategory2Count.text = "Total Report Made: $categoryTrafficLight"
                textViewCategory3Count.text = "Total Report Made: $categoryBuilding"
                textViewCategory4Count.text = "Total Report Made: $categoryGarbage"
                textViewCategory5Count.text = "Total Report Made: $categoryPlants"
                textViewCategory6Count.text = "Total Report Made: $categoryFurniture"
            }

            override fun onCancelled(p0: DatabaseError) {
                // Handle Error
            }
        })
    }
}
