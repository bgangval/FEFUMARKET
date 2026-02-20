package com.example.fefumarket.data.models.api

// Модель ответа сервера для пользователя
data class UserOut(
    val id: Int,
    val email: String,
    val name: String,
    val avatar_url: String?,
    val is_admin: Boolean
)
