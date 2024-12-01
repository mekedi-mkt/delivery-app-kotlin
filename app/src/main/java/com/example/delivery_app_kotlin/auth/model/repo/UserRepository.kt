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

    suspend fun fetchUsers(): List<UserModel> {
        return try {
            val querySnapshot = firestore.collection("users").get().await()

            // Convert the documents into a list of User objects
            val users = querySnapshot.documents.mapNotNull { document ->
                document.toObject(UserModel::class.java)
            }

            users
        } catch (e: Exception) {
            throw Exception("Failed to fetch user data: ${e.message}")
        }
    }

    suspend fun fetchDeliveryGuys(): List<UserModel> {
        return try {
            val users = fetchUsers()
            return users.filter { user ->
                user.userType == "Delivery Guy"
            }
        } catch (e: Exception) {
            throw Exception("Failed to fetch delivery guys: ${e.message}")
        }
    }

    suspend fun fetchClients(): List<UserModel> {
        return try {
            val users = fetchUsers()
            return users.filter { user ->
                user.userType == "Client"
            }
        } catch (e: Exception) {
            throw Exception("Failed to fetch clients: ${e.message}")
        }
    }
}
