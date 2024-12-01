package com.example.delivery_app_kotlin.main.view

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.View.GONE
import android.view.View.VISIBLE
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.delivery_app_kotlin.auth.viewmodel.AuthViewModel
import com.example.delivery_app_kotlin.auth.viewmodel.UserViewModel
import com.example.delivery_app_kotlin.databinding.ActivityMainBinding
import com.example.delivery_app_kotlin.delivery.view.NewDeliveryActivity
import com.example.delivery_app_kotlin.delivery.viewmodel.DeliveryViewModel
import com.example.delivery_app_kotlin.main.viewmodel.LocationViewModel
import org.osmdroid.api.IMapController
import org.osmdroid.config.Configuration
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    //    private val authViewModel: AuthViewModel by viewModels()
    private lateinit var authViewModel: AuthViewModel
    private lateinit var deliveryViewModel: DeliveryViewModel
    private lateinit var userViewModel: UserViewModel

    private val locationViewModel: LocationViewModel by viewModels()
    private var marker: Marker? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

//        locationViewModel.startLocationUpdates()
//        startDeliveryGuyLocationTracking()

        authViewModel = ViewModelProvider(this)[AuthViewModel::class.java]
        // Observe user data
        authViewModel.user.observe(this) { user ->
            if (user != null) {
                binding.userTextView.text = "${user.name} (${user.userType})"
                if (user.userType == "Client") {
                    binding.buttonAddNew.visibility = VISIBLE
                }
                deliveryViewModel.getDelivery(user.userType)
            }
        }
        authViewModel.fetchUserData()

        deliveryViewModel = ViewModelProvider(this)[DeliveryViewModel::class.java]
        userViewModel = ViewModelProvider(this)[UserViewModel::class.java]
        // Observe delivery data
        observeDeliveryData()
        deliveryViewModel.selectedDG.observe(this) {
            if (it != null) {
                binding.textOtherUser.text = it.name
            }
        }
        userViewModel.deliveryGuys.observe(this) {
            if (it != null && it.isNotEmpty()) {
                val selectedDGs =
                    it.filter { dg -> dg.userId == deliveryViewModel.delivery.value?.deliveryGuyId }
                if (selectedDGs.isNotEmpty())
                    binding.textOtherUser.text = selectedDGs[0].name
            }
        }
        userViewModel.clients.observe(this) {
            if (it != null && it.isNotEmpty()) {
                val selectedClients =
                    it.filter { dg -> dg.userId == deliveryViewModel.delivery.value?.clientId }
                if (selectedClients.isNotEmpty()) {
                    binding.textOtherUser.text = selectedClients[0].name
                    deliveryViewModel.setSelectedDG(selectedClients[0])
                }
            }
        }


        binding.buttonAddNew.setOnClickListener {
            // Navigate to RegisterActivity
            startActivity(Intent(this, NewDeliveryActivity::class.java))
        }

        binding.buttonCancel.setOnClickListener {
            deliveryViewModel.cancelDelivery()
        }
    }

    override fun onResume() {
        super.onResume()
        observeDeliveryData()
        if (authViewModel.user.value?.userType != null)
            deliveryViewModel.getDelivery(authViewModel.user.value!!.userType)
    }

    private fun observeDeliveryData() {
        deliveryViewModel.delivery.observe(this) { delivery ->

            if (delivery != null) {
                if (hasLocationPermission()) {
                    initializeLocationTracking(
                        delivery.destLocation.latitude,
                        delivery.destLocation.longitude
                    )
                } else {
                    requestLocationPermission()
                }

//                if (authViewModel.user.value!!.userType == "Delivery Guy") {
//                    startDeliveryGuyLocationTracking()
//                } else {
//                    fetchClientLocation(authViewModel.user.value!!.userId)
//                }

                binding.textPickup.text = delivery.pickup
                binding.textDest.text = delivery.destination

                if (authViewModel.user.value!!.userType == "Client") {
                    binding.buttonCancel.visibility = VISIBLE
                } else {
                    binding.buttonCancel.visibility = GONE
                }

                if (deliveryViewModel.selectedDG.value == null) {
                    if (authViewModel.user.value!!.userType == "Client") {
                        userViewModel.getDeliveryGuys()
                        val dg =
                            userViewModel.deliveryGuys.value?.filter { dg -> dg.userId == delivery.deliveryGuyId }
                        if (dg != null)
                            binding.textOtherUser.text = dg[0].name
                    } else {
                        userViewModel.getClients()
                        val client =
                            userViewModel.clients.value?.filter { client -> client.userId == delivery.clientId }
                        if (client != null)
                            binding.textOtherUser.text = client[0].name
                    }
                } else
                    binding.textOtherUser.text = deliveryViewModel.selectedDG.value?.name


                binding.noDeliveryLayout.visibility = GONE
                binding.currentDeliveryLayout.visibility = VISIBLE

                // Location Tracking
//                locationViewModel.listenForLocationUpdates()

            } else {
                binding.noDeliveryLayout.visibility = VISIBLE
                binding.currentDeliveryLayout.visibility = GONE
            }
        }
    }

//    private fun initializeLocationTracking(deliveryGuyId : int) {
//        // Initialize the map
//        binding.mapView.setTileSource(org.osmdroid.tileprovider.tilesource.TileSourceFactory.MAPNIK)
////        binding.mapView.setZoomLevel(15.0)
//        binding.mapView.setMultiTouchControls(true)
//
//        // Observe location changes from the ViewModel (Firestore)
//        locationViewModel.deliveryPersonLocation.observe(this) { location ->
//            location?.let {
//                updateMapWithLocation(it)
//            }
//        }
//    }
//
//    // Update the map with the new location
//    private fun updateMapWithLocation(location: android.location.Location) {
//        val geoPoint = GeoPoint(location.latitude, location.longitude)
//
//        // Move the map's center to the delivery person's new location
//        val controller: IMapController = binding.mapView.controller
//        controller.setCenter(geoPoint)
//
//        // If the marker is already on the map, just update its position
//        if (deliveryPersonMarker == null) {
//            // Create a new marker
//            deliveryPersonMarker = Marker(binding.mapView)
//            deliveryPersonMarker?.apply {
//                position = geoPoint
//                setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
//                title = "Delivery Person"
//            }
//            binding.mapView.overlays.add(deliveryPersonMarker)
//        } else {
//            // Update marker position
//            deliveryPersonMarker?.position = geoPoint
//        }
//    }


    private fun initializeLocationTracking(lat: Double, long: Double) {
        // Initialize OSMDroid map
        Configuration.getInstance().load(
            applicationContext,
            android.preference.PreferenceManager.getDefaultSharedPreferences(applicationContext)
        )
        binding.mapView.setTileSource(org.osmdroid.tileprovider.tilesource.TileSourceFactory.MAPNIK)
        val controller: IMapController = binding.mapView.controller
        controller.setZoom(15)

        // Create a GeoPoint (latitude, longitude)
        val geoPoint = GeoPoint(lat, long)
        controller.setCenter(geoPoint)

        // Add a marker at the GeoPoint
        val marker = Marker(binding.mapView)
        marker.position = geoPoint
        marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
        marker.title = "Destination"
        binding.mapView.overlays.add(marker)

//        // Initialize OSMDroid configuration
//        Configuration.getInstance().load(this, applicationContext.getSharedPreferences("osmdroid", MODE_PRIVATE))
//        // Initialize the MapView
//        binding.mapView.setMultiTouchControls(true)
//        binding.mapView.controller.setZoom(15.0)

//        // Observe live location updates
//        locationViewModel.location.observe(this, Observer { location ->
//            updateMap(location.latitude, location.longitude)
//        })
//
//        // Check location permissions
//        if (hasLocationPermission()) {
//            locationViewModel.startLocationUpdates()
//        } else {
//            requestLocationPermission()
//        }
    }


    private fun startDeliveryGuyLocationTracking() {
        locationViewModel.startLocationUpdates()

        // Observe the delivery guy's location (live tracking)
        locationViewModel.userLocation.observe(this, Observer { location ->
            location?.let {
                // Update the marker on the map
                marker = Marker(binding.mapView)
                marker!!.position = org.osmdroid.util.GeoPoint(it.latitude, it.longitude)
                marker!!.title = "Delivery Guy"
                binding.mapView.overlays.add(marker)


                // Move map to user's position
                val controller: IMapController = binding.mapView.controller
                controller.setCenter(marker!!.position)
            }
        })
    }

    private fun fetchClientLocation(userId: String) {
        // For Clients: Fetch location from Firestore
        locationViewModel.fetchOtherUserLocation(userId).observe(this, Observer { location ->
            location?.let {
//                locationTextView.text = "Client Location: ${it.latitude}, ${it.longitude}"

                // You can display a marker or do something else with the location
                // If you want to show it on the map as well, you can follow the same logic as for the Delivery Guy
            }
        })
    }

    private fun hasLocationPermission(): Boolean {
        return ActivityCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun requestLocationPermission() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
            LOCATION_PERMISSION_REQUEST_CODE
        )
    }

    //
//    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
//        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
//            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                locationViewModel.startLocationUpdates()
//            }
//        }
//    }
//
//    private fun updateMap(latitude: Double, longitude: Double) {
//        val geoPoint = GeoPoint(latitude, longitude)
//        binding.mapView.controller.setCenter(geoPoint)
//
//        // Add or update the user marker
//        if (userMarker == null) {
//            userMarker = Marker(binding.mapView).apply {
//                position = geoPoint
//                setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
//                binding.mapView.overlays.add(this)
//            }
//        } else {
//            userMarker?.position = geoPoint
//        }
//
//        binding.mapView.invalidate() // Refresh the map
//    }
//
//    override fun onDestroy() {
//        super.onDestroy()
//        locationViewModel.stopLocationUpdates()
//    }
//
    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1001
    }

}
