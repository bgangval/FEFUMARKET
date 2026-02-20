package com.example.fefumarket.data.models.api

// Модель запроса для создания нового продукта/объявления
data class ProductCreate(
    val title: String,
    val price: Double,
    val category: String,
    val condition: String,
    val building: String,
    val description: String? = null
)
