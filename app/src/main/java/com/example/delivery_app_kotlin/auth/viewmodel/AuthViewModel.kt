package com.example.delivery_app_kotlin.auth.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth

class AuthViewModel : ViewModel() {

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

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

    fun register(email: String, password: String) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    _authState.value = AuthState.Success("Registration successful")
                } else {
                    _authState.value = AuthState.Error(task.exception?.message ?: "Registration failed")
                }
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
