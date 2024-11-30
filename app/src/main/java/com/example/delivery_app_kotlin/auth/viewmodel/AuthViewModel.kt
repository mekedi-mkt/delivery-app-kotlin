package com.example.delivery_app_kotlin.auth.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.delivery_app_kotlin.auth.model.entities.UserModel
import com.example.delivery_app_kotlin.auth.model.repo.UserRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch

class AuthViewModel : ViewModel() {

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
    val userRepository = UserRepository(firestore)

    // LiveData for authentication state
    private val _authState = MutableLiveData<AuthState>()
    val authState: LiveData<AuthState> get() = _authState

    // LiveData for User Model
    private val _user = MutableLiveData<UserModel?>()
    val user: LiveData<UserModel?> get() = _user

    fun fetchUserData() {
        viewModelScope.launch {
            try {
                val userId = auth.currentUser?.uid
                val userData = userRepository.fetchUserData(userId!!)
                _user.value = userData // Store user data in ViewModel
            } catch (e: Exception) {
//                _error.value = e.message
            }
        }
    }

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

    fun register(name: String, email: String, password: String, userType: String) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val userId = auth.currentUser?.uid
                    if (userId != null) {
                        saveUserDetails(userId, name, email, userType)
                    }
                    _authState.value = AuthState.Success("Registration successful")
                } else {
                    _authState.value =
                        AuthState.Error(task.exception?.message ?: "Registration failed")
                }
            }
    }

    private fun saveUserDetails(userId: String, name: String, email: String, userType: String) {
        val user = hashMapOf<String, Any>(
            "userId" to userId,
            "name" to name,
            "email" to email,
            "userType" to userType
        )

        firestore.collection("users").document(userId).set(user)
            .addOnSuccessListener {
                _authState.value = AuthState.Success("Registration successful")
            }
            .addOnFailureListener { exception ->
                _authState.value =
                    AuthState.Error(exception.message ?: "Failed to save user details")
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
