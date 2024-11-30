package com.example.delivery_app_kotlin.auth.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class AuthViewModel : ViewModel() {

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()

    // LiveData for authentication state
    private val _authState = MutableLiveData<AuthState>()
    val authState: LiveData<AuthState> get() = _authState

    fun login(email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    _authState.value = AuthState.Success("Login successful")
                } else {
                    _authState.value = AuthState.Error(task.exception?.message ?: "Login failed")
                }
            }
    }

    fun register(email: String, password: String, userType: String) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val userId = auth.currentUser?.uid
                    if (userId != null) {
                        saveUserDetails(userId, email, userType)
                    }
                    _authState.value = AuthState.Success("Registration successful")
                } else {
                    _authState.value = AuthState.Error(task.exception?.message ?: "Registration failed")
                }
            }
    }

    private fun saveUserDetails(userId: String, email: String, userType: String) {
        val user = hashMapOf(
            "userId" to userId,
            "email" to email,
            "userType" to userType,
            "createdAt" to System.currentTimeMillis()
        )

        firestore.collection("users").document(userId).set(user)
            .addOnSuccessListener {
                _authState.value = AuthState.Success("Registration successful")
            }
            .addOnFailureListener { exception ->
                _authState.value = AuthState.Error(exception.message ?: "Failed to save user details")
            }
    }

    fun checkUserLoggedIn() {
        if (auth.currentUser != null) {
            _authState.value = AuthState.Success("User already logged in")
        } else {
            _authState.value = AuthState.Idle
        }
    }

    sealed class AuthState {
        object Idle : AuthState()
        data class Success(val message: String) : AuthState()
        data class Error(val errorMessage: String) : AuthState()
    }
}
