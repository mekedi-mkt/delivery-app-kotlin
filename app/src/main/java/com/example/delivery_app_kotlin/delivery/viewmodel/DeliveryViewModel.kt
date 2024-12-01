package com.example.delivery_app_kotlin.delivery.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.delivery_app_kotlin.auth.model.entities.UserModel
import com.example.delivery_app_kotlin.delivery.model.entities.Delivery
import com.example.delivery_app_kotlin.delivery.model.repo.DeliveryRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch

class DeliveryViewModel : ViewModel() {
    // Store the selected delivery guy
    private val _selectedDG = MutableLiveData<UserModel?>()
    val selectedDG: LiveData<UserModel?> get() = _selectedDG

    // Store the delivery
    private val _delivery = MutableLiveData<Delivery?>()
    val delivery: LiveData<Delivery?> get() = _delivery

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
    private val deliveryRepo = DeliveryRepository(firestore)

    fun setSelectedDG(dg: UserModel) {
        _selectedDG.value = dg
    }

    fun addNewDelivery(pickup: String, destination: String) {
        val clientId = auth.currentUser!!.uid
        val delivery = Delivery(null, clientId, _selectedDG.value!!.userId, pickup, destination)
        viewModelScope.launch {
            deliveryRepo.saveDeliveryToFirestore(delivery)
//        _selectedDG.value = null
            _delivery.value = delivery

        }
    }

    fun getDelivery() {
        viewModelScope.launch {
            val clientId = auth.currentUser!!.uid
            val currentDelivery = deliveryRepo.fetchDeliveries().filter { delivery ->
                delivery.clientId == clientId
            }
            if (currentDelivery.isNotEmpty())
                _delivery.value = currentDelivery[0]

        }
    }

    fun cancelDelivery() {
        viewModelScope.launch {
            deliveryRepo.cancelDelivery(delivery.value!!.id!!)
            _selectedDG.value = null
            _delivery.value = null
        }
    }
}