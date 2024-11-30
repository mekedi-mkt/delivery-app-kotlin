package com.example.delivery_app_kotlin.main.view

import android.os.Bundle
import android.view.View
import android.view.View.VISIBLE
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.delivery_app_kotlin.R
import com.example.delivery_app_kotlin.auth.viewmodel.AuthViewModel
import com.example.delivery_app_kotlin.databinding.ActivityLoginBinding
import com.example.delivery_app_kotlin.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    //    private val authViewModel: AuthViewModel by viewModels()
    private lateinit var authViewModel: AuthViewModel

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
                    binding.button.visibility = VISIBLE
                }
            }
        }
        authViewModel.fetchUserData()
    }

}
