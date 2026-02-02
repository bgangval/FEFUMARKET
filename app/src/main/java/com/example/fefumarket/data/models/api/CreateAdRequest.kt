package com.example.fefumarket.data.models.api

// Модель запроса для создания нового объявления на сервере
data class CreateAdRequest(
    val title: String,
    val description: String,
    val price: Int
)