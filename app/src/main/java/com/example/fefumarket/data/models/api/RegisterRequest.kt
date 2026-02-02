package com.example.fefumarket.data.models.api

// Модель запроса для регистрации пользователя на сервере
data class RegisterRequest(
    val email: String,
    val password: String
)