package com.example.delivery_app_kotlin.delivery.model.entities

import com.google.firebase.firestore.GeoPoint

data class Delivery(
    val id: String? = null,
    val clientId: String = "",
    val deliveryGuyId: String = "",
    val pickup: String = "",
    val destination: String = "",
    val destLocation : GeoPoint = GeoPoint(0.0, 0.0)
)
