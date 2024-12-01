package com.example.delivery_app_kotlin.main.view

import android.content.Intent
import android.os.Bundle
import android.view.View.GONE
import android.view.View.VISIBLE
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.delivery_app_kotlin.auth.viewmodel.AuthViewModel
import com.example.delivery_app_kotlin.auth.viewmodel.UserViewModel
import com.example.delivery_app_kotlin.databinding.ActivityMainBinding
import com.example.delivery_app_kotlin.delivery.view.NewDeliveryActivity
import com.example.delivery_app_kotlin.delivery.viewmodel.DeliveryViewModel

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    //    private val authViewModel: AuthViewModel by viewModels()
    private lateinit var authViewModel: AuthViewModel
    private lateinit var deliveryViewModel: DeliveryViewModel
    private lateinit var userViewModel: UserViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        authViewModel = ViewModelProvider(this)[AuthViewModel::class.java]
        // Observe user data
        authViewModel.user.observe(this) { user ->
            if (user != null) {
                binding.userTextView.text = "${user.name} (${user.userType})"
                if (user.userType == "Client") {
                    binding.buttonAddNew.visibility = VISIBLE
                }
            }
        }
        authViewModel.fetchUserData()

        deliveryViewModel = ViewModelProvider(this)[DeliveryViewModel::class.java]
        userViewModel = ViewModelProvider(this)[UserViewModel::class.java]
        // Observe delivery data
        observeDeliveryData()
        deliveryViewModel.selectedDG.observe(this) {
            if (it != null) {
                binding.textDeliveryGuy.text = it.name
            }
        }
        userViewModel.deliveryGuys.observe(this) {
            if (it != null && it.isNotEmpty()) {
                val selectedDGs =
                    it.filter { dg -> dg.userId == deliveryViewModel.delivery.value?.deliveryGuyId }
                if (selectedDGs.isNotEmpty())
                    binding.textDeliveryGuy.text = selectedDGs[0].name
            }
        }
        deliveryViewModel.getDelivery()

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
        deliveryViewModel.getDelivery()
    }

    private fun observeDeliveryData() {
        deliveryViewModel.delivery.observe(this) { delivery ->
            if (delivery != null) {
                binding.textPickup.text = delivery.pickup
                binding.textDest.text = delivery.destination

                if (deliveryViewModel.selectedDG.value == null) {
                    userViewModel.getDeliveryGuys()
                    val dg =
                        userViewModel.deliveryGuys.value?.filter { dg -> dg.userId == delivery.deliveryGuyId }
                    if (dg != null)
                        binding.textDeliveryGuy.text = dg[0].name
                } else
                    binding.textDeliveryGuy.text = deliveryViewModel.selectedDG.value?.name


                binding.noDeliveryLayout.visibility = GONE
                binding.currentDeliveryLayout.visibility = VISIBLE

                // Location Tracking
//                initializeLocationTracking()

            } else {
                binding.noDeliveryLayout.visibility = VISIBLE
                binding.currentDeliveryLayout.visibility = GONE
            }
        }
    }

}
