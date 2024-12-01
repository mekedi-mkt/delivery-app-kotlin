package com.example.delivery_app_kotlin.main.model.repo

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import androidx.core.content.ContextCompat
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationResult
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.GeoPoint

class LocationRepository(context: Context) {

    private val fusedLocationProviderClient: FusedLocationProviderClient =
        LocationServices.getFusedLocationProviderClient(context)

    // LiveData to observe the current user's location
    private val _userLocation = MutableLiveData<Location>()
    val userLocation: LiveData<Location> get() = _userLocation

    // Firestore reference
    private val db = FirebaseFirestore.getInstance()
    private val locationCollection = db.collection("locations")

    // Location request settings
    private val locationRequest = LocationRequest.Builder(
        LocationRequest.PRIORITY_HIGH_ACCURACY, 1000
    ).build()

    fun fetchLocation(context: Context, callback: (Location?) -> Unit) {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)
            == PackageManager.PERMISSION_GRANTED
        ) {
            startLocationUpdates()
            fusedLocationProviderClient.lastLocation
                .addOnSuccessListener { location ->
                    callback(location)
                }
                .addOnFailureListener {
                    callback(null)
                }
        } else {
            callback(null)
        }
    }

    private val locationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            super.onLocationResult(locationResult)
            val location = locationResult.lastLocation
            if (location != null) {
                _userLocation.postValue(location)
                // Update Firestore with the new location
                updateLocationInFirestore(location)
            }
        }
    }

    // Start location updates
    @SuppressLint("MissingPermission")
    fun startLocationUpdates() {
        fusedLocationProviderClient.requestLocationUpdates(
            locationRequest,
            locationCallback,
            null
        )
    }

    // Stop location updates
    fun stopLocationUpdates() {
        fusedLocationProviderClient.removeLocationUpdates(locationCallback)
    }

    // Store user's location in Firestore
    private fun updateLocationInFirestore(location: Location) {
        val geoPoint = GeoPoint(location.latitude, location.longitude)
        val locationData = mapOf(
            "location" to geoPoint,
            "timestamp" to System.currentTimeMillis()
        )
        val userId = "userId"  // Replace this with the actual user ID or device ID
        locationCollection.document(userId).set(locationData)
    }

    // Fetch location of another user from Firestore
    fun fetchOtherUserLocation(userId: String): LiveData<Location?> {
        val liveLocation = MutableLiveData<Location>()
        locationCollection.document(userId).get()
            .addOnSuccessListener { document ->
                val geoPoint = document.getGeoPoint("location")
                geoPoint?.let {
                    val location = Location("Firestore")
                    location.latitude = it.latitude
                    location.longitude = it.longitude
                    liveLocation.postValue(location)
                }
            }
        return liveLocation
    }
}
