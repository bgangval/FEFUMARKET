package com.example.fefumarket.data.models.api

// Модель ответа сервера для одного продукта/объявления
data class ProductOut(
    val id: Int,
    val title: String,
    val price: Double,
    val category: String,
    val condition: String,
    val building: String,
    val description: String?,
    val owner_id: Int,
    val created_at: String
)
