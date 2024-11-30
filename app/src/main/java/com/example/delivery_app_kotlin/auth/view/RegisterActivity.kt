package com.example.delivery_app_kotlin.auth.view

import android.content.Intent
import android.os.Bundle
import android.widget.RadioButton
import android.widget.Toast
import androidx.activity.*
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.delivery_app_kotlin.R
import com.example.delivery_app_kotlin.auth.viewmodel.AuthViewModel
import com.example.delivery_app_kotlin.databinding.ActivityRegisterBinding
import com.google.firebase.FirebaseApp

class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding

    //    private val authViewModel: AuthViewModel by viewModels()
    private lateinit var authViewModel: AuthViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
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
                    // Navigate back to LoginActivity
                    finish()
                }

                is AuthViewModel.AuthState.Error -> {
                    Toast.makeText(this, state.errorMessage, Toast.LENGTH_SHORT).show()
                }
            }
        })
    }

    private fun setupClickListeners() {
        binding.registerButton.setOnClickListener {
            val name = binding.editTextName.text.toString()
            val email = binding.emailEditText.text.toString()
            val password = binding.passwordEditText.text.toString()
            val selectedId = binding.userRadioGroup.checkedRadioButtonId

            if (email.isNotEmpty() && password.isNotEmpty() && selectedId != -1) {
                val selectedRadioButton = findViewById<RadioButton>(selectedId)
                val userType = selectedRadioButton.text.toString()
                authViewModel.register(name, email, password, userType)
            } else if (selectedId == -1) {
                Toast.makeText(this, "Please choose user type", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Please enter email and password", Toast.LENGTH_SHORT).show()
            }
        }

        binding.loginTextView.setOnClickListener {
            // Navigate to RegisterActivity
            startActivity(Intent(this, LoginActivity::class.java))
        }
    }
}
