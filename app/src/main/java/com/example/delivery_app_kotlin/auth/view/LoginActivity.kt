package com.example.delivery_app_kotlin.auth.view

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.*
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.delivery_app_kotlin.MainActivity
import com.example.delivery_app_kotlin.auth.viewmodel.AuthViewModel
import com.example.delivery_app_kotlin.databinding.ActivityLoginBinding
import com.google.firebase.FirebaseApp

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
//    private val authViewModel: AuthViewModel by viewModels()
    private lateinit var authViewModel: AuthViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        authViewModel = ViewModelProvider(this)[AuthViewModel::class.java]

        setupObservers()
        setupClickListeners()
    }

    private fun setupObservers() {
        authViewModel.authState.observe(this, Observer { state ->
            when (state) {
                is AuthViewModel.AuthState.Idle -> {
                    // No action needed
                }
                is AuthViewModel.AuthState.Success -> {
                    Toast.makeText(this, state.message, Toast.LENGTH_SHORT).show()
                    // Navigate to the main screen
                    startActivity(Intent(this, MainActivity::class.java))
                }
                is AuthViewModel.AuthState.Error -> {
                    Toast.makeText(this, state.errorMessage, Toast.LENGTH_SHORT).show()
                }
            }
        })
    }

    private fun setupClickListeners() {
        binding.loginButton.setOnClickListener {
            val email = binding.emailEditText.text.toString()
            val password = binding.passwordEditText.text.toString()

            if (email.isNotEmpty() && password.isNotEmpty()) {
                authViewModel.login(email, password)
            } else {
                Toast.makeText(this, "Please enter email and password", Toast.LENGTH_SHORT).show()
            }
        }

        binding.registerTextView.setOnClickListener {
            // Navigate to RegisterActivity
            startActivity(Intent(this, RegisterActivity::class.java))
        }
    }
}
