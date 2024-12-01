package com.example.delivery_app_kotlin.delivery.model.repo

import com.example.delivery_app_kotlin.delivery.model.entities.Delivery
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class DeliveryRepository(private val firestore: FirebaseFirestore) {
    fun saveDeliveryToFirestore(delivery: Delivery) {
        val deliveriesCollection = firestore.collection("deliveries")

        // Save the delivery object to Firestore
        deliveriesCollection.add(delivery)
            .addOnSuccessListener {
                // Delivery saved successfully
                // Toast.makeText(this, "Delivery added!", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { e ->
                // Handle the error
                throw Exception("Error saving delivery: ${e.message}")
            }
    }

    suspend fun fetchDeliveries(): List<Delivery> {
        return try {
            val querySnapshot = firestore.collection("deliveries").get().await()

            val deliveries = querySnapshot.documents.mapNotNull { document ->
                val delivery = document.toObject(Delivery::class.java)
                delivery?.copy(id = document.id) ?: delivery
            }

            deliveries
        } catch (e: Exception) {
            throw Exception("Failed to fetch delivery data: ${e.message}")
        }
    }

    suspend fun cancelDelivery(deliveryId: String): Boolean {
        return try {
            val deliveryRef = firestore.collection("deliveries").document(deliveryId)
            deliveryRef.delete().await()
            true
        } catch (e: Exception) {
            throw Exception("Failed to cancelling delivery: ${e.message}")
            false
        }
    }
}