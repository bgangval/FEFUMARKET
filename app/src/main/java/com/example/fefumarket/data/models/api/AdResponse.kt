package com.example.fefumarket.data.models.api

// Модель данных объявления, получаемая напрямую из API сервера
data class AdResponse(
    val id: Int,
    val title: String,
    val description: String,
    val price: Int,
    val owner_id: Int,
    val created_at: String
)