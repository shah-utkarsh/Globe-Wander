// Author : Shah Utkarsh
package com.example.globewander
import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import android.widget.SearchView;
import android.net.Uri;
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import android.location.Address
import android.location.Geocoder
import android.util.Log
import android.widget.Toast
import java.io.IOException

// This activity is responsible for displaying Google Maps and showing specific locations on it.
internal class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap

    companion object {
        private const val LOCATION_PERMISSION_REQUEST = 1
    }

    // Sets up the activity, including the map fragment and location permissions.
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        // Check for location permissions and request if not granted.
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                LOCATION_PERMISSION_REQUEST
            )
        }

    }

    // Handle the result of the permission request.
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            LOCATION_PERMISSION_REQUEST -> {
                if ((grantResults.isNotEmpty() &&
                            grantResults[0] == PackageManager.PERMISSION_GRANTED)
                ) {
                    enableMyLocation()
                }
            }
        }
    }

    // Enables user location on the map if permissions are granted.
    private fun enableMyLocation() {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
            && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }
        mMap.isMyLocationEnabled = true
    }

    // Prepares the map once it's ready, sets map UI settings, and displays the specified city location.
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        enableMyLocation()

        val uiSettings = mMap.uiSettings
        uiSettings.isZoomControlsEnabled = true

        // Get the city name from the intent
        val cityName = intent.getStringExtra("cityName")
        if (cityName != null) {
            showCityOnMap(cityName)
        } else {
            Toast.makeText(this, "City name not provided", Toast.LENGTH_SHORT).show()
        }
    }

    // Retrieves and shows the location of the specified city on the map.
    private fun showCityOnMap(cityName: String) {
        val geocoder = Geocoder(this)
        try {
            val addressList = geocoder.getFromLocationName(cityName, 1)
            if (addressList != null && addressList.isNotEmpty()) {
                val address = addressList[0]
                val cityLocation = LatLng(address.latitude, address.longitude)
                mMap.addMarker(MarkerOptions().position(cityLocation).title(cityName))
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(cityLocation, 10f))
            } else {
                Toast.makeText(this, "Location not found for $cityName", Toast.LENGTH_SHORT).show()
            }
        } catch (e: IOException) {
            e.printStackTrace()
            Toast.makeText(this, "Error finding location for $cityName", Toast.LENGTH_SHORT).show()
        }
    }


}
