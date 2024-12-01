package com.example.delivery_app_kotlin.delivery.view

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.delivery_app_kotlin.auth.viewmodel.UserViewModel
import com.example.delivery_app_kotlin.databinding.ActivityNewDeliveryBinding
import com.example.delivery_app_kotlin.delivery.viewmodel.DeliveryViewModel
import com.example.delivery_app_kotlin.main.viewmodel.LocationViewModel

class NewDeliveryActivity : AppCompatActivity() {
    private lateinit var binding: ActivityNewDeliveryBinding

    private lateinit var userViewModel: UserViewModel
    private lateinit var deliveryViewModel: DeliveryViewModel

    private lateinit var newDeliveryAdapter: NewDeliveryAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNewDeliveryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        userViewModel = ViewModelProvider(this)[UserViewModel::class.java]
        deliveryViewModel = ViewModelProvider(this)[DeliveryViewModel::class.java]

        // Fetch Delivery Guys
        userViewModel.getDeliveryGuys()

        // Setup RecyclerView
        setUpRecyclerView()

        // Back Button
        binding.iconButton.setOnClickListener {
            // Navigate to MainActivity without saving
            finish()
        }

        // Add New Delivery Button
        binding.addDeliveryButton.setOnClickListener {
            val pickup = binding.editTextPickup.text.toString()
            val dest = binding.editTextDest.text.toString()
            if (pickup.isNotEmpty() && dest.isNotEmpty() &&
                deliveryViewModel.selectedDG.value != null
            ) {
//                val locationViewModel = ViewModelProvider(this)[LocationViewModel::class.java]
//                val userLocation = locationViewModel.userLocation.value
                deliveryViewModel.addNewDelivery(applicationContext, pickup, dest)
            } else {
                Toast.makeText(this, "Please fill out the details", Toast.LENGTH_SHORT).show()
            }
        }

        deliveryViewModel.delivery.observe(this) {
            if (it != null) {
                Toast.makeText(this, "Successfully created new delivery!", Toast.LENGTH_SHORT).show()
                finish()
            }
        }
    }

    private fun setUpRecyclerView() {
        newDeliveryAdapter = NewDeliveryAdapter(deliveryViewModel)
        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            setHasFixedSize(true)
            adapter = newDeliveryAdapter
        }

        userViewModel.deliveryGuys.observe(this) {
            newDeliveryAdapter.differ.submitList(it)
        }
    }
}