package com.example.globewander

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

/**
 * Activity for handling new user registration.
 */
class RegisterActivity : AppCompatActivity() {

    // FirebaseAuth instance for authentication purposes
    private lateinit var auth: FirebaseAuth

    // EditText fields for registration form
    private lateinit var passwordEditText: EditText
    private lateinit var confirmPasswordEditText: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        // Initialize FirebaseAuth instance
        auth = FirebaseAuth.getInstance()

        // UI elements: EditText fields and register button
        val firstNameEditText = findViewById<EditText>(R.id.firstNameEditText)
        val lastNameEditText = findViewById<EditText>(R.id.lastNameEditText)
        val emailEditText = findViewById<EditText>(R.id.emailEditText)
        passwordEditText = findViewById<EditText>(R.id.passwordEditText)
        confirmPasswordEditText = findViewById<EditText>(R.id.confirmPasswordEditText)
        val contactEditText = findViewById<EditText>(R.id.contactEditText)
        val addressEditText = findViewById<EditText>(R.id.addressEditText)
        val countryEditText = findViewById<EditText>(R.id.countryEditText)
        val registerButton = findViewById<Button>(R.id.registerButton)

        // OnClickListener for the register button
        registerButton.setOnClickListener {
            // Validate form inputs
            if (!validateForm(firstNameEditText, lastNameEditText, emailEditText, passwordEditText, confirmPasswordEditText, contactEditText, addressEditText, countryEditText)) {
                return@setOnClickListener
            }

            // Extract email and password from EditText fields
            val email = emailEditText.text.toString()
            val password = passwordEditText.text.toString()

            // Create a new user with email and password
            auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // On successful registration, create and save user profile in Firebase Database
                    val user = auth.currentUser
                    val userProfile = UserProfile(
                        userId = user?.uid ?: "",
                        firstName = firstNameEditText.text.toString(),
                        lastName = lastNameEditText.text.toString(),
                        email = email,
                        contact = contactEditText.text.toString(),
                        address = addressEditText.text.toString(),
                        country = countryEditText.text.toString()
                    )

                    FirebaseDatabase.getInstance().getReference("Users")
                        .child(user!!.uid)
                        .setValue(userProfile)
                        .addOnCompleteListener { databaseTask ->
                            if (databaseTask.isSuccessful) {
                                // On successful profile creation, redirect to OnboardingActivity
                                Toast.makeText(this, "Registration successful", Toast.LENGTH_SHORT).show()
                                val intent = Intent(this, OnboardingActivity::class.java).apply {
                                    putExtra("userId", user.uid)
                                }
                                startActivity(intent)
                                finish()
                            } else {
                                // Handle failure in saving user profile
                                Toast.makeText(this, "Failed to save user profile: ${databaseTask.exception?.message}", Toast.LENGTH_SHORT).show()
                            }
                        }
                } else {
                    // Handle registration failure
                    Toast.makeText(this, "Registration failed: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    /**
     * Validates the registration form.
     *
     * @param fields Variable number of EditText fields to validate.
     * @return Boolean indicating whether the form is valid or not.
     */
    private fun validateForm(vararg fields: EditText): Boolean {
        var isValid = true

        // Check if any field is empty
        fields.forEach { field ->
            if (field.text.toString().trim().isEmpty()) {
                field.error = "This field is required"
                isValid = false
            }
        }

        // Check if passwords match
        if (passwordEditText.text.toString() != confirmPasswordEditText.text.toString()) {
            confirmPasswordEditText.error = "Passwords do not match"
            isValid = false
        }

        return isValid
    }
}

/**
 * Data class representing a user profile.
 */
data class UserProfile(
    val userId: String = "",
    val firstName: String = "",
    val lastName: String = "",
    val email: String = "",
    val contact: String = "",
    val address: String = "",
    val country: String = ""
)
