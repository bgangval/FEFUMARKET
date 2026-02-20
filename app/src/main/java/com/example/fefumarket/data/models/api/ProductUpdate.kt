package com.example.fefumarket.data.models.api

// Модель запроса для обновления продукта/объявления (все поля опциональные)
data class ProductUpdate(
    val title: String? = null,
    val price: Double? = null,
    val category: String? = null,
    val condition: String? = null,
    val building: String? = null,
    val description: String? = null
)
