package com.example.fefumarket.data.models

// Модель запроса для авторизации пользователя на сервере

data class LoginRequest(
    val email: String,
    val password: String
)