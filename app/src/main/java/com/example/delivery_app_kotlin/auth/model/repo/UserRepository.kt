package com.example.delivery_app_kotlin.auth.model.repo

import com.example.delivery_app_kotlin.auth.model.entities.UserModel
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class UserRepository(private val firestore: FirebaseFirestore) {

    suspend fun fetchUserData(userId: String): UserModel {
        return try {
            val document = firestore.collection("users").document(userId).get().await()
            document.toObject(UserModel::class.java) ?: throw Exception("User not found")
        } catch (e: Exception) {
            throw Exception("Failed to fetch user data: ${e.message}")
        }
    }
}
