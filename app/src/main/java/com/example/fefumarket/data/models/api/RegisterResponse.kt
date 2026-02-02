package com.example.fefumarket.data.models.api

// Модель ответа сервера при успешной регистрации пользователя,
// содержит токен для дальнейшей авторизации запросов
data class RegisterResponse(
    val access_token: String,
    val token_type: String
)