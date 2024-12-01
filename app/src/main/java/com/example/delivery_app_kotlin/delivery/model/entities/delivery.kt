package com.example.delivery_app_kotlin.delivery.model.entities

data class Delivery(
    val id: String = "",
    val clientId: String = "",
    val deliveryGuyId: String = "",
    val pickup: String = "",
    val destination: String = ""
)
