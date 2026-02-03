package com.example.fefumarket.data.models

// Модель ответа сервера при успешной авторизации пользователя,
// содержит токен и тип токена для дальнейших защищённых запросов
data class LoginResponse(
    val access_token: String,
    val token_type: String = "bearer"
)