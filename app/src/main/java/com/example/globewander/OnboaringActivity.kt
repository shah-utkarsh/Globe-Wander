package com.example.globewander

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.FirebaseDatabase
import java.util.*

/**
 * Activity for handling the onboarding process, collecting user preferences.
 */
class OnboardingActivity : AppCompatActivity() {

    private lateinit var editTextStartDate: EditText
    private lateinit var editTextEndDate: EditText
    private lateinit var submitButton: Button
    private lateinit var userId: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_onboarding)

        // Retrieve the user ID passed from the previous activity
        userId = intent.getStringExtra("userId") ?: return

        // Initialize UI components
        editTextStartDate = findViewById(R.id.editTextStartDate)
        editTextEndDate = findViewById(R.id.editTextEndDate)
        submitButton = findViewById(R.id.buttonSubmit)

        // Set onClickListener to show DatePickerDialog for start and end date fields
        editTextStartDate.setOnClickListener { showDatePickerDialog(editTextStartDate) }
        editTextEndDate.setOnClickListener { showDatePickerDialog(editTextEndDate) }

        // Set onClickListener to save user data
        submitButton.setOnClickListener { saveUserData() }
    }

    /**
     * Shows a DatePickerDialog and sets the selected date on the EditText.
     */
    private fun showDatePickerDialog(editText: EditText) {
        val calendar = Calendar.getInstance()
        val datePickerDialog = DatePickerDialog(this,
            { _, year, month, dayOfMonth ->
                val formattedDate = String.format(Locale.getDefault(), "%04d-%02d-%02d", year, month + 1, dayOfMonth)
                editText.setText(formattedDate)
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )

        // Prevent past date selection
        datePickerDialog.datePicker.minDate = System.currentTimeMillis() - 1000

        datePickerDialog.show()
    }

    /**
     * Saves user preferences to Firebase Database.
     */
    private fun saveUserData() {
        val selectedCountries = getSelectedCountries()
        val interests = getSelectedInterests()
        val startDate = editTextStartDate.text.toString()
        val endDate = editTextEndDate.text.toString()

        val userData = mapOf(
            "selectedCountries" to selectedCountries,
            "interests" to interests,
            "startDate" to startDate,
            "endDate" to endDate
        )

        // Save data to Firebase Database
        FirebaseDatabase.getInstance().getReference("UserPreferences")
            .child(userId)
            .setValue(userData).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(this, "Data saved successfully", Toast.LENGTH_SHORT).show()
                    val mainActivityIntent = Intent(this, MainActivity::class.java)
                    startActivity(mainActivityIntent)
                    finish()
                } else {
                    Toast.makeText(this, "Failed to save user preferences: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                }
            }
    }

    /**
     * Retrieves selected countries from checkboxes.
     */
    private fun getSelectedCountries(): List<String> {
        val selectedCountries = mutableListOf<String>()
        // Add selected countries to the list
        if (findViewById<CheckBox>(R.id.checkbox_paris).isChecked) selectedCountries.add("Paris")
        if (findViewById<CheckBox>(R.id.checkbox_tokyo).isChecked) selectedCountries.add("Tokyo")
        if (findViewById<CheckBox>(R.id.checkbox_singapore).isChecked) selectedCountries.add("Singapore")
        if (findViewById<CheckBox>(R.id.checkbox_italy).isChecked) selectedCountries.add("Italy")
        if (findViewById<CheckBox>(R.id.checkbox_dubai).isChecked) selectedCountries.add("Dubai")
        if (findViewById<CheckBox>(R.id.checkbox_london).isChecked) selectedCountries.add("London")
        return selectedCountries
    }

    /**
     * Retrieves selected interests from checkboxes.
     */
    private fun getSelectedInterests(): List<String> {
        val selectedInterests = mutableListOf<String>()
        // Add selected interests to the list
        if (findViewById<CheckBox>(R.id.checkbox_mountains).isChecked) selectedInterests.add("Mountains")
        if (findViewById<CheckBox>(R.id.checkbox_beach).isChecked) selectedInterests.add("Beach")
        if (findViewById<CheckBox>(R.id.checkbox_sunset).isChecked) selectedInterests.add("Sunset Places")
        if (findViewById<CheckBox>(R.id.checkbox_snow).isChecked) selectedInterests.add("Snow")
        return selectedInterests
    }
}
