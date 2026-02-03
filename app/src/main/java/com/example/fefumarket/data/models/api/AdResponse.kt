package com.example.fefumarket.data.models.api

// Модель данных объявления, получаемая напрямую из API сервера
data class AdResponse(
    val id: Int,
    val title: String,
    val price: Double,
    val category: String,
    val condition: String,
    val building: String,
    val description: String?,
    val owner_id: Int,
    val created_at: String,
    val images: List<String> = emptyList() // ссылки на картинки
)