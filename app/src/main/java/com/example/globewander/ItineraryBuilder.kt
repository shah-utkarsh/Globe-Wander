// Author : Raj Patel
package com.example.globewander

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import com.google.android.material.internal.ViewUtils.hideKeyboard

// Activity to build itinerary
class ItineraryBuilder : AppCompatActivity() {

    private lateinit var daysEditText: EditText
    private lateinit var citySpinner: Spinner
    private lateinit var placesCardLayout: LinearLayout
    private lateinit var generateItineraryButton: Button
    private lateinit var parentLayout: View
    private lateinit var itineraryTextView: TextView

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_itinerary_builder)

        daysEditText = findViewById(R.id.daysEditText)
        citySpinner = findViewById(R.id.citySpinner)
        placesCardLayout = findViewById(R.id.placesCardLayout)
        generateItineraryButton = findViewById(R.id.generateItineraryButton)
        parentLayout = findViewById(R.id.parentLayout)
        itineraryTextView = findViewById(R.id.itineraryTextView)

        // Initially disable the TextView
        itineraryTextView.isEnabled = false

        // Sample city options
        val cities = arrayOf("Select City", "Paris", "Tokyo", "Singapore", "Rome", "Dubai", "London")

        // Create an ArrayAdapter using the sample cities array and a custom layout
        val adapter = ArrayAdapter(this, R.layout.spinner_item, cities)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        // Apply the adapter to the spinner
        citySpinner.adapter = adapter

        // Sample places data
        val placesMap = mapOf(
            "Paris" to listOf("Paris City Tour \n Duration: 4 hours \n Popularity: 5/5", "Louvre Museum \n" +
                    " Duration: 4 hours \n" +
                    " Popularity: 4/5", "Eiffel Tower \n" +
                    " Duration: 4 hours \n" +
                    " Popularity: 5/5", "DisneyLand \n" +
                    " Duration: 4 hours \n" +
                    " Popularity: 4.5/5", "Arch De Triumph \n" +
                    " Duration: 4 hours \n" +
                    " Popularity: 4/5"),
            "Tokyo" to listOf("Tokyo City Tour \n" +
                    " Duration: 4 hours \n" +
                    " Popularity: 5/5", "Tokyo Tower \n" +
                    " Duration: 4 hours \n" +
                    " Popularity: 5/5", "Tokyo Skytree \n" +
                    " Duration: 4 hours \n" +
                    " Popularity: 4/5", "Ueno Park \n" +
                    " Duration: 4 hours \n" +
                    " Popularity: 4.5/5", "Imperial Palace \n" +
                    " Duration: 4 hours \n" +
                    " Popularity: 4/5"),
            "Singapore" to listOf("Singapore City Tour \n" +
                    " Duration: 4 hours \n" +
                    " Popularity: 5/5", "Gardens by the Bay \n" +
                    " Duration: 4 hours \n" +
                    " Popularity: 5/5", "Sentosa Island \n" +
                    " Duration: 4 hours \n" +
                    " Popularity: 4.5/5", "Singapore Flyer \n" +
                    " Duration: 4 hours \n" +
                    " Popularity: 5/5", "Merlion \n" +
                    " Duration: 4 hours \n" +
                    " Popularity: 4/5"),
            "Rome" to listOf("Rome City Tour \n" +
                    " Duration: 4 hours \n" +
                    " Popularity: 4/5", "Colosseum \n" +
                    " Duration: 4 hours \n" +
                    " Popularity: 5/5", "Trevi Fountain \n" +
                    " Duration: 4 hours \n" +
                    " Popularity: 4/5", "Roman Forum \n" +
                    " Duration: 4 hours \n" +
                    " Popularity: 3.5/5", "Sistine Chapel \n" +
                    " Duration: 4 hours \n" +
                    " Popularity: 4/5"),
            "Dubai" to listOf("Dubai City Tour \n" +
                    " Duration: 4 hours \n" +
                    " Popularity: 5/5", "Palm Island \n" +
                    " Duration: 4 hours \n" +
                    " Popularity: 5/5", "Desert Safari \n" +
                    " Duration: 4 hours \n" +
                    " Popularity: 4/5", "Burj Khalifa \n" +
                    " Duration: 4 hours \n" +
                    " Popularity: 5/5", "Dubai Frame \n" +
                    " Duration: 4 hours \n" +
                    " Popularity: 4.5/5"),
            "London" to listOf("London City Tour \n" +
                    " Duration: 4 hours \n" +
                    " Popularity: 5/5", "London Eye \n" +
                    " Duration: 4 hours \n" +
                    " Popularity: 4/5", "Tower Bridge \n" +
                    " Duration: 4 hours \n" +
                    " Popularity: 4.5/5", "Big Ben \n" +
                    " Duration: 4 hours \n" +
                    " Popularity: 4/5", "Buckingham Palace \n" +
                    " Duration: 4 hours \n" +
                    " Popularity: 4/5"),
        )

        // Set up a listener for the spinner item selection
        citySpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                // Clear existing cards
                placesCardLayout.removeAllViews()

                // Get selected city
                val selectedCity = cities[position]

                // Get places for the selected city
                val places = placesMap[selectedCity]

                // Dynamically add CardViews with checkboxes based on places data
                places?.let {
                    for (place in it) {
                        val cardView = layoutInflater.inflate(R.layout.card_place, null) as CardView
                        val checkBox = cardView.findViewById<CheckBox>(R.id.placeCheckBox)
                        checkBox.text = place
                        placesCardLayout.addView(cardView)
                    }
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                // Handle case where nothing is selected (optional)
            }
        }

        generateItineraryButton.setOnClickListener {
            val numberOfDays = daysEditText.text.toString().toIntOrNull()
            val selectedCity = citySpinner.selectedItem.toString()

            // Find the selected checkboxes in the CheckBoxGroup
            val selectedPlaces = mutableListOf<String>()
            for (i in 0 until placesCardLayout.childCount) {
                val cardView = placesCardLayout.getChildAt(i) as CardView
                val checkBox = cardView.findViewById<CheckBox>(R.id.placeCheckBox)
                if (checkBox.isChecked) {
                    selectedPlaces.add(checkBox.text.toString())
                }
            }

            if (numberOfDays != null && numberOfDays > 0 && selectedCity != "Select City" && selectedPlaces.isNotEmpty()) {
                // Enable the TextView
                itineraryTextView.isEnabled = true

                // Display the itinerary in a TextView
                val itineraryStringBuilder = StringBuilder()
                itineraryStringBuilder.append("Your Itinerary for $numberOfDays days in $selectedCity:\n\n")

                if (numberOfDays >= selectedPlaces.size) {
                    // If days are greater than or equal to places selected
                    for (i in 1..numberOfDays) {
                        if (i <= selectedPlaces.size) {
                            // Assign one place to each day
                            itineraryStringBuilder.append("Day-$i: Visit ${selectedPlaces[i - 1]}\n")
                        } else {
                            // For the rest of the days, show a rest day
                            itineraryStringBuilder.append("Day-$i: Rest Day\n")
                        }
                    }
                } else {
                    // If days are less than places selected
                    val remainingPlaces = mutableListOf<String>()
                    remainingPlaces.addAll(selectedPlaces)

                    for (i in 1..numberOfDays) {
                        if (remainingPlaces.isNotEmpty()) {
                            val randomIndex = (0 until remainingPlaces.size).random()
                            val place = remainingPlaces.removeAt(randomIndex)
                            itineraryStringBuilder.append("Day-$i: Visit $place\n")
                        } else {
                            // If no more places, show a rest day
                            itineraryStringBuilder.append("Day-$i: Rest Day\n")
                        }
                    }
                }

                itineraryTextView.text = itineraryStringBuilder.toString()

            } else {
                showToast("Please enter valid information.")

                // If the information is not valid, disable the TextView
                itineraryTextView.isEnabled = false
            }
        }


        parentLayout.setOnTouchListener(object : View.OnTouchListener {
            override fun onTouch(v: View?, event: MotionEvent?): Boolean {
                hideKeyboard()
                return false
            }
        })
    }

    @SuppressLint("ServiceCast")
    private fun hideKeyboard() {
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(currentFocus?.windowToken, 0)
    }

    private fun showToast(message: String) {
        // Display a toast message (you can replace this with your preferred UI feedback)
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}
