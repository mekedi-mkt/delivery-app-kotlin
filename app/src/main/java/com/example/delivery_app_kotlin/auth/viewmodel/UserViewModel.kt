package com.example.delivery_app_kotlin.auth.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.delivery_app_kotlin.auth.model.entities.UserModel
import com.example.delivery_app_kotlin.auth.model.repo.UserRepository
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch

class UserViewModel : ViewModel() {

    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
    val userRepository = UserRepository(firestore)

    private val _deliveryGuys = MutableLiveData<List<UserModel>>()
    val deliveryGuys: LiveData<List<UserModel>> get() = _deliveryGuys

    private val _clients = MutableLiveData<List<UserModel>>()
    val clients: LiveData<List<UserModel>> get() = _clients

     fun getDeliveryGuys() {
        viewModelScope.launch {
            _deliveryGuys.value = userRepository.fetchDeliveryGuys()
        }
    }

    fun getClients() {
        viewModelScope.launch {
            _clients.value = userRepository.fetchClients()
        }
    }
}