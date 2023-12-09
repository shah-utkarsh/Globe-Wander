package com.example.globewander

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.example.globewander.models.Attraction
import com.example.globewander.models.Destination
import com.example.globewander.models.Food
import android.widget.Button
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class MainActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private var userId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        auth = FirebaseAuth.getInstance()
        userId = auth.currentUser?.uid

        uploadDestinations()

        if (auth.currentUser == null) {
            // If not logged in, redirect to LoginActivity
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
        else{
            setupButton()
            mapsButton()


            // View All Destinations button
            val btnViewDestinations: View = findViewById(R.id.btnViewDestinations)
            btnViewDestinations.setOnClickListener {
                // Redirect to DestinationActivity
                val intent = Intent(this, DestinationActivity::class.java)
                startActivity(intent)
            }

            // Logout button
            val buttonLogout = findViewById<Button>(R.id.buttonLogout)
            buttonLogout.setOnClickListener {
                // Log out the user
                auth.signOut()
                // Redirect to LoginActivity
                startActivity(Intent(this, LoginActivity::class.java))
                finish()
            }

            val viewFAQsButton = findViewById<Button>(R.id.viewFAQsButton)
            viewFAQsButton.setOnClickListener {
                val intent = Intent(this, FaqActivity::class.java)
                startActivity(intent)
            }
            val viewDocumentsButton = findViewById<Button>(R.id.buttonUpload)
            viewDocumentsButton.setOnClickListener {
                val intent1 = Intent(this, DocumentMgmtActivity::class.java)
                intent1.putExtra("userId", userId)
                startActivity(intent1)
            }


            val recommendationButton: Button = findViewById(R.id.buttonTab3)
            recommendationButton.setOnClickListener {
                // Intent to start RecommendationActivity
                val intent = Intent(this, RecommendationActivity::class.java)
                startActivity(intent)
            }


        }
    }


    private fun setupButton() {
        // Find the button by its ID
        val allBlogsButton = findViewById<Button>(R.id.allBlogsButton)

        // Set an OnClickListener
        allBlogsButton.setOnClickListener {
            // Create an Intent to start AllBlogsActivity
            val intent = Intent(this, AllBlogsActivity::class.java)
            startActivity(intent)
        }

        // Find the button by its ID
        val allItinerariesButton = findViewById<Button>(R.id.allItinerariesButton)

        // Set an OnClickListener
        allItinerariesButton.setOnClickListener {
            // Create an Intent to start AllBlogsActivity
            val intent = Intent(this, AllItinerariesActivity::class.java)
            startActivity(intent)
        }

        // Find the button by its ID
        val allRatingsButton = findViewById<Button>(R.id.allRatingsButton)

        // Set an OnClickListener
        allRatingsButton.setOnClickListener {
            // Create an Intent to start AllBlogsActivity
            val intent = Intent(this, AllRatingsActivity::class.java)
            startActivity(intent)
        }
    }

    private fun mapsButton(){

        val viewMapButton = findViewById<Button>(R.id.viewMapButton)

        viewMapButton.setOnClickListener{

            val intent = Intent(this, MapsActivity::class.java)
            startActivity(intent)
        }
    }



    private val database: DatabaseReference = FirebaseDatabase.getInstance().reference.child("Destinations")

    private fun uploadDestinations() {
        val destinationUploader = DestinationUploader(database)

        val destinationDubai = createSampleDestination("Dubai", "Dubai is a city and emirate known for luxury shopping, ultramodern architecture, and a lively nightlife scene.")

        // Sample destination data for London
        val destinationLondon = createSampleDestination("London", "London, the capital of England and the United Kingdom, is a 21st-century city with history stretching back to Roman times.")

        // Sample destination data for Italy
        val destinationItaly = createSampleDestination("Italy", "Italy is a European country with a long Mediterranean coastline, which has left a powerful mark on Western culture and cuisine.")

        destinationUploader.uploadDestination(destinationDubai)
        destinationUploader.uploadDestination(destinationLondon)
        destinationUploader.uploadDestination(destinationItaly)
    }

    private fun createSampleDestination(city: String, advice: String): Destination {
        return Destination(
            city = city,
            advice = advice,
            attractions = when (city) {
                "Tokyo" -> listOf(
                    Attraction("Marina Bay Sands", "An iconic hotel and casino complex with a stunning rooftop pool."),
                    Attraction("Gardens by the Bay", "A futuristic park featuring the striking Supertree Grove and Cloud Forest."),
                    Attraction("Sentosa Island", "A resort island with attractions like Universal Studios and S.E.A. Aquarium.")
                )
                "Singapore" -> listOf(
                    Attraction("Marina Bay Sands", "An iconic hotel and casino complex with a stunning rooftop pool."),
                    Attraction("Gardens by the Bay", "A futuristic park featuring the striking Supertree Grove and Cloud Forest."),
                    Attraction("Sentosa Island", "A resort island with attractions like Universal Studios and S.E.A. Aquarium.")
                )
                "Dubai" -> listOf(
                    Attraction("Burj Khalifa", "The tallest building in the world, offering breathtaking views of the city."),
                    Attraction("Dubai Mall", "A massive shopping and entertainment complex with an indoor ice rink and aquarium."),
                    Attraction("Palm Jumeirah", "A man-made archipelago known for its luxury resorts and palm tree-shaped design.")
                )
                "London" -> listOf(
                    Attraction("British Museum", "A vast collection of world art and artifacts and free to all visitors."),
                    Attraction("Tower of London", "A historic castle located on the banks of the River Thames."),
                    Attraction("Buckingham Palace", "The official residence of the monarch of the United Kingdom.")
                )
                "Italy" -> listOf(
                    Attraction("Colosseum", "An ancient amphitheater known for its gladiator contests."),
                    Attraction("Venice", "A unique city built on water, known for its canals and historic architecture."),
                    Attraction("Florence", "The capital city of the Tuscany region, known for its art and architecture.")
                )
                else -> emptyList()
            },
            language = when (city) {
                "Tokyo" -> "Japanese"
                "Singapore" -> "English, Malay, Mandarin, Tamil"
                "Dubai" -> "Arabic"
                "London" -> "English"
                "Italy" -> "Italian"
                else -> ""
            },
            food = when (city) {
                "Tokyo" -> listOf(
                    Food("Sushi", "Traditional Japanese dish of prepared vinegared rice, usually with some sugar and salt, accompanying a variety of ingredients such as seafood, vegetables, and occasionally tropical fruits."),
                    Food("Ramen", "Japanese dish with Chinese origins. It consists of Chinese wheat noodles served in a meat or (occasionally) fish-based broth, often flavored with soy sauce or miso, and topped with ingredients such as sliced pork, dried seaweed, menma, and green onions."),
                    Food("Tempura", "Japanese dish usually consisting of seafood or vegetables that have been battered and deep-fried.")
                )
                "Singapore" -> listOf(
                    Food("Hainanese Chicken Rice", "Poached chicken served with fragrant rice and chili sauce."),
                    Food("Chilli Crab", "Crab cooked in a tangy, mildly spicy tomato and chili-based sauce."),
                    Food("Satay", "Grilled skewers of marinated meat served with peanut sauce.")
                )
                "Dubai" -> listOf(
                    Food("Shawarma", "A popular Middle Eastern street food made with thinly sliced meat."),
                    Food("Hummus", "A creamy dip made from chickpeas, tahini, lemon, and garlic."),
                    Food("Kebabs", "Grilled or roasted meat typically served with rice or flatbread.")
                )
                "London" -> listOf(
                    Food("Fish and Chips", "Deep-fried fish in batter with chunky chips."),
                    Food("Roast Beef", "A classic British Sunday roast with roast beef and Yorkshire pudding."),
                    Food("Chicken Tikka Masala", "A popular Indian-inspired dish with chunks of roasted chicken in a creamy tomato sauce.")
                )
                "Italy" -> listOf(
                    Food("Pizza Margherita", "A classic pizza with tomato, mozzarella, and fresh basil."),
                    Food("Pasta Carbonara", "A Roman dish made with eggs, cheese, pancetta, and black pepper."),
                    Food("Gelato", "Italian ice cream, similar to but denser and creamier than American ice cream.")
                )
                else -> emptyList()
            },
            budget = when (city) {
                "Tokyo" -> 2000.0
                "Singapore" -> 2500.0
                "Dubai" -> 3000.0
                "London" -> 2800.0
                "Italy" -> 2600.0
                else -> 0.0
            },
            population = when (city) {
                "Tokyo" -> 14000000
                "Singapore" -> 5700000
                "Dubai" -> 3000000
                "London" -> 8900000
                "Italy" -> 60000000
                else -> 0
            }
        )
    }
}
