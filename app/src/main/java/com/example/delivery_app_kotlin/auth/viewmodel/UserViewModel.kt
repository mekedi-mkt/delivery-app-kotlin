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

    // LiveData for authentication state
    private val _deliveryGuys = MutableLiveData<List<UserModel>>()
    val deliveryGuys: LiveData<List<UserModel>> get() = _deliveryGuys

     fun getDeliveryGuys() {
        viewModelScope.launch {
            _deliveryGuys.value = userRepository.fetchDeliveryGuys()
        }
    }
}