package com.example.delivery_app_kotlin.main.viewmodel

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.example.delivery_app_kotlin.main.model.repo.LocationRepository
import android.location.Location

class LocationViewModel(private val locationRepository: LocationRepository) : ViewModel() {

    // Observing user's current location
    val userLocation: LiveData<Location> = locationRepository.userLocation

    // Start live tracking (location updates) and save to Firestore
    fun startLocationUpdates() {
        locationRepository.startLocationUpdates()
    }

    // Stop live tracking
    fun stopLocationUpdates() {
        locationRepository.stopLocationUpdates()
    }

    // Get other user's location from Firestore
    fun fetchOtherUserLocation(userId: String): LiveData<Location?> {
        return locationRepository.fetchOtherUserLocation(userId)
    }
}
