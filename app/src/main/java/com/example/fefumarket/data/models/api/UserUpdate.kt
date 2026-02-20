package com.example.fefumarket.data.models.api

// Модель запроса для обновления пользователя (все поля опциональные)
data class UserUpdate(
    val name: String? = null,
    val avatar_url: String? = null
)
