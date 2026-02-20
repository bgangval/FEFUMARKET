package com.example.fefumarket.data.models.api

// Модель ответа сервера для сообщения в чате
data class MessageOut(
    val id: Int,
    val sender_id: Int,
    val text: String,
    val created_at: String
)
